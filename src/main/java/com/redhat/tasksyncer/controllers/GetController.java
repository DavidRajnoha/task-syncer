package com.redhat.tasksyncer.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.tasksyncer.dao.accessors.issue.AbstractIssueAccessor;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class GetController {

    private AbstractIssueAccessor issueAccessor;

    @Autowired
    public GetController(AbstractIssueAccessor issueAccessor){
        this.issueAccessor = issueAccessor;
    }

    @RequestMapping(path = "/v1/project/{projectName}/all/{issueType}",
            method = RequestMethod.GET
    )
    public String getIssuesByType(@PathVariable String projectName,
                                  @PathVariable String issueType) throws JsonProcessingException {
        Set<AbstractIssue> issues = issueAccessor.getIssuesByType(IssueType.fromString(issueType));

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(issues);
    }

    @RequestMapping(path = "/v1/all/{projectName}",
            method = RequestMethod.GET
    )
    public String getAllIssues(@PathVariable String projectName) throws JsonProcessingException {
        List<AbstractIssue> issues = issueAccessor.getProject(projectName);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(issues);
    }

    @RequestMapping(path = "/v1/all",
            method = RequestMethod.GET
    )
    public String getAll() throws JsonProcessingException {
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
