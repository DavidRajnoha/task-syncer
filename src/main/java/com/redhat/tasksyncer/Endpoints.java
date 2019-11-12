package com.redhat.tasksyncer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private AbstractIssueRepository issueRepository;

    @Autowired
    private AbstractRepositoryRepository repositoryRepository;

    private TaskSyncerService service;


    public Endpoints(TaskSyncerService service) {
        this.service = service;
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
        try {
            service.processHook(projectName, request, serviceName);
        } catch (RepositoryTypeNotSupportedException | InvalidWebhookCallbackException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (TrelloCalllbackNotAboutCardException ignored) {
        }

        return ResponseEntity.status(HttpStatus.OK).body("Webhook processed");
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
        try {
            return service.connectService(projectName, serviceName, repoNamespace, repoName, firstLoginCredential,
                    secondLoginCredential, hookOrConnect);
        } catch (CannotConnectToRepositoryException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Could not connect to the " + serviceName +
                    "web service");
        } catch (RepositoryTypeNotSupportedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Repository of service of type: " + serviceName +
                    " is not implemented");
        } catch (SynchronizationFailedException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Connecting to the repository" +
                    "failed");
        }
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
        try {
            return service.createProject(projectName, serviceName, repoNamespace, repoName, boardName, firstLoginCredential, secondLoginCredential, true);
        } catch (RepositoryTypeNotSupportedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Repository of service of type: " + serviceName +
                    " is not implemented");
        }
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
