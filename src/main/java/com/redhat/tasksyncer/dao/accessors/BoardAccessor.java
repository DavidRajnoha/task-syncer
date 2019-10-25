package com.redhat.tasksyncer.dao.accessors;

import com.redhat.tasksyncer.dao.entities.*;

import java.io.IOException;
import java.util.List;

/**
 * @author Filip Cap
 */
public interface BoardAccessor {
    public abstract AbstractBoard createBoard();
    public abstract AbstractCard update(AbstractCard card);

    public abstract BoardAccessor initializeAndSave(AbstractBoard board, String trelloApplicationKey, String trelloAccessToken);

    public abstract List<AbstractColumn> getColumns();
    public abstract void setProject(Project project);

    public abstract void save();

    public abstract ColumnAccessor createColumn(String name);

    public abstract String deleteBoard(String trelloApplicationKey, String trelloAccessToken) throws IOException;
}
