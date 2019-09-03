package com.redhat.tasksyncer.dao.entities;

import com.redhat.tasksyncer.dao.enumerations.IssueType;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;

import javax.persistence.Entity;
import java.util.Objects;


@Entity
public class GithubIssue extends AbstractIssue {
    public GithubIssue() {
        super(IssueType.GITHUB);
    }

    public static class ObjectToGithubIssueConverter {

        public static GithubIssue convert(GHIssue input) {
            GithubIssue issue = new GithubIssue();

            issue.setRemoteIssueId("GH" + input.getRepository().getName() + (input.getId()));
            issue.setTitle(input.getTitle());
            issue.setDescription(input.getBody());
            issue.setRepositoryName(input.getRepository().getName());

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
