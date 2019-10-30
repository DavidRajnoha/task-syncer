package com.redhat.tasksyncer.dao.accessors;

import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.Project;
import com.redhat.tasksyncer.exceptions.CannotConnectToRepositoryException;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;
import com.redhat.tasksyncer.exceptions.SynchronizationFailedException;
import org.gitlab4j.api.GitLabApiException;

import java.io.IOException;


/**
 * @author Filip Cap, David Rajnoha
 */
public interface ProjectAccessor {

    void deleteBoard(String trelloApplicationKey, String trelloAccessToken) throws CannotConnectToRepositoryException;

    Project saveAndInitialize(Project project);

    void initialize(String boardType, String boardName);

    RepositoryAccessor addRepository(AbstractRepository repository) throws  RepositoryTypeNotSupportedException, CannotConnectToRepositoryException;

    AbstractIssue update(AbstractIssue newIssue);

    AbstractIssue updateCard(AbstractIssue issue);

    void syncIssue(AbstractIssue issue);

    void hookRepository(AbstractRepository repository, String webhookUrl) throws RepositoryTypeNotSupportedException,
            IOException, SynchronizationFailedException, GitLabApiException, CannotConnectToRepositoryException;

    void save();

    void deleteProject(Project project);

    AbstractIssue setCard(AbstractIssue newGithubIssue);
}