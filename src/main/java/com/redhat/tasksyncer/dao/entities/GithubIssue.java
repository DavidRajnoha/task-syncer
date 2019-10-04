package com.redhat.tasksyncer.dao.entities;

import com.redhat.tasksyncer.dao.enumerations.IssueType;
import org.gitlab4j.api.models.Assignee;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHUser;

import javax.persistence.Entity;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


@Entity
public class GithubIssue extends AbstractIssue {
    public GithubIssue() {
        super(IssueType.GITHUB);
    }

    public static class ObjectToGithubIssueConverter {

        public static GithubIssue convert(GHIssue input) {
            GithubIssue issue = new GithubIssue();

            //set RemoteIssueId
            issue.setRemoteIssueId(Long.toString(input.getId()));
            //set Title
            issue.setTitle(input.getTitle());
            //set Description
            issue.setDescription(input.getBody());

            //set Assignee
            try {
                Optional.ofNullable(input.getAssignee()).ifPresent(assignee -> {
                    issue.setAssignee(assignee.getLogin());
                });
            } catch (IOException e) {e.printStackTrace(); }

            //set Labels
            try {
                Set<String> labels = new HashSet<>();
                input.getLabels().forEach(ghLabel -> labels.add(ghLabel.getName()));
                issue.setLabel(labels);
            } catch (IOException e) {e.printStackTrace();}

            //set Comments
            try {
                Set<Comment> comments = new HashSet<>();
                input.getComments().forEach(githubComment -> {
                    try {
                        comments.add(new Comment(githubComment.getBody(), githubComment.getCreatedAt(), githubComment.getUser().getLogin()));
                    } catch (IOException e) { e.printStackTrace(); } });
                issue.setComments(comments);
            } catch (IOException e) { e.printStackTrace(); }

            //set CreatedAt
            try {
                issue.setCreatedAt(input.getCreatedAt());
            } catch (IOException e) { e.printStackTrace(); }

            //set ClosedAt
            issue.setClosedAt(input.getClosedAt());

            //set ClosedBy
            try {
                Optional.ofNullable(input.getClosedBy()).ifPresent(ghUser -> {
                    issue.setClosedBy(ghUser.getLogin());
                });
            } catch (IOException e) { e.printStackTrace(); }



            //Dealing with state
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
