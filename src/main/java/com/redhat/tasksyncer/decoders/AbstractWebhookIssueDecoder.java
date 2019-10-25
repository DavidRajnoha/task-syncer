package com.redhat.tasksyncer.decoders;

import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author David Rajnoha
 * */
public abstract class AbstractWebhookIssueDecoder {

    public static AbstractWebhookIssueDecoder getInstance(String serviceType) throws RepositoryTypeNotSupportedException {
        AbstractWebhookIssueDecoder issueDecoder;

        switch (serviceType){
            case "gitlab":
                issueDecoder = new GitlabWebhookIssueDecoder();
                break;
            case "github":
                issueDecoder = new GithubWebhookIssueDecoder();
                break;
            case "jira":
                issueDecoder = new JiraWebhookIssueDecoder();
                break;
            case "trello":
                issueDecoder = new TrelloWebhookIssueDecoder();
                break;
            default:
                throw new RepositoryTypeNotSupportedException("");
        }

        return issueDecoder;
    }

    public abstract AbstractIssue decode(HttpServletRequest request, Project project, AbstractRepositoryRepository repositoryRepository) throws Exception;

    public static class RequestToJsonDecoder {
        public static JSONObject toJson(HttpServletRequest request) throws JSONException {
            StringBuilder stringBuffer = new StringBuilder();
            String line;

            try {
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null)
                    stringBuffer.append(line);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String jsonString = stringBuffer.toString();
            return new JSONObject(jsonString);
        }
    }
}
