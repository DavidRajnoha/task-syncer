package com.redhat.tasksyncer.dao.accessors;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.TList;
import com.redhat.tasksyncer.dao.entities.TrelloColumn;
import com.redhat.tasksyncer.dao.repositories.AbstractColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Service;

/**
 * @author Filip Cap
 */
@Service
@EntityScan(basePackages = {"com.redhat.tasksyncer.dao.entities"})
public class TrelloColumnAccessor extends ColumnAccessor {
    private AbstractColumnRepository columnRepository;
    private Trello trelloApi;

    @Autowired
    public TrelloColumnAccessor(AbstractColumnRepository columnRepository) {

        this.columnRepository = columnRepository;

    }

    public TrelloColumnAccessor createItself(Trello trelloApi) {
        this.trelloApi = trelloApi;
        return this;
    }

    public TrelloColumn createColumn(String name, String remoteBoardId){
        TList list = trelloApi.createList(name, remoteBoardId);

        TrelloColumn column = new TrelloColumn();
        column.setRemoteColumnId(list.getId());
        column.setName(name);

        return column;
    }

    public void save(TrelloColumn column) {
        columnRepository.save(column);
    }
}
