package com.redhat.tasksyncer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.tasksyncer.dao.accessors.ProjectAccessor;
import com.redhat.tasksyncer.dao.accessors.RepositoryAccessor;
import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import com.redhat.tasksyncer.dao.repositories.*;
import com.redhat.tasksyncer.decoders.AbstractWebhookIssueDecoder;

import com.redhat.tasksyncer.exceptions.InvalidWebhookCallbackException;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;
import com.redhat.tasksyncer.exceptions.TrelloCalllbackNotAboutCardException;
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

    @Autowired
    private ProjectAccessor projectAccessor;

    public Endpoints() {
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
    public String hookEndpoint(@PathVariable String serviceName,
            @PathVariable String projectName,
                       HttpServletRequest request
    ) throws Exception {
        return processHook(projectName, request, serviceName);
    }

    //processing received webhook
    //TODO: Move from endpoints
    private String processHook(String projectName, HttpServletRequest request, String serviceType
    ) throws Exception {

        Project project = projectRepository.findProjectByName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project with name does not exist"));

        try {
            AbstractIssue newIssue = AbstractWebhookIssueDecoder.getInstance(serviceType).decode(request, project, repositoryRepository);


            projectAccessor.saveAndInitialize(project);
            projectAccessor.syncIssue(newIssue);
        } catch (TrelloCalllbackNotAboutCardException | InvalidWebhookCallbackException ignored) {
        } catch (JsonProcessingException e){
            return "Proccessing of the webhook failed - unsuported type";
        }
        return OK;
    }



    /**
     * @param firstLoginCredential Trello - app key; Jira - email@adress
     * @param secondLoginCredential Trello - token; Jira - ???
     *
     * */
    @RequestMapping(path = "/service/{serviceName}/project/{projectName}/{hookOrConnect}/{repoNamespace}/{repoName}",
            method = RequestMethod.PUT
    )    public ResponseEntity<String> connectService(@PathVariable String serviceName,
                                                      @PathVariable String projectName,
                                                      @PathVariable String hookOrConnect,
                                                      @PathVariable String repoNamespace,
                                                      @PathVariable String repoName,
                                                      @RequestParam("firstLoginCredential") String firstLoginCredential,
                                                      @RequestParam("secondLoginCredential") String secondLoginCredential) throws Exception {
        Project project = projectRepository.findProjectByName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project with name does not exist"));

        //Creates a projectAccessor and passes all components that has been autowired and values that has been defined here
        projectAccessor.saveAndInitialize(project);
        AbstractRepository repository = AbstractRepository.newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace(serviceName, firstLoginCredential, secondLoginCredential, repoName, repoNamespace);


        //And also conducts synchronization of the gitlab issues with the local issueRepository and trello
            RepositoryAccessor repositoryAccessor = projectAccessor.addRepository(repository);

        // Deciding if create hook or not based on the query parameters
        switch (hookOrConnect){
            case "hook":
                //TODO: let the repositoryAccessors get the hook somewhere else
                try {
                //projectAccessor.hookRepository(repository, gitlabWebhookURLString.replace("{projectName}", projectName));
                projectAccessor.hookRepository(repository, githubWebhookURLString.replace("{projectName}", projectName));
                } catch (Exception e) {
                    e.printStackTrace();
                    repositoryAccessor.deleteRepository(repository);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create the webhook," +
                            " check if the webhook is not already created");
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
    @RequestMapping(path = "/service/{serviceType}/new/project/{projectName}/{repoNamespace}/{repoName}/to/trello/{boardName}",
                    method = RequestMethod.PUT
    )
    public ResponseEntity<String> createProjectEndpoint(@PathVariable String projectName,
                                                @PathVariable String serviceType,
                                                @PathVariable String repoNamespace,
                                                @PathVariable String repoName,
                                                @PathVariable String boardName,
                                                @RequestParam("firstLoginCredential") String firstLoginCredential,
                                                @RequestParam("secondLoginCredential") String secondLoginCredential
    ) throws Exception {
        return createProject(projectName, serviceType, repoNamespace, repoName, boardName, firstLoginCredential, secondLoginCredential, true);
    }


    //TODO: rename, move out of endpoints and reconsider
    public ResponseEntity<String> createProject(String projectName, String serviceType, String repoNamespace, String repoName,
                                                String boardName, String firstLoginCredential, String secondLoginCredential,
                                                Boolean trello) throws RepositoryTypeNotSupportedException, IOException {
        projectRepository.findProjectByName(projectName).ifPresent(p -> {
            throw new IllegalArgumentException("Project with name already exists");
        });

        Project project = new Project();
        project.setName(projectName);

        projectAccessor.saveAndInitialize(project);
        AbstractRepository repository = AbstractRepository.newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace(serviceType, firstLoginCredential, secondLoginCredential, repoName, repoNamespace);


        if (trello) {
            try {
                projectAccessor.initialize(TrelloCard.class.getName(), boardName);
            } catch (HttpClientErrorException e){
                projectAccessor.deleteProject(project);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authentication with Trello failed");
            }
        }

        try {
            projectAccessor.addRepository(repository);
        } catch (Exception e) {
            projectAccessor.deleteBoard(trelloApplicationKey, trelloAccessToken);
            e.printStackTrace();
            projectAccessor.deleteProject(project);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Database failure, repository already exists");
        }

        projectAccessor.save(); // todo: make it transactional

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("OK");
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
