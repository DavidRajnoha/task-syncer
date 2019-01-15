package com.redhat.tasksyncer.dao.entities;

import org.gitlab4j.api.models.Issue;

import javax.persistence.Entity;

/**
 * @author Filip Cap
 */
@Entity
public class GitlabIssue extends AbstractIssue {
    public GitlabIssue() {
        super();
    }

    public static class ObjectToGitlabIssueConverter {
        public static GitlabIssue convert(Issue input) {
            GitlabIssue issue = new GitlabIssue();

            issue.setRemoteIssueId(input.getId().toString());
            issue.setTitle(input.getTitle());
            issue.setDescription(input.getDescription());

            return issue;
        }
    }
}
