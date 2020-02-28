package com.redhat.tasksyncer.dao.entities.issues;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.redhat.tasksyncer.dao.entities.issues.components.Comment;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.Entity;
import java.util.*;


/**
 * @author David Rajnoha
 * */
@Entity
public class JiraIssue extends AbstractIssue {
    public JiraIssue() {
        super(IssueType.JIRA);
    }

    // TODO: Move static methods from here

    public static class ObjectToJiraIssueConverter{
        public static JiraIssue convert(Issue input, Map<String, String> columnMapping) {
            JiraIssue issue = new JiraIssue();

            issue.setRemoteIssueId(input.getKey());
            issue.setTitle(input.getSummary());
            issue.setDescription(input.getDescription());
            //set DueDate
            Optional.ofNullable(input.getDueDate()).ifPresent(dateTime -> issue.setDueDate(dateTime.toDate()));
            //set Assignee
            Optional.ofNullable(input.getAssignee()).ifPresent(user -> issue.setAssignee(user.getName()));
            issue.setLabel(input.getLabels());
            //set CreatedAt
            Optional.ofNullable(input.getCreationDate()).ifPresent(dateTime -> issue.setCreatedAt(dateTime.toDate()));
            //set Comments
            Set<Comment> comments = new HashSet<>();


            input.getComments().forEach(comment -> {
                comments.add(new Comment(comment.getBody(), comment.getCreationDate().toDate(), Objects.requireNonNull(comment.getAuthor()).getName()));
            });
            issue.setComments(comments);


            input.getIssueType();
            input.getSubtasks();
            input.getVotes();
            input.getAffectedVersions();
            input.getComments();
            input.getFixVersions();


//            //TODO: rework so the Issue States are not hardcoded here, but set as a parameter (Each porject has different issue states)
//            if (input.getStatus().getName().equals("Next"))
//                issue.setState(AbstractIssue.STATE_OPENED);
//            else if (input.getStatus().getName().toLowerCase().equals("opened"))
//                issue.setState(AbstractIssue.STATE_OPENED);
//            else if (input.getStatus().getName().equals("Next"))
//                issue.setState(AbstractIssue.STATE_OPENED);
//            else issue.setState(AbstractIssue.STATE_CLOSED);

            String jiraState = input.getStatus().getName().toUpperCase();
            issue.setState(columnMapping.get(jiraState));

            return issue;
        }


        //
        public static AbstractIssue convert(JSONObject input, Map<String, String> columnMapping) throws JSONException {
            AbstractIssue issue = new JiraIssue();


            JSONObject inputIssue = input.getJSONObject("issue");
            JSONObject inputFields = inputIssue.getJSONObject("fields");




            issue.setRemoteIssueId(inputIssue.getString("key"));

            issue.setTitle(inputFields.getString("summary"));
            issue.setDescription(inputFields.optString("description"));

            // Sets state based on the mapping
            String jiraState = inputFields.getJSONObject("status").getString("name").toUpperCase();
            issue.setState(columnMapping.get(jiraState));

//            //TODO: rework so the Issue States are not hardcoded here, but set as a parameter (Each project has different issue states, the mapping will be set by the user)
//            if(status.equals("open"))
//                issue.setState(STATE_OPENED);
//            else if (status.equals("Backlog"))
//                issue.setState(STATE_OPENED);
//            else if(status.equals("Closed"))
//                issue.setState(STATE_CLOSED);
//            else issue.setState(STATE_OPENED);

            return issue;
        }


        public static Set<AbstractIssue> getSubtasks(Issue input){
            Set<AbstractIssue> subIssues = new HashSet<>();
            Optional.ofNullable(input.getSubtasks()).ifPresent(subtasks -> subtasks.forEach(subtask -> {
                AbstractIssue subIssue = new JiraIssue();

                subIssue.setRemoteIssueId(subtask.getIssueKey());
                subIssue.setTitle(subtask.getSummary());

                subIssues.add(subIssue);
            }));

            return subIssues;
        }


    }
}
