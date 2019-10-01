package com.redhat.tasksyncer.dao.entities;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.Entity;


@Entity
public class JiraIssue extends AbstractIssue {
    public JiraIssue() {
        super(IssueType.JIRA);
    }

    public static class ObjectToJiraIssueConverter{
        public static JiraIssue convert(Issue input){
            JiraIssue issue = new JiraIssue();

            issue.setRemoteIssueId(input.getId().toString());
            issue.setTitle(input.getSummary());
            issue.setDescription(input.getDescription());


            //TODO: rework so the Issue States are not hardcoded here, but set as a parameter (Each porject has different issue states)
            if(input.getStatus().getDescription().equals("Next"))
                issue.setState(AbstractIssue.STATE_OPENED);
            if(input.getStatus().getDescription().equals("Next"))
                issue.setState(AbstractIssue.STATE_OPENED);
            if(input.getStatus().getDescription().equals("Next"))
                issue.setState(AbstractIssue.STATE_OPENED);
            else issue.setState(AbstractIssue.STATE_CLOSED);

            return issue;
        }

        public static AbstractIssue convert(JSONObject input) throws JSONException {
            AbstractIssue issue = new JiraIssue();
            JSONObject inputIssue = input.getJSONObject("issue");
            JSONObject inputFields = inputIssue.getJSONObject("fields");

            issue.setRemoteIssueId(inputIssue.getString("id"));

            issue.setTitle(inputFields.getString("summary"));
            issue.setDescription(inputFields.getString("description"));

            String status = inputFields.getJSONObject("status").getString("name");

            //TODO: rework so the Issue States are not hardcoded here, but set as a parameter (Each project has different issue states, the mapping will be set by the user)
            if(status.equals("open"))
                issue.setState(STATE_OPENED);
            else if (status.equals("Backlog"))
                issue.setState(STATE_OPENED);
            else if(status.equals("Closed"))
                issue.setState(STATE_CLOSED);

            return issue;
        }
    }
}
