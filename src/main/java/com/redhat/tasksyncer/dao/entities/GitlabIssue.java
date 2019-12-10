package com.redhat.tasksyncer.dao.entities;

import com.redhat.tasksyncer.dao.enumerations.IssueType;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.webhook.IssueEvent;

import javax.persistence.Entity;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

/**
 * @author Filip Cap
 */
@Entity
public class GitlabIssue extends AbstractIssue {
    public GitlabIssue() {
        super(IssueType.GITLAB);
    }

    public static class ObjectToGitlabIssueConverter {
        public static GitlabIssue convert(Issue input, Map<String, String> columnMapping) {
            GitlabIssue issue = new GitlabIssue();

            issue.setRemoteIssueId(input.getId().toString());
            issue.setTitle(input.getTitle());
            issue.setDescription(input.getDescription());

            issue.setDueDate(input.getDueDate());
            //set Assignee
            Optional.ofNullable(input.getAssignee()).ifPresent(assignee -> issue.setAssignee(assignee.getName()));
            Optional.ofNullable(input.getLabels()).ifPresent(label -> {issue.setLabel(new HashSet<>(label));});


            issue.setCreatedAt(input.getCreatedAt());
            issue.setClosedAt(input.getClosedAt());
            //set ClosedBy
            Optional.ofNullable(input.getClosedBy()).ifPresent(user -> issue.setClosedBy(user.getName()));


//            if(input.getState() == Constants.IssueState.OPENED)
//                issue.setState(columnMapping.get(AbstractIssue.STATE_OPENED));
//            if(input.getState() == Constants.IssueState.CLOSED)
//                issue.setState(columnMapping.get(AbstractIssue.STATE_CLOSED));
//            if(input.getState() == Constants.IssueState.REOPENED)
//                issue.setState(columnMapping.get(AbstractIssue.STATE_REOPENED));

            issue.setState(columnMapping.get(input.getState().toString()));

            return issue;
        }

        public static GitlabIssue convert(IssueEvent.ObjectAttributes input,
                                          Map<String, String> columnMapping) {
            GitlabIssue issue = new GitlabIssue();

            issue.setRemoteIssueId(input.getId().toString());
            issue.setTitle(input.getTitle());
            issue.setDescription(input.getDescription());

            issue.setCreatedAt(input.getCreatedAt());

//            if(Objects.equals(input.getState(), Constants.IssueState.OPENED.toString()))
//                issue.setState(columnMapping.get(AbstractIssue.STATE_OPENED));
//            if(Objects.equals(input.getState(), Constants.IssueState.CLOSED.toString()))
//                issue.setState(columnMapping.get(AbstractIssue.STATE_CLOSED));
//            if(Objects.equals(input.getState(), Constants.IssueState.REOPENED.toString()))
//                issue.setState(columnMapping.get(AbstractIssue.STATE_REOPENED));

            issue.setState(columnMapping.get(input.getState()));


            return issue;
        }


    }
}
