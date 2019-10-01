package com.redhat.tasksyncer.decoders;

import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.GitlabIssue;
import com.redhat.tasksyncer.dao.entities.Project;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.webhook.IssueEvent;
import org.gitlab4j.api.webhook.WebHookListener;
import org.gitlab4j.api.webhook.WebHookManager;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Filip Cap
 */
public class GitlabWebhookIssueDecoder extends AbstractWebhookIssueDecoder{
    private WebHookManager webHookManager = new WebHookManager(); // todo: check secret token
    private IssueEvent ie;

    public GitlabWebhookIssueDecoder() {
        webHookManager.addListener(new WebHookListener() {
            @Override
            public void onIssueEvent(IssueEvent event) {
                ie = event;
            }
        });
    }

    public AbstractIssue decode(HttpServletRequest request, Project project, AbstractRepositoryRepository repositoryRepository) throws GitLabApiException {
        webHookManager.handleEvent(request);

        IssueEvent ie = this.ie;
        this.ie = null;

        IssueEvent.ObjectAttributes oa = ie.getObjectAttributes();

        AbstractIssue issue = GitlabIssue.ObjectToGitlabIssueConverter.convert(oa);
        issue.setRepository(repositoryRepository.findByRepositoryNameAndProject_Id(parseRepositoryName(ie.getRepository().getName()), project.getId()));

        return issue;
    }

    //When calling the Gitlab api, the spaces in the repo name are replaced with dashes.
    //However, when we get the repo name from the web hook issue event, it is in the original form without dashes, so
    //it is necessary to parse it so it matches the data in our repository
    private String parseRepositoryName(String name) {
        return name.toLowerCase().replaceAll(" ", "-");
    }
}
