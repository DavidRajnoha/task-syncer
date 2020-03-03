package com.redhat.tasksyncer.dao.accessors.repository;

import com.redhat.tasksyncer.dao.entities.repositories.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.repositories.GithubRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.InvalidMappingException;
import org.kohsuke.github.GHIssueState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
* @author David Rajnoha
* */
@Component
public class GithubRepositoryAccessor extends RepositoryAccessor {


    @Autowired
    public GithubRepositoryAccessor(AbstractRepositoryRepository repositoryRepository, AbstractIssueRepository issueRepository) {
        this.repositoryRepository = repositoryRepository;
    }

    @Override
    public AbstractRepository createRepositoryOfType() {
        return new GithubRepository();
    }

    @Override
    public Map<String, String> isMappingValid(List<String> columnNames, Map<String, String> mapping) throws InvalidMappingException {

        Map<String, String> upperCaseMap = new LinkedHashMap<>();

        for (String key : mapping.keySet()){
            String value  = mapping.get(key);

            key = key.toUpperCase();

            if (!key.equals(GHIssueState.OPEN.name()) &&
                    !key.equals(GHIssueState.CLOSED.name())){
                throw new InvalidMappingException("State " + key + " is not valid GitHub Issue state");
            }

            upperCaseMap.put(key, value);
        }

        return upperCaseMap;
    }

}
