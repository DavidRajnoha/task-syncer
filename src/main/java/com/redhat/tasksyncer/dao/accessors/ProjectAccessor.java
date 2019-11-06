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


    /**
     * Deletes the board associated with the boardAccessor associated with the projectAccessor
     *
     * @throws CannotConnectToRepositoryException when the trello login credentials are not false or when the connection
     *                                            with trello is not working
     */
    // TODO pass the board as an argument
    void deleteBoard(String trelloApplicationKey, String trelloAccessToken) throws CannotConnectToRepositoryException;

    /**
     * @param project - the project you wish to access and modify with this project accessor
     *                <p>
     *                Adds project to this.project field, so the project can be modified by other methods of the projectAccessor
     * @return project passed as an argument with filled in id
     */
    Project saveAndInitialize(Project project);


    /**
     * @param boardType - type of the board you wish to display your issues on, currently is only trello supported
     * @param boardName - Name of the board you wish to create
     */
    void initialize(String boardType, String boardName);

    /**
     * Adds remote repository to the local project and gets the issues from the repository
     *
     * @param repository you wish to add to the project
     */
    RepositoryAccessor addRepository(AbstractRepository repository) throws RepositoryTypeNotSupportedException, CannotConnectToRepositoryException;

    /**
     * @param newIssue - AbstractIssue you wish to update
     * @return AbstractIssue - if the newIssue is new (issue with same remoteIssueId and repository does not exist in local database)
     * then returns newIssue
     * If the issue already exists locally, then returns the old issues with updated properties based on
     * the new issue
     */
    AbstractIssue update(AbstractIssue newIssue);


    /**
     * @param issue - the card of this issue will be updated
     */
    AbstractIssue updateCard(AbstractIssue issue);

    /**
     * Updates the issue
     * Updates the card of the issue
     * Saves the issue
     */
    void syncIssue(AbstractIssue issue);

    /**
     * Creates a webhook pointing to tasksyncer on a remote service
     *
     * @throws RepositoryTypeNotSupportedException - The creation of webhooks on Jira is not implemented yet
     */
    void hookRepository(AbstractRepository repository, String webhookUrl) throws RepositoryTypeNotSupportedException,
            IOException, SynchronizationFailedException, GitLabApiException, CannotConnectToRepositoryException;

    /**
     * Saves the project associated with the projectAccessor
     */
    void save();

    /**
     * Deletes the project passed as argument
     */
    void deleteProject(Project project);

    /**
     * Updates the properties of the card and syncs the card with remote board
     */
    AbstractIssue setCard(AbstractIssue newGithubIssue);

    RepositoryAccessor createRepositoryAccessor(AbstractRepository repository)
            throws RepositoryTypeNotSupportedException, CannotConnectToRepositoryException;
}