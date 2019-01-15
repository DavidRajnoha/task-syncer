package com.redhat.tasksyncer.decoders;

import com.redhat.tasksyncer.dao.entities.GitlabIssue;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.webhook.IssueEvent;
import org.gitlab4j.api.webhook.WebHookListener;
import org.gitlab4j.api.webhook.WebHookManager;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Filip Cap
 */
public class GitlabWebhookIssueDecoder {
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

    public GitlabIssue decode(HttpServletRequest request) throws GitLabApiException {
        webHookManager.handleEvent(request);

        IssueEvent ie = this.ie;
        this.ie = null;

        IssueEvent.ObjectAttributes oa = ie.getObjectAttributes();

        GitlabIssue issue = new GitlabIssue();
        issue.setRemoteIssueId(oa.getId().toString());
        issue.setTitle(oa.getTitle());
        issue.setDescription(oa.getDescription());

        return issue;
    }
}
