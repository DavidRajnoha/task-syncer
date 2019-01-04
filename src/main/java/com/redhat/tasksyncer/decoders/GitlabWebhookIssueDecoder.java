package com.redhat.tasksyncer.decoders;

import com.redhat.tasksyncer.dao.entities.Issue;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.webhook.IssueEvent;
import org.gitlab4j.api.webhook.WebHookListener;
import org.gitlab4j.api.webhook.WebHookManager;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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

    public Issue decode(HttpServletRequest request) throws GitLabApiException {
        webHookManager.handleEvent(request);

        IssueEvent ie = this.ie;
        this.ie = null;

        IssueEvent.ObjectAttributes oa = ie.getObjectAttributes();

        Issue i = new Issue(oa);
        i.setType(Issue.GITLAB_ISSUE);

        return i;
    }
}
