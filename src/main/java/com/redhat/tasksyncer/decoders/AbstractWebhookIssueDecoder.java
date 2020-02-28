package com.redhat.tasksyncer.decoders;

import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.projects.Project;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.InvalidWebhookCallbackException;
import com.redhat.tasksyncer.exceptions.TrelloCalllbackNotAboutCardException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author David Rajnoha
 * */
public abstract class AbstractWebhookIssueDecoder {

    protected AbstractRepositoryRepository repositoryRepository;

    public abstract AbstractIssue decode(HttpServletRequest request, Project project)
            throws InvalidWebhookCallbackException, TrelloCalllbackNotAboutCardException;

    public static class RequestToJsonDecoder {
        public static JSONObject toJson(HttpServletRequest request) throws InvalidWebhookCallbackException {
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
            try {
                return new JSONObject(jsonString);
            } catch (JSONException e){
                e.printStackTrace();
                throw new InvalidWebhookCallbackException("Converting the body to JSON object");
            }
        }
    }
}
