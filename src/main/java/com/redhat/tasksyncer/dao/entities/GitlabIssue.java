package com.redhat.tasksyncer.dao.entities;

import com.redhat.tasksyncer.dao.enumerations.IssueType;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.webhook.IssueEvent;

import javax.persistence.Entity;
import java.util.Objects;

/**
 * @author Filip Cap
 */
@Entity
public class GitlabIssue extends AbstractIssue {
    public GitlabIssue() {
        super(IssueType.GITLAB);
    }

    public static class ObjectToGitlabIssueConverter {
        public static GitlabIssue convert(Issue input) {
            GitlabIssue issue = new GitlabIssue();

            issue.setRemoteIssueId(input.getId().toString());
            issue.setTitle(input.getTitle());
            issue.setDescription(input.getDescription());

            if(input.getState() == Constants.IssueState.OPENED)
                issue.setState(AbstractIssue.STATE_OPENED);
            if(input.getState() == Constants.IssueState.CLOSED)
                issue.setState(AbstractIssue.STATE_CLOSED);
            if(input.getState() == Constants.IssueState.REOPENED)
                issue.setState(AbstractIssue.STATE_REOPENED);


            return issue;
        }

        public static GitlabIssue convert(IssueEvent.ObjectAttributes input) {
            GitlabIssue issue = new GitlabIssue();

            issue.setRemoteIssueId(input.getId().toString());
            issue.setTitle(input.getTitle());
            issue.setDescription(input.getDescription());

            if(Objects.equals(input.getState(), Constants.IssueState.OPENED.toString()))
                issue.setState(AbstractIssue.STATE_OPENED);
            if(Objects.equals(input.getState(), Constants.IssueState.CLOSED.toString()))
                issue.setState(AbstractIssue.STATE_CLOSED);
            if(Objects.equals(input.getState(), Constants.IssueState.REOPENED.toString()))
                issue.setState(AbstractIssue.STATE_REOPENED);

            return issue;
        }
    }
}
