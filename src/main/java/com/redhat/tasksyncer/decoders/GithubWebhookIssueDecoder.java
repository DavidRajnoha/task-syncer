package com.redhat.tasksyncer.decoders;

import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.GithubIssue;
import com.redhat.tasksyncer.dao.entities.Project;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GitHub;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Filip Cap
 */
public class GithubWebhookIssueDecoder extends AbstractWebhookIssueDecoder {


    public AbstractIssue decode(HttpServletRequest request, Project project, AbstractRepositoryRepository repositoryRepository) throws IOException {
        GHEventPayload.Issue issueEventPayload = GitHub.connectAnonymously().parseEventPayload(request.getReader(), GHEventPayload.Issue.class);

        AbstractIssue gitHubIssue = GithubIssue.ObjectToGithubIssueConverter.convert(issueEventPayload.getIssue());
        gitHubIssue.setRepository(repositoryRepository.findByRepositoryNameAndProject_Id(issueEventPayload.getRepository().getName(), project.getId()));

        return gitHubIssue;
    }

//    public AbstractIssue decode(HttpServletRequest request, AbstractRepositoryRepository repositoryRepository, Long projectId) throws IOException {
//        GHEventPayload.Issue issueEventPayload = GitHub.connectAnonymously().parseEventPayload(request.getReader(), GHEventPayload.Issue.class);
//        AbstractIssue gitHubIssue = GithubIssue.ObjectToGithubIssueConverter.convert(issueEventPayload.getIssue());
//        AbstractRepository repository = repositoryRepository.findByRepositoryNameAndProject_Id(issueEventPayload.getRepository().getName(), projectId);
//        gitHubIssue.setRepository(repository);
//        return gitHubIssue;
//    }
}
