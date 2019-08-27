package com.redhat.tasksyncer.decoders;

import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.GithubIssue;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GitHub;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Filip Cap
 */
public class GithubWebhookIssueDecoder {


    public AbstractIssue decode(HttpServletRequest request) throws IOException {
        GHEventPayload.Issue issueEventPayload = GitHub.connectAnonymously().parseEventPayload(request.getReader(), GHEventPayload.Issue.class);
        AbstractIssue gitHubIssue = GithubIssue.ObjectToGithubIssueConverter.convert(issueEventPayload.getIssue());
        return gitHubIssue;
    }
}
