package com.redhat.tasksyncer.decoders;
import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.JiraIssue;
import com.redhat.tasksyncer.dao.entities.Project;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.InvalidWebhookCallbackException;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;


public class JiraWebhookIssueDecoder extends AbstractWebhookIssueDecoder {

    @Override
    public AbstractIssue decode(HttpServletRequest request, Project project, AbstractRepositoryRepository repositoryRepository) throws Exception {
//        JSONObject input = requestToInput(request);
        JSONObject input = AbstractWebhookIssueDecoder.RequestToJsonDecoder.toJson(request);

        // TODO: Decide not based on input.has("issue") but based on the issue.getString("webhookEvent") value
        if (!input.has("issue")){
            throw new InvalidWebhookCallbackException("Callback has no atribute for Issue");
        }

        AbstractIssue issue = JiraIssue.ObjectToJiraIssueConverter.convert(input);

        AbstractRepository repository = repositoryRepository.findByRepositoryNameAndProject_Id(input.getJSONObject("issue").getJSONObject("fields").getJSONObject("project").getString("key"), project.getId());
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
