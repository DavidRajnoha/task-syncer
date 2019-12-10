package com.redhat.tasksyncer.decoders;

import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.GitlabIssue;
import com.redhat.tasksyncer.dao.entities.Project;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.InvalidWebhookCallbackException;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.webhook.IssueEvent;
import org.gitlab4j.api.webhook.WebHookListener;
import org.gitlab4j.api.webhook.WebHookManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Filip Cap
 */


@PropertySource("classpath:other.properties")
@Component
public class GitlabWebhookIssueDecoder extends AbstractWebhookIssueDecoder{
    private WebHookManager webHookManager = new WebHookManager(); // todo: check secret token
    private IssueEvent ie;


    @Autowired
    public GitlabWebhookIssueDecoder(AbstractRepositoryRepository repositoryRepository) {
        webHookManager.addListener(new WebHookListener() {
            @Override
            public void onIssueEvent(IssueEvent event) {
                ie = event;
            }
        });
        this.repositoryRepository = repositoryRepository;
    }



    public AbstractIssue decode
            (HttpServletRequest request, Project project)
            throws  InvalidWebhookCallbackException {
        try {
            webHookManager.handleEvent(request);
        } catch (GitLabApiException e){
            e.printStackTrace();
            throw new InvalidWebhookCallbackException("Could not handle the webhook content");
        }

        IssueEvent ie = this.ie;
        this.ie = null;

        IssueEvent.ObjectAttributes oa = ie.getObjectAttributes();
        AbstractRepository repository = repositoryRepository
                .findByRepositoryNameAndProject_Id(parseRepositoryName(ie.getRepository().getName()), project.getId());

        AbstractIssue issue = GitlabIssue.ObjectToGitlabIssueConverter.convert(oa, repository.getColumnMapping());
        issue.setRepository(repository);

        return issue;
    }

    //When calling the Gitlab api, the spaces in the repo name are replaced with dashes.
    //However, when we get the repo name from the web hook issue event, it is in the original form without dashes, so
    //it is necessary to parse it so it matches the data in our repository
    private String parseRepositoryName(String name) {
        return name.toLowerCase().replaceAll(" ", "-");
    }
}
