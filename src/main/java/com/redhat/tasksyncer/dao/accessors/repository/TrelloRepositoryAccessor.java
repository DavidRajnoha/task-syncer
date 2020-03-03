package com.redhat.tasksyncer.dao.accessors.repository;

import com.redhat.tasksyncer.dao.entities.repositories.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.repositories.TrelloRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.InvalidMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


/**
 * @author David Rajnoha
 * */

@Component
public class TrelloRepositoryAccessor extends RepositoryAccessor{

    @Autowired
    public TrelloRepositoryAccessor(AbstractRepositoryRepository repositoryRepository){
        this.repositoryRepository = repositoryRepository;
    }

    @Override
    public AbstractRepository createRepositoryOfType() {
        return new TrelloRepository();
    }

    @Override
    public Map<String, String> isMappingValid(List<String> columnNames, Map<String, String> mapping)
            throws InvalidMappingException {

//        TODO: make this work
//        List<String> tListIds = trelloApi.getBoard(repository.getProject().getBoard().getRemoteBoardId()).getLists()
//                .stream().map(TList::getId).collect(Collectors.toList());
//
//        for (String columnId : mapping.keySet()){
//            if (!tListIds.contains(columnId)){
//                throw new InvalidMappingException("Mapped list doesn't exist in your trello board");
//            }
//        }

        return mapping;
    }
}
