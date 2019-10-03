package com.redhat.tasksyncer.dao.accessors;

import com.redhat.tasksyncer.dao.entities.AbstractBoard;
import com.redhat.tasksyncer.dao.entities.AbstractCard;
import com.redhat.tasksyncer.dao.entities.AbstractColumn;
import com.redhat.tasksyncer.dao.entities.Project;

import java.io.IOException;
import java.util.List;

/**
 * @author Filip Cap
 */
public abstract class BoardAccessor {
    public abstract AbstractBoard createItself();
    public abstract AbstractCard update(AbstractCard card);
    public abstract List<AbstractColumn> getColumns();
    public abstract void setProject(Project project);

    public abstract void save();

    public abstract ColumnAccessor createColumn(String name);

    public abstract String deleteBoard(String trelloApplicationKey, String trelloAccessToken) throws IOException;
}
