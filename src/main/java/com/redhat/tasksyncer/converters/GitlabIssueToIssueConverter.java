package com.redhat.tasksyncer.converters;

import com.redhat.tasksyncer.dao.entities.Issue;
import org.gitlab.api.models.GitlabIssue;

import java.util.HashSet;

/**
 * @author Filip Cap
 */
public class GitlabIssueToIssueConverter implements ObjectToIssueConverter<GitlabIssue> {
    @Override
    public Issue convert(GitlabIssue object) {
        return new Issue(
                String.valueOf(object.getId()),
                String.valueOf(object.getIid()),
                object.getTitle(),
                object.getDescription(),
                GitlabIssue.STATE_OPENED.equalsIgnoreCase(object.getState()),
                new HashSet<>(),
                Issue.GITLAB_ISSUE
        );
    }
}
