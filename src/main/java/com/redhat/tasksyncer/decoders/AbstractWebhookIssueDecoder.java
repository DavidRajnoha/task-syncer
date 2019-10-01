package com.redhat.tasksyncer.decoders;

import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.GitlabIssue;
import com.redhat.tasksyncer.dao.entities.Project;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.webhook.IssueEvent;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractWebhookIssueDecoder {
    public abstract AbstractIssue decode(HttpServletRequest request, Project project, AbstractRepositoryRepository repositoryRepository) throws Exception;
}
