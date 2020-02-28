package com.redhat.tasksyncer.services.issues;

import com.redhat.tasksyncer.dao.accessors.issue.AbstractIssueAccessor;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueService {

    private AbstractIssueAccessor issueAccessor;

    @Autowired
    public IssueService(AbstractIssueAccessor issueAccessor){
        this.issueAccessor = issueAccessor;
    }

    public List<AbstractIssue> getIssuesFromProject(String projectName){
        return issueAccessor.getProject(projectName);
    }

}
