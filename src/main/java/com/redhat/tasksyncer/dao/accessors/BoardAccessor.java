package com.redhat.tasksyncer.dao.accessors;

import com.redhat.tasksyncer.dao.entities.AbstractBoard;
import com.redhat.tasksyncer.dao.entities.AbstractCard;

/**
 * @author Filip Cap
 */
public abstract class BoardAccessor {
    public abstract AbstractBoard createItself();
    public abstract AbstractCard update(AbstractCard card);
}
