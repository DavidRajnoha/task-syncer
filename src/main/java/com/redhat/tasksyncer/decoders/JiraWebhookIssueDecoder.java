package com.redhat.tasksyncer.decoders;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.repositories.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.issues.JiraIssue;
import com.redhat.tasksyncer.dao.entities.projects.Project;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.InvalidWebhookCallbackException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author David Rajnoha
 * */
@Component
public class JiraWebhookIssueDecoder extends AbstractWebhookIssueDecoder {

    @Autowired
    public JiraWebhookIssueDecoder(AbstractRepositoryRepository repositoryRepository){
        this.repositoryRepository = repositoryRepository;
    }


    @Override
    public AbstractIssue decode(HttpServletRequest request, Project project)
            throws  InvalidWebhookCallbackException {
//        JSONObject input = requestToInput(request);
        JSONObject input = AbstractWebhookIssueDecoder.RequestToJsonDecoder.toJson(request);

        // TODO: Decide not based on input.has("issue") but based on the issue.getString("webhookEvent") value
        try {
            if (input.has("issue")) {
                return decodeIssue(input, repositoryRepository, project);
            } else if (input.getString("webhookEvent").equals("issuelink_deleted")) {
                return destroyLink(input, repositoryRepository, project);
            } else {
                throw new InvalidWebhookCallbackException("Callback has no atribute for Issue");
            }
        } catch (JSONException e){
            e.printStackTrace();
            throw new InvalidWebhookCallbackException("Error parsing webhhok body");
        }
    }

    // TODO: alter so it will be supported, necessary to use jira id's and not keys
    private AbstractIssue destroyLink(JSONObject input, AbstractRepositoryRepository repositoryRepository, Project project) throws InvalidWebhookCallbackException {
        throw new InvalidWebhookCallbackException("Converting subtasks to issues is ot yet supported");
    }

    private AbstractIssue decodeIssue(JSONObject input, AbstractRepositoryRepository repositoryRepository, Project project)
            throws JSONException {

        AbstractRepository repository = repositoryRepository
                .findByRepositoryNameAndProject_Id(input.getJSONObject("issue")
                        .getJSONObject("fields")
                        .getJSONObject("project")
                        .getString("key"), project.getId());

        AbstractIssue issue = JiraIssue.ObjectToJiraIssueConverter.convert(input, repository.getColumnMapping());
        issue.setRepository(repository);

        // if the callback is about subissue, then sends the subIssue to the update method wrapped in container issue with
        // same remoteIssueId and repository as the parent of the issue. The correct parent is then find while updating the
        // container issue and the parent-issue link is correctly created
        if (input.getJSONObject("issue").getJSONObject("fields").getJSONObject("issuetype").getBoolean("subtask")){
            AbstractIssue parentIssue = new JiraIssue();
            parentIssue.setRepository(repository);
            parentIssue.setRemoteIssueId(input.getJSONObject("issue").getJSONObject("fields").getJSONObject("parent").getString("key"));
            parentIssue.addChildIssue(issue);
            return parentIssue;
        }

        return issue;
    }


}
