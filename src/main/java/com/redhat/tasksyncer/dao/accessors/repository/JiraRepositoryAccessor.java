package com.redhat.tasksyncer.dao.accessors.repository;

import com.redhat.tasksyncer.dao.entities.repositories.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.repositories.JiraRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author David Rajnoha
 * */

@Component
public class JiraRepositoryAccessor extends RepositoryAccessor {

    @Autowired
    public JiraRepositoryAccessor(AbstractRepositoryRepository repositoryRepository){
        this.repositoryRepository = repositoryRepository;
    }


    @Override
    public AbstractRepository createRepositoryOfType() {
        return new JiraRepository();
    }

    @Override
    public Map<String, String> isMappingValid(Map<String, String> mapping) {
        Map<String, String> upperCaseMap = new LinkedHashMap<>();

        for (String key : mapping.keySet()){
            String value  = mapping.get(key);
            key = key.toUpperCase();
            upperCaseMap.put(key, value);
        }

        return upperCaseMap;
    }
}
