package com.redhat.tasksyncer.decoders;


import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.Project;
import com.redhat.tasksyncer.dao.entities.TrelloIssue;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.InvalidWebhookCallbackException;
import com.redhat.tasksyncer.exceptions.TrelloCalllbackNotAboutCardException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;


/**
 * @author David Rajnoha
 * */
public class TrelloWebhookIssueDecoder extends AbstractWebhookIssueDecoder {

    @Override
    public AbstractIssue decode(HttpServletRequest request, Project project, AbstractRepositoryRepository repositoryRepository)
            throws InvalidWebhookCallbackException, TrelloCalllbackNotAboutCardException {

        JSONObject input = AbstractWebhookIssueDecoder.RequestToJsonDecoder.toJson(request);

        AbstractIssue issue;
        try {
            issue = TrelloIssue.ObjectToTrelloIssueConvertor.convert(input);
            issue.setRepository(repositoryRepository.findByRepositoryNameAndProject_Id(input.getJSONObject("action")
                    .getJSONObject("data").getJSONObject("board").get("id").toString(), project.getId()));
        } catch (JSONException e){
            e.printStackTrace();
            throw new InvalidWebhookCallbackException("Error processing the webhook");
        }

        return issue;
    }

}
