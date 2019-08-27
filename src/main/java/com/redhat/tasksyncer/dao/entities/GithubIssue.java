package com.redhat.tasksyncer.dao.entities;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;

import javax.persistence.Entity;
import java.util.Objects;


@Entity
public class GithubIssue extends AbstractIssue {
    public GithubIssue() {
        super();
    }

    public static class ObjectToGithubIssueConverter {

        public static GithubIssue convert(GHIssue input) {
            GithubIssue issue = new GithubIssue();

            issue.setRemoteIssueId("GH" + (input.getId()));
            issue.setTitle(input.getTitle());
            issue.setDescription(input.getBody());

            if(Objects.equals(input.getState(), GHIssueState.OPEN)){
                issue.setState(AbstractIssue.STATE_OPENED);
            }
            if(Objects.equals(input.getState(), GHIssueState.CLOSED)){
                issue.setState(AbstractIssue.STATE_CLOSED);
            }

            return issue;
        }

    }
}