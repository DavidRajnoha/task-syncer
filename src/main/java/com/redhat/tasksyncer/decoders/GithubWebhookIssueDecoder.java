package com.redhat.tasksyncer.decoders;

import com.redhat.tasksyncer.dao.entities.Issue;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GitHub;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Filip Cap
 */
public class GithubWebhookIssueDecoder {

    public Issue decode(HttpServletRequest request) throws IOException {
        GitHub gh = GitHub.offline();
        GHEventPayload.Issue i = gh.parseEventPayload(request.getReader(), GHEventPayload.Issue.class);

        return new Issue(i.getIssue());
    }
}
