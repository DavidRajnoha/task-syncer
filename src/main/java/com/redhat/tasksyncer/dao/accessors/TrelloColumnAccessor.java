package com.redhat.tasksyncer.dao.accessors;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.TList;
import com.redhat.tasksyncer.dao.entities.TrelloColumn;
import com.redhat.tasksyncer.dao.repositories.AbstractColumnRepository;

/**
 * @author Filip Cap
 */
public class TrelloColumnAccessor extends ColumnAccessor {
    private TrelloColumn column;
    private AbstractColumnRepository columnRepository;
    private Trello trelloApi;

    public TrelloColumnAccessor(TrelloColumn column, AbstractColumnRepository columnRepository, Trello trelloApi) {

        this.column = column;
        this.columnRepository = columnRepository;
        this.trelloApi = trelloApi;
    }

    public TrelloColumnAccessor createItself() {
        TList list = trelloApi.createList(column.getName(), column.getBoard().getRemoteBoardId());
        column.setRemoteColumnId(list.getId());

        this.save();
        return this;
    }

    public void save() {
        this.column = columnRepository.save(column);
    }
}
