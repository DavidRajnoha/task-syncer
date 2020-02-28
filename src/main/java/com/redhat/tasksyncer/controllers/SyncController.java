package com.redhat.tasksyncer.controllers;

import com.redhat.tasksyncer.exceptions.CannotConnectToRepositoryException;
import com.redhat.tasksyncer.services.repositories.RemoteRepositoryService;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class SyncController {

    private final RemoteRepositoryService repositoryService;

    @Autowired
    public SyncController(RemoteRepositoryService repositoryService){
        this.repositoryService = repositoryService;
    }

    @RequestMapping(path = "/v1/service/{serviceName}/project/{projectName}/repository/{repoName}/download",
            method = RequestMethod.PUT
    )    public ResponseEntity<String> syncRepository(@PathVariable String serviceName,
                                                      @PathVariable String projectName,
                                                      @PathVariable String repoName)
            throws IOException, CannotConnectToRepositoryException, GitLabApiException {

        repositoryService.downloadAndSaveIssues(repoName, projectName, serviceName);

        return ResponseEntity.status(HttpStatus.OK).body("");
    }
}
