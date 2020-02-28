package com.redhat.tasksyncer.dao.accessors.repository;

import com.redhat.tasksyncer.dao.entities.trello.AbstractColumn;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.repositories.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.repositories.GitlabRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.InvalidMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Filip Cap
 */
@Component
public class GitlabRepositoryAccessor extends RepositoryAccessor {


    @Autowired
    public GitlabRepositoryAccessor(AbstractRepositoryRepository repositoryRepository) {
        this.repositoryRepository = repositoryRepository;
    }

    @Override
    public AbstractRepository createRepositoryOfType() {
        AbstractRepository gitlabRepository = new GitlabRepository();
        Map<String, String> defaultColumnMapping = new HashMap<>();

        defaultColumnMapping.put(AbstractIssue.STATE_OPENED, AbstractColumn.TODO_DEFAULT);
        defaultColumnMapping.put(AbstractIssue.STATE_REOPENED, AbstractColumn.TODO_DEFAULT);
        defaultColumnMapping.put(AbstractIssue.STATE_CLOSED, AbstractColumn.DONE_DEFAULT);

        gitlabRepository.setColumnMapping(defaultColumnMapping);

        return gitlabRepository;
    }


    @Override
    public Map<String, String> isMappingValid(Map<String, String> mapping) throws InvalidMappingException {
        if (mapping.size() != 3){
            throw new InvalidMappingException("The mapping for gitlab must contain exactly three entries");
        }

//        Boolean isValid = true;
//        mapping.keySet().forEach(key -> {
//            if (!key.equals(AbstractIssue.STATE_OPENED) && !key.equals(AbstractIssue.STATE_CLOSED) &&
//            !key.equals(AbstractIssue.STATE_REOPENED)) {
//                isValid
//            }
//            }
//        });

        for (String key : mapping.keySet()) {
            if (!key.equals(AbstractIssue.STATE_OPENED) && !key.equals(AbstractIssue.STATE_CLOSED) &&
                    !key.equals(AbstractIssue.STATE_REOPENED)) {
                throw new InvalidMappingException("The mapping contains unknown key: " + key);
            }
        }


        // WARNING: Repository might not be initialized
        List<String> columnNames = repository.getProject().getColumnNames().orElseThrow(() -> new InvalidMappingException("msg"));
        for (String value : mapping.values()) {
            if (!columnNames.contains(value)) {
                throw new InvalidMappingException("The mapping contains non existing column: "
                        + value);
            }
        }

        return mapping;
    }

}
