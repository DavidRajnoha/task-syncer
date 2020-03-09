package com.redhat.tasksyncer.presentation.trello;

import com.julienvey.trello.domain.TList;
import com.redhat.tasksyncer.dao.entities.trello.AbstractColumn;
import com.redhat.tasksyncer.dao.entities.trello.TrelloColumn;
import com.redhat.tasksyncer.dao.entities.projects.Project;
import com.redhat.tasksyncer.dao.repositories.AbstractColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Filip Cap
 */
@Service
@EntityScan(basePackages = {"com.redhat.tasksyncer.dao.entities"})
public class TrelloColumnAccessor extends AbstractTrelloAccessor{
    private AbstractColumnRepository columnRepository;

    @Autowired
    public TrelloColumnAccessor(AbstractColumnRepository columnRepository) {

        this.columnRepository = columnRepository;

    }

    public void createColumns(Project project){

        List<String> columnNames = project.getColumnNames().orElseGet(() -> {
            List<String> cN = new ArrayList<>();
            cN.add(AbstractColumn.TODO_DEFAULT);
            cN.add(AbstractColumn.DONE_DEFAULT);
            return cN;
        });


        // For each name from the list column names creates column on the trello board
        columnNames.forEach(columnName -> {
            AbstractColumn column = createColumn(columnName, project.getBoard().getRemoteBoardId());
            column.setBoard(project.getBoard());
            columnRepository.save(column);
        });

    }

    private TrelloColumn createColumn(String name, String remoteBoardId){
        TList list = trelloApi.createList(name, remoteBoardId);

        TrelloColumn column = new TrelloColumn();
        column.setRemoteColumnId(list.getId());
        column.setName(name);

        return column;
    }

    public List<AbstractColumn> getColumns(Project project){
        return columnRepository.findByBoard_Id(project.getBoard().getId());
    }

    public void save(TrelloColumn column) {
        columnRepository.save(column);
    }
}
