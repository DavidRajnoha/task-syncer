package com.redhat.tasksyncer.dao.accessors.remoteRepository;

import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.repositories.AbstractRepository;
import com.redhat.tasksyncer.exceptions.CannotConnectToRepositoryException;
import org.gitlab4j.api.GitLabApiException;

import java.io.IOException;
import java.util.List;

/**
 * @author Filip Cap, David Rajnoha
 */
public abstract class RemoteRepositoryAccessor {
    protected AbstractRepository repository;


    // Online service accessing

    protected abstract void connectToRepository() throws IOException;

    public abstract List<AbstractIssue> downloadAllIssues() throws IOException, GitLabApiException;

    public abstract void createWebhook(String webhook) throws IOException, GitLabApiException;

    /**
     * Creates new repository accessor that is connected to the external service (for example the trelloApi etc. fields
     * are initiated and ready to communicate with the ext. service)
     * */
    public RemoteRepositoryAccessor getConnectedInstance(AbstractRepository repository) throws CannotConnectToRepositoryException {

        this.repository = repository;

        try {
            this.connectToRepository();
        } catch (IOException exception){
            throw new CannotConnectToRepositoryException(exception.getMessage());
        }

        return this;
    }




}