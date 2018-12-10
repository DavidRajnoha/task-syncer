package com.redhat.tasksyncer.dao.entities;

import org.gitlab.api.models.GitlabIssue;

import java.util.HashSet;

/**
 * @author Filip Cap
 */
public class GLIssue extends Issue {
    /**
     * Neither of args should be null
     */
    public GLIssue(GitlabIssue gli) {
        super(
                String.valueOf(gli.getIid()),
                String.valueOf(gli.getIid()),
                gli.getTitle(),
                gli.getDescription(),
                gli.getState().equals(GitlabIssue.STATE_OPENED),
                new HashSet<>(),
                Issue.GITLAB_ISSUE
        );

    }
}
