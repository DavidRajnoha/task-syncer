package com.redhat.tasksyncer.dao.accessors;

import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;
import com.redhat.tasksyncer.exceptions.SynchronizationFailedException;

import java.io.IOException;

public interface ProjectAccessor {

    BoardAccessor getBoardAccessor();

    void deleteBoard(String trelloApplicationKey, String trelloAccessToken) throws IOException;

    Project saveAndInitialize(Project project);

    void save();

    void initialize(String boardType, String boardName);

    RepositoryAccessor addRepository(AbstractRepository repository) throws SynchronizationFailedException, IOException, RepositoryTypeNotSupportedException;

    AbstractIssue update(AbstractIssue newIssue);

    AbstractIssue updateCard(AbstractIssue issue);

    void syncIssue(AbstractIssue issue);

    void hookRepository(AbstractRepository repository, String webhookUrl) throws Exception;


    void deleteProject(Project project);
}
