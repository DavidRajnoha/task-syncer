package com.redhat.tasksyncer.dao.accessors;

import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Filip Cap
 */
public abstract class RepositoryAccessor {
    public abstract List<AbstractIssue> downloadAllIssues() throws Exception;

    public abstract AbstractIssue saveIssue(AbstractIssue issue);

    public abstract Optional<AbstractIssue> getIssue(AbstractIssue issue);

    public abstract void save();

    public abstract AbstractRepository createItself();

}
