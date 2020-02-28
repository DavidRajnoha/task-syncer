package com.redhat.tasksyncer.services.repositories;

import com.redhat.tasksyncer.dao.accessors.project.ProjectAccessor;
import com.redhat.tasksyncer.dao.accessors.repository.RepositoryAccessor;
import com.redhat.tasksyncer.dao.entities.projects.Project;
import com.redhat.tasksyncer.exceptions.InvalidMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class LocalRepositoryService extends AbstractRepositoryService {


    @Autowired
    public LocalRepositoryService(Map<String, RepositoryAccessor> repositoryAccessors,
                             ProjectAccessor projectAccessor
    ) {
        super(repositoryAccessors, projectAccessor);
    }


    public void createRepository(String firstLoginCredential, String secondLoginCredential, String repoName,
                                 String repoNamespace, String projectName, String repoType, List<String> columnMappingKeys,
                                 List<String> customNames) throws InvalidMappingException {

        Project project = projectAccessor.getProject(projectName);
        RepositoryAccessor repositoryAccessor = getRepositoryAccessor(repoType);

        areColumnNamesValid(customNames, project.getColumnNames().orElseThrow(() -> new InvalidMappingException("")));
        Map<String, String> columnMapping = getColumnMapping(columnMappingKeys, customNames);

        repositoryAccessor.createRepository(firstLoginCredential, secondLoginCredential, repoName, repoNamespace,
                project, columnMapping);
    }


    private void areColumnNamesValid(List<String> customNames, List<String> projectColumnNames) throws InvalidMappingException {
        for (String customName : customNames) {
            if (! projectColumnNames.contains(customName)){
                throw new InvalidMappingException("columns you wish to " +
                        "map to do not exist in this project");
            }
        }
    }

    private Map<String, String> getColumnMapping(List<String> columnMappingKeys, List<String> customNames)
            throws InvalidMappingException {

        Map<String, String> columnMapping = new LinkedHashMap<>();
        if (columnMappingKeys.size() <= customNames.size()) {
            for (int i = 0; i < columnMappingKeys.size(); i++) {
                columnMapping.put(columnMappingKeys.get(i), customNames.get(i));
            }
            return columnMapping;
        } else {
            throw new InvalidMappingException("The number of mapped parameters doesn't" +
                    " match the number of columns");
        }

    }










}
