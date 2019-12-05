package com.redhat.tasksyncer.decoders;


import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.Project;
import com.redhat.tasksyncer.dao.entities.TrelloIssue;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.InvalidWebhookCallbackException;
import com.redhat.tasksyncer.exceptions.TrelloCalllbackNotAboutCardException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


/**
 * @author David Rajnoha
 * */
@Component
public class TrelloWebhookIssueDecoder extends AbstractWebhookIssueDecoder {

    @Autowired
    public TrelloWebhookIssueDecoder(AbstractRepositoryRepository repositoryRepository){
        this.repositoryRepository = repositoryRepository;
    }


    @Override
    public AbstractIssue decode(HttpServletRequest request, Project project)
            throws InvalidWebhookCallbackException, TrelloCalllbackNotAboutCardException {

        JSONObject input = AbstractWebhookIssueDecoder.RequestToJsonDecoder.toJson(request);

        AbstractIssue issue;
        try {
            AbstractRepository repository = (repositoryRepository.findByRepositoryNameAndProject_Id(input.getJSONObject("action")
                    .getJSONObject("data").getJSONObject("board").get("id").toString(), project.getId()));

            issue = TrelloIssue.ObjectToTrelloIssueConvertor.convert(input, repository.getColumnMapping());

            issue.setRepository(repository);
        } catch (JSONException e){
            e.printStackTrace();
            throw new InvalidWebhookCallbackException("Error processing the webhook");
        }

        return issue;
    }

}
