package com.redhat.tasksyncer.decoders;

import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.GithubIssue;
import com.redhat.tasksyncer.dao.entities.Project;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.InvalidWebhookCallbackException;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GitHub;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author David Rajnoha
 * */
public class GithubWebhookIssueDecoder extends AbstractWebhookIssueDecoder {


    public AbstractIssue decode(HttpServletRequest request, Project project, AbstractRepositoryRepository repositoryRepository) throws
            InvalidWebhookCallbackException {
        GHEventPayload.Issue issueEventPayload;
        try {
             issueEventPayload = GitHub.connectAnonymously().parseEventPayload(request.getReader(), GHEventPayload.Issue.class);
        } catch (IOException e){
            e.printStackTrace();
            throw new InvalidWebhookCallbackException("The callback was not parsed correctly");
        }
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
