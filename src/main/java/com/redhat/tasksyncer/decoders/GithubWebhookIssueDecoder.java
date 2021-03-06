package com.redhat.tasksyncer.decoders;

import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.repositories.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.issues.GithubIssue;
import com.redhat.tasksyncer.dao.entities.projects.Project;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.InvalidWebhookCallbackException;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author David Rajnoha
 * */

@Component
public class GithubWebhookIssueDecoder extends AbstractWebhookIssueDecoder {

    @Autowired
    public GithubWebhookIssueDecoder(AbstractRepositoryRepository repositoryRepository){
        this.repositoryRepository = repositoryRepository;
    }



    public AbstractIssue decode(HttpServletRequest request, Project project) throws
            InvalidWebhookCallbackException {
        GHEventPayload.Issue issueEventPayload;
        try {
             issueEventPayload = GitHub.connectAnonymously().parseEventPayload(request.getReader(), GHEventPayload.Issue.class);
        } catch (IOException e){
            e.printStackTrace();
            throw new InvalidWebhookCallbackException("The callback was not parsed correctly");
        }
        AbstractRepository repository = (repositoryRepository.findByRepositoryNameAndProject_Id(issueEventPayload.getRepository().getName(), project.getId()));

        AbstractIssue gitHubIssue = GithubIssue.ObjectToGithubIssueConverter.convert(issueEventPayload.getIssue(),
                repository.getColumnMapping());

        gitHubIssue.setRepository(repository);

        return gitHubIssue;
    }
}
