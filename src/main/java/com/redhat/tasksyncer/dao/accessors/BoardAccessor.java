package com.redhat.tasksyncer.dao.accessors;

import com.redhat.tasksyncer.dao.entities.AbstractBoard;
import com.redhat.tasksyncer.dao.entities.AbstractCard;
import com.redhat.tasksyncer.dao.entities.AbstractColumn;

import java.util.List;

/**
 * @author Filip Cap
 */
public abstract class BoardAccessor {
    public abstract AbstractBoard createItself();
    public abstract AbstractCard update(AbstractCard card);
    public abstract List<AbstractColumn> getColumns();

    public abstract void save();

    public abstract ColumnAccessor createColumn(String name);
}
