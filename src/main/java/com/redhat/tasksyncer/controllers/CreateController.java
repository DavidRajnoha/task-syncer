package com.redhat.tasksyncer.controllers;


import com.redhat.tasksyncer.exceptions.CannotConnectToRepositoryException;
import com.redhat.tasksyncer.exceptions.InvalidMappingException;
import com.redhat.tasksyncer.services.projects.ProjectService;
import com.redhat.tasksyncer.services.repositories.LocalRepositoryService;
import com.redhat.tasksyncer.services.repositories.RemoteRepositoryService;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @author David Rajnoha
 */
@RestController
public class CreateController {

    @Value("${githubWebhookURL}")
    private String githubWebhookUrl;

    @Value("${gitlabWebhookURL}")
    private String gitlabWebhookUrl;

    private ProjectService projectService;
    private LocalRepositoryService localRepositoryService;
    private RemoteRepositoryService remoteRepositoryService;

    @Autowired
    public CreateController(ProjectService projectService, LocalRepositoryService localRepositoryService,
                            RemoteRepositoryService remoteRepositoryService){
        this.projectService = projectService;
        this.localRepositoryService = localRepositoryService;
        this.remoteRepositoryService = remoteRepositoryService;
    }

    @RequestMapping(path = "/v1/project/{projectName}/create",
            method = RequestMethod.POST
    )
    public ResponseEntity<String> createProjectEndpoint(@PathVariable String projectName,
                                                        @RequestParam List<String> columnNames){
        projectService.createProject(projectName, columnNames);
        return ResponseEntity.status(200).body("");
    }


    /**
     * Connects the repository from external service with existing project in tasksyncer
     *
     * @param firstLoginCredential Trello - app key; Jira - email@adress; Github - email@adress; GitLab - instance URL
     * @param secondLoginCredential Trello - token; Jira - API token; Github - password; Gitlab - AuthKey
     * @param serviceName supported values: trello, jira, github, gitlab
     * @param projectName name of the project you are trying to connect external service to

     * @param repoNamespace Trello - random value, namespace not required
     *                      Jira - url of your instance
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
    @RequestMapping(path = "/v1/service/{serviceName}/project/{projectName}/connect/{repoNamespace}/{repoName}",
            method = RequestMethod.POST
    ) public ResponseEntity<String> connectServiceEndpoint(@PathVariable String serviceName,
                                                           @PathVariable String projectName,
                                                           @PathVariable String repoNamespace,
                                                           @PathVariable String repoName,
                                                           @RequestParam("firstLoginCredential") String firstLoginCredential,
                                                           @RequestParam("secondLoginCredential") String secondLoginCredential,
                                                           @RequestParam List<String> columnNames,
                                                           @RequestParam List<String> columnMapping)
            throws InvalidMappingException, IOException, CannotConnectToRepositoryException, GitLabApiException {
        localRepositoryService.createRepository(firstLoginCredential, secondLoginCredential, repoName, repoNamespace,
                projectName, serviceName, columnMapping, columnNames);

        remoteRepositoryService.downloadAndSaveIssues(repoName, projectName, serviceName);

        return ResponseEntity.status(200).body("");
    }



    @RequestMapping(path = "/v1/service/{serviceName}/project/{projectName}/hook/{repoNamespace}/{repoName}",
            method = RequestMethod.POST
    ) public ResponseEntity<String> hookServiceEndpoint(@PathVariable String serviceName,
                                                           @PathVariable String projectName,
                                                           @PathVariable String repoNamespace,
                                                           @PathVariable String repoName,
                                                           @RequestParam("firstLoginCredential") String firstLoginCredential,
                                                           @RequestParam("secondLoginCredential") String secondLoginCredential,
                                                           @RequestParam List<String> columnNames,
                                                           @RequestParam List<String> columnMapping)
            throws InvalidMappingException, IOException, CannotConnectToRepositoryException, GitLabApiException {
        connectServiceEndpoint(serviceName, projectName, repoNamespace, repoName, firstLoginCredential,
                secondLoginCredential, columnNames, columnMapping);

        hookRepository(serviceName, projectName, repoName);

        return ResponseEntity.status(200).body("");
    }



    @RequestMapping(path = "/v1/service/{serviceName}/project/{projectName}/repository/{repoName}",
            method = RequestMethod.POST
    ) public ResponseEntity<String> hookRepository(@PathVariable String serviceName,
                                                   @PathVariable String projectName,
                                                   @PathVariable String repoName)
            throws IOException, CannotConnectToRepositoryException, GitLabApiException {

        remoteRepositoryService.hookRepository(repoName, projectName, serviceName,  githubWebhookUrl);

        return ResponseEntity.status(200).body("");
    }








}
