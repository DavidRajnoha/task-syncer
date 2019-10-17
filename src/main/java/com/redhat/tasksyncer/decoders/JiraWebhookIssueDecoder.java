package com.redhat.tasksyncer.decoders;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser;
import com.atlassian.jira.util.http.JiraHttpUtils;
import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.JiraIssue;
import com.redhat.tasksyncer.dao.entities.Project;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;

import org.codehaus.jettison.json.JSONException;
import org.json.HTTP;
import org.json.JSONObject;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Set;


public class JiraWebhookIssueDecoder extends AbstractWebhookIssueDecoder {

    @Override
    public AbstractIssue decode(HttpServletRequest request, Project project, AbstractRepositoryRepository repositoryRepository) throws Exception {
//        JSONObject input = requestToInput(request);
        JSONObject input = AbstractWebhookIssueDecoder.RequestToJsonDecoder.toJson(request);

        AbstractIssue issue = JiraIssue.ObjectToJiraIssueConverter.convert(input);

        issue.setRepository(repositoryRepository.findByRepositoryNameAndProject_Id(input.getJSONObject("issue").getJSONObject("fields").getJSONObject("project").getString("key"), project.getId()));

        return issue;
    }


//    private JSONObject requestToInput(HttpServletRequest request) throws JSONException, org.json.JSONException {
//        StringBuilder stringBuffer = new StringBuilder();
//        String line;
//
//        try {
//            BufferedReader reader = request.getReader();
//            while ((line = reader.readLine()) != null)
//                stringBuffer.append(line);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String jsonString = stringBuffer.toString();
//        return new JSONObject(jsonString);
//    }

}
