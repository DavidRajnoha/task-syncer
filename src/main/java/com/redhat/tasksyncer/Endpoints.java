package com.redhat.tasksyncer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.tasksyncer.dao.accessors.ProjectAccessor;
import com.redhat.tasksyncer.dao.accessors.RepositoryAccessor;
import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import com.redhat.tasksyncer.dao.repositories.*;
import com.redhat.tasksyncer.decoders.AbstractWebhookIssueDecoder;

import com.redhat.tasksyncer.exceptions.*;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Filip Cap, David Rajnoha
 */
@RestController
@PropertySource("classpath:other.properties")
@ComponentScan(basePackages = "com.redhat.tasksyncer.dao.entities")
public class Endpoints {
    public static final String OK = "OK";

    @Value("${trello.appKey}")
    private String trelloApplicationKey;

    @Value("${trello.token}")
    private String trelloAccessToken;

    @Value("${githubWebhookURL}")
    private String githubWebhookURLString;

    @Value("${gitlabWebhookURL}")
    private String gitlabWebhookURLString;

    @Autowired
    private AbstractIssueRepository issueRepository;

    @Autowired
    private AbstractRepositoryRepository repositoryRepository;

    @Autowired
    private ProjectRepository projectRepository;


    private ProjectAccessor projectAccessor;
    private Map<String, RepositoryAccessor> repositoryAccessors;
    private Map<String, AbstractWebhookIssueDecoder> webhookIssueDecoderMap;


    public Endpoints(Map<String, RepositoryAccessor> repositoryAccessors, ProjectAccessor projectAccessor,
                     Map<String, AbstractWebhookIssueDecoder> webhookIssueDecoderMap) {
        this.repositoryAccessors = repositoryAccessors;
        this.projectAccessor = projectAccessor;
        this.webhookIssueDecoderMap = webhookIssueDecoderMap;
    }

    /**
     * When creating a trello endpoint via webhook, trello sends a HEAD request and is avaitong 200 response that would not come
     * from POST Endpoint
     * */
    @RequestMapping(path = "/service/{serviceName}/project/{projectName}/hook",
            method = {RequestMethod.GET}
    ) public ResponseEntity<String> yesTrelloThisEndpointWorks(){
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    /**
     *  Endpoint for processing webhooks
     * @param serviceName - name of the service you are trying to connect
     *                    Available values: "gitlab", "github, "jira"
     * @param projectName - name of the project in this app you want to have your webhooks send
     * */
    @RequestMapping(path = "/service/{serviceName}/project/{projectName}/hook",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            method = {RequestMethod.POST}
    )
    public ResponseEntity<String> hookEndpoint(@PathVariable String serviceName,
            @PathVariable String projectName,
                       HttpServletRequest request
    )  {
       return processHook(projectName, request, serviceName);

    }

    //processing received webhook
    //TODO: Move from endpoints
    private ResponseEntity<String> processHook(String projectName, HttpServletRequest request, String serviceType
    ) {
        // if project the hook is pointed at does not exists, throws error
        // TODO: Swap error for HTTP response
        Project project = projectRepository.findProjectByName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project with name does not exist"));

        AbstractWebhookIssueDecoder webhookIssueDecoder;

        try {
            webhookIssueDecoder = findWebhookIssueDecoder(serviceType);
        } catch (RepositoryTypeNotSupportedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        try {
            // converts webhook request to the abstractIssue

            AbstractIssue newIssue = webhookIssueDecoder.decode(request, project);

            // syncs the updated issue with the local database
            projectAccessor.saveAndInitialize(project);
            projectAccessor.syncIssue(newIssue);
        } catch (TrelloCalllbackNotAboutCardException  ignored) {
        } catch (InvalidWebhookCallbackException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Webhook processed");
    }


    private AbstractWebhookIssueDecoder findWebhookIssueDecoder(String serviceName) throws RepositoryTypeNotSupportedException {
        String serviceType = serviceName.toLowerCase().concat("WebhookIssueDecoder");
        AbstractWebhookIssueDecoder webhookIssueDecoder = webhookIssueDecoderMap.get(serviceType);
        if (webhookIssueDecoder == null){
            throw new RepositoryTypeNotSupportedException("Service type: " + serviceType + " not supported for processing" +
                    "webhooks ");
        }
        return webhookIssueDecoder;
    }


    /**
     * Connects or connects and hooks the repository from external service with existing project in tasksyncer
     *
     * @param firstLoginCredential Trello - app key; Jira - email@adress; Github - email@adress; GitLab - instance URL
     * @param secondLoginCredential Trello - token; Jira - API token; Github - password; Gitlab - AuthKey
     * @param serviceName supported values: trello, jira, github, gitlab\
     * @param projectName name of the project you are trying to connect external service to
     * @param hookOrConnect supported values: hook - you wish to connect to the service and create webhook there pointing
     *                                               at this app
     *                                        connect - you wish to connect to the service but not create a webhook
     * @param repoNamespace Trello - random value, namespace not required
     *                      Jira - namespace of your account, NAMESPACE.atlassian.net
     *                      Github - name of your account, github.com/NAME/repositoryName
     *                      Gitlab - gitlab namespace
     * @param repoName Trello - full id of the board you wish to connect, can be found at trello.com/your/board/url.json
     *                 Jira - project key - the three or two letter shortcut of the project name written in capital letters
     *                 Github - repository name, github.com/namespace/REPOSITORY_NAME
     *                 Gitlab - project name
     *
     *
     * @return Http ResponseEntity; 400 when serviceName is not valid; 503 when there was an error in communication with
     * external service
     * */
    @RequestMapping(path = "/service/{serviceName}/project/{projectName}/{hookOrConnect}/{repoNamespace}/{repoName}",
            method = RequestMethod.PUT
    )    public ResponseEntity<String> connectService(@PathVariable String serviceName,
                                                      @PathVariable String projectName,
                                                      @PathVariable String hookOrConnect,
                                                      @PathVariable String repoNamespace,
                                                      @PathVariable String repoName,
                                                      @RequestParam("firstLoginCredential") String firstLoginCredential,
                                                      @RequestParam("secondLoginCredential") String secondLoginCredential) {
        // Project with projectName must be already created
        Project project = projectRepository.findProjectByName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project with name does not exist"));

        //Initialize a projectAccessor - adds a project to the accessor and saves the project
        projectAccessor.saveAndInitialize(project);

        RepositoryAccessor repositoryAccessor;
        try {
            repositoryAccessor = findRepositoryAccessor(serviceName);
        } catch (RepositoryTypeNotSupportedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        AbstractRepository repository;

        // Creates a repository of the serviceName type
        repository = repositoryAccessor.createRepository(firstLoginCredential, secondLoginCredential, repoName, repoNamespace);

        try {
            // Adds the repository to the project and syncs the remoteIssues with local database
            repositoryAccessor = projectAccessor.addRepository(repository);
        } catch (CannotConnectToRepositoryException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Could not connect to the " + serviceName +
                    "web service");
        } catch (RepositoryTypeNotSupportedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Repository of service of type: " + serviceName +
                     " is not implemented");
        }



        // Deciding if create hook or not based on the query parameters
        switch (hookOrConnect){
            case "hook":
                //TODO: let the repositoryAccessors get the hook somewhere else
                try {
                // creates webhook pointing to local adress
                projectAccessor.hookRepository(repository, githubWebhookURLString.replace("{projectName}", projectName));
                } catch (IOException |
                        GitLabApiException e) {
                    e.printStackTrace();
                    repositoryAccessor.deleteRepository(repository);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create the webhook," +
                            " check if the webhook is not already created");
                } catch (RepositoryTypeNotSupportedException e){
                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Creation of webhook for the service: " +
                            serviceName + "is not supported");
                } catch (SynchronizationFailedException e){
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Connecting to the repository" +
                            "failed");
                } catch (CannotConnectToRepositoryException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Could not connect to the " + serviceName +
                            "web service");
                }

                break;
            case "connect":
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid action \n Valid parameters are \"connect\" and \"hook\"");
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("");
    }


    //CREATE PROJECT
    /**
     * Creates a new project in tasksyncer and connects it with the service repositoru/project (syncs all the issues, webhook
     * is not created automatically)
     *
     * @param firstLoginCredential Trello - app key; Jira - email@adress; Github - email@adress; GitLab - instance URL
     * @param secondLoginCredential Trello - token; Jira - API token; Github - password; Gitlab - AuthKey
     * @param serviceName supported values: trello, jira, github, gitlab\
     * @param projectName name of the project you are trying to connect external service to
     * @param repoNamespace Trello - random value, namespace not required
     *                      Jira - namespace of your account, NAMESPACE.atlassian.net
     *                      Github - name of your account, github.com/NAME/repositoryName
     *                      Gitlab - gitlab namespace
     * @param repoName Trello - full id of the board you wish to connect, can be found at trello.com/your/board/url.json
     *                 Jira - project key - the three or two letter shortcut of the project name written in capital letters
     *                 Github - repository name, github.com/namespace/REPOSITORY_NAME
     *                 Gitlab - project name
     * @param boardName Name of the board that will be created at trello
     *
     * @return Http ResponseEntity; 400 when serviceName is not valid; 503 when there was an error in communication with
     * external service
     * */
    @RequestMapping(path = "/service/{serviceName}/new/project/{projectName}/{repoNamespace}/{repoName}/to/trello/{boardName}",
                    method = RequestMethod.PUT
    )
    public ResponseEntity<String> createProjectEndpoint(@PathVariable String projectName,
                                                @PathVariable String serviceName,
                                                @PathVariable String repoNamespace,
                                                @PathVariable String repoName,
                                                @PathVariable String boardName,
                                                @RequestParam("firstLoginCredential") String firstLoginCredential,
                                                @RequestParam("secondLoginCredential") String secondLoginCredential
    )  {
        return createProject(projectName, serviceName, repoNamespace, repoName, boardName, firstLoginCredential, secondLoginCredential, true);
    }


    //TODO: rename, move out of endpoints and reconsider
    public ResponseEntity<String> createProject(String projectName, String serviceType, String repoNamespace, String repoName,
                                                String boardName, String firstLoginCredential, String secondLoginCredential,
                                                Boolean trello) {
        if (projectRepository.findProjectByName(projectName).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Project with name " + projectName + "Already Exists");
        }

        // creates a new project
        Project project = new Project();
        project.setName(projectName);

        RepositoryAccessor repositoryAccessor;
        AbstractRepository repository;

        projectAccessor.saveAndInitialize(project);


        try {
            repositoryAccessor = findRepositoryAccessor(serviceType);

        } catch (RepositoryTypeNotSupportedException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Service of type: " + serviceType + " is not yet " +
                    "supported");
        }
        repository = repositoryAccessor.createRepository(firstLoginCredential, secondLoginCredential, repoName, repoNamespace);



        // based on the trello parameter decides if create trello board or not
        if (trello) {
            try {
                projectAccessor.initialize(TrelloCard.class.getName(), boardName);
            } catch (HttpClientErrorException e){
                projectAccessor.deleteProject(project);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authentication with Trello failed");
            }
        }

        // adds repository to the project and syncs the remote issues with the local database
        try {
            projectAccessor.addRepository(repository);
        } catch (CannotConnectToRepositoryException e) {
            revertCreation(project);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't connect to the external service");
        } catch (RepositoryTypeNotSupportedException e) {
            e.printStackTrace();
        }

        // saves the project
        projectAccessor.save(); // todo: make it transactional

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("OK");
    }



    private RepositoryAccessor findRepositoryAccessor(String serviceName) throws RepositoryTypeNotSupportedException {
        String serviceType = serviceName.toLowerCase().concat("RepositoryAccessor");
        RepositoryAccessor repositoryAccessor = repositoryAccessors.get(serviceType);
        if (repositoryAccessor == null){
            throw new RepositoryTypeNotSupportedException("Repo type: " + serviceType + " not supported");
        }

        return repositoryAccessor;
    }

    /**
     * deletes board from trello and project from tasksyncer, should clean changes in case of an error during creation of
     * the project
     * TODO: assert functionality and test thoroughly
     * */
    public void revertCreation(Project project){
        try {
            projectAccessor.deleteBoard(trelloApplicationKey, trelloAccessToken);
        } catch (CannotConnectToRepositoryException e) {
            e.printStackTrace();
        }
        projectAccessor.deleteProject(project);
    }









    //GET REQUESTS

    @RequestMapping(path = "/project/{projectName}/all/{issueType}",
                    method = RequestMethod.GET
    )
    public String getIssuesByType(@PathVariable String projectName,
                                  @PathVariable IssueType issueType) throws JsonProcessingException {
        Set<AbstractIssue> issues = issueRepository.findByIssueType(issueType);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(issues);
    }

    @RequestMapping(path = "/project/{projectName}/all",
            method = RequestMethod.GET
    )
    public String getAllIssues(@PathVariable String projectName) throws JsonProcessingException {
        List<AbstractIssue> issues = (List<AbstractIssue>) issueRepository.findAll();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(issues);
    }

    @RequestMapping(path = "/project/{projectName}/repositories",
            method = RequestMethod.GET
    )
    public String getRepositoriesByProject(@PathVariable String projectName) throws JsonProcessingException {
        List<AbstractRepository> repositories = repositoryRepository.findByProject_Name(projectName);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(repositories);
    }

    @RequestMapping(path = "/project/{projectName}/{repositoryName}/{remoteIssueId}",
            method = RequestMethod.GET
    )
    public String getIssueByTypeAndRepositoryAndId(@PathVariable String projectName, @PathVariable String repositoryName,
                                                   @PathVariable String remoteIssueId) throws JsonProcessingException {
        List<AbstractIssue> issues = issueRepository
                .findByRepository_Project_nameAndRemoteIssueIdAndRepository_repositoryName(projectName, remoteIssueId, repositoryName);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(issues);
    }

}
