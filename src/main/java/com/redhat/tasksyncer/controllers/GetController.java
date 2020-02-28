package com.redhat.tasksyncer.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.tasksyncer.dao.accessors.issue.AbstractIssueAccessor;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Set;

public class GetController {

    private AbstractIssueAccessor issueAccessor;


    @RequestMapping(path = "/v1/project/{projectName}/all/{issueType}",
            method = RequestMethod.GET
    )
    public String getIssuesByType(@PathVariable String projectName,
                                  @PathVariable IssueType issueType) throws JsonProcessingException {
        Set<AbstractIssue> issues = issueAccessor.getIssuesByType(issueType);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(issues);
    }

    @RequestMapping(path = "/v1/all",
            method = RequestMethod.GET
    )
    public String getAllIssues(@PathVariable String projectName) throws JsonProcessingException {
        List<AbstractIssue> issues = issueAccessor.getAll();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(issues);
    }


    @RequestMapping(path = "/v1/project/{projectName}/{repositoryName}/{remoteIssueId}",
            method = RequestMethod.GET
    )
    public String getIssueByTypeAndRepositoryAndId(@PathVariable String projectName, @PathVariable String repositoryName,
                                                   @PathVariable String remoteIssueId) throws JsonProcessingException {
        List<AbstractIssue> issues = issueAccessor.getIssue(projectName, repositoryName, remoteIssueId);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(issues);
    }

}
