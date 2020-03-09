package com.redhat.tasksyncer.services.webhooks;

import com.redhat.tasksyncer.dao.accessors.issue.AbstractIssueAccessor;
import com.redhat.tasksyncer.dao.accessors.project.ProjectAccessor;
import com.redhat.tasksyncer.presentation.trello.TrelloCardAccessor;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.decoders.AbstractWebhookIssueDecoder;
import com.redhat.tasksyncer.exceptions.InvalidWebhookCallbackException;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;
import com.redhat.tasksyncer.exceptions.TrelloCalllbackNotAboutCardException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public class WebhookService {

    private AbstractIssueAccessor issueAccessor;
    private ProjectAccessor projectAccessor;
    private Map<String, AbstractWebhookIssueDecoder> webhookIssueDecoderMap;
    private TrelloCardAccessor cardAccessor;

    @Autowired
    public WebhookService(ProjectAccessor projectAccessor, Map<String, AbstractWebhookIssueDecoder> webhookIssueDecoderMap,
                          AbstractIssueAccessor issueAccessor) {
        this.issueAccessor = issueAccessor;
        this.projectAccessor = projectAccessor;
        this.webhookIssueDecoderMap = webhookIssueDecoderMap;
    }

    public AbstractIssue processHook(String projectName, HttpServletRequest request, String repositoryType) throws RepositoryTypeNotSupportedException, TrelloCalllbackNotAboutCardException, InvalidWebhookCallbackException {
        AbstractWebhookIssueDecoder webhookIssueDecoder = findWebhookIssueDecoder(repositoryType);


        AbstractIssue newIssue = webhookIssueDecoder.decode(request, projectAccessor.getProject(projectName));

        return issueAccessor.update(newIssue);
    }


    private AbstractWebhookIssueDecoder findWebhookIssueDecoder(String serviceName) throws RepositoryTypeNotSupportedException {
        String serviceType = serviceName.toLowerCase().concat("WebhookIssueDecoder");
        AbstractWebhookIssueDecoder webhookIssueDecoder = webhookIssueDecoderMap.get(serviceType);
        if (webhookIssueDecoder == null){
            throw new RepositoryTypeNotSupportedException("Service type: " + serviceType + " not supported for processing" +
                    "webhooks ");
        }
        return webhookIssueDecoder;
    }
}
