package com.redhat.tasksyncer.decoders;


import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.Project;
import com.redhat.tasksyncer.dao.entities.TrelloIssue;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;


/**
 * @author David Rajnoha
 * */
public class TrelloWebhookIssueDecoder extends AbstractWebhookIssueDecoder {

    @Override
    public AbstractIssue decode(HttpServletRequest request, Project project, AbstractRepositoryRepository repositoryRepository) throws Exception {
        JSONObject input = AbstractWebhookIssueDecoder.RequestToJsonDecoder.toJson(request);

        AbstractIssue issue = TrelloIssue.ObjectToTrelloIssueConvertor.convert(input);

        issue.setRepository(repositoryRepository.findByRepositoryNameAndProject_Id(input.getJSONObject("action")
                .getJSONObject("data").getJSONObject("board").get("id").toString(), project.getId()));

        return issue;
    }

}
