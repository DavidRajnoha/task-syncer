package com.redhat.tasksyncer.dao.accessors.repository;

import com.redhat.tasksyncer.dao.entities.projects.Project;
import com.redhat.tasksyncer.dao.entities.repositories.AbstractRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.InvalidMappingException;

import java.util.Map;

/**
 * @author Filip Cap, David Rajnoha
 */
public abstract class RepositoryAccessor {

    protected AbstractRepositoryRepository repositoryRepository;
    protected AbstractRepository repository;


    public abstract AbstractRepository createRepositoryOfType();

    protected abstract Map<String, String> isMappingValid(Map<String, String> mapping) throws InvalidMappingException;


    public AbstractRepository getRepository(String name, String projectName){
        return repositoryRepository.findByRepositoryNameAndProject_Name(name, projectName);
    }


    public AbstractRepository createRepository(String firstLoginCredential, String secondLoginCredential,
                                               String repoName, String repoNamespace, Project project,
                                               Map<String, String> columnMapping) throws InvalidMappingException {
        AbstractRepository repository = createRepositoryOfType();
        repository.setFirstLoginCredential(firstLoginCredential);
        repository.setSecondLoginCredential(secondLoginCredential);
        repository.setRepositoryName(repoName);
        repository.setRepositoryNamespace(repoNamespace);
        repository.setProject(project);
        repository.setColumnMapping(isMappingValid(columnMapping));

        return repositoryRepository.save(repository);

    };

}