package com.redhat.tasksyncer.decoders;

import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.webhook.IssueEvent;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractWebhookIssueDecoder {

    public static AbstractWebhookIssueDecoder getInstance(String serviceType) throws RepositoryTypeNotSupportedException {
        AbstractWebhookIssueDecoder issueDecoder;

        switch (serviceType){
            case "gitlab":
                issueDecoder = new GitlabWebhookIssueDecoder();
                break;
            case "github":
                issueDecoder = new GithubWebhookIssueDecoder();
                break;
            case "jira":
                issueDecoder = new JiraWebhookIssueDecoder();
                break;
            default:
                throw new RepositoryTypeNotSupportedException("");
        }

        return issueDecoder;
    }

    public abstract AbstractIssue decode(HttpServletRequest request, Project project, AbstractRepositoryRepository repositoryRepository) throws Exception;
}
