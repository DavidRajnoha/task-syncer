package com.redhat.tasksyncer.services.repositories;

import com.redhat.tasksyncer.dao.accessors.project.ProjectAccessor;
import com.redhat.tasksyncer.dao.accessors.repository.RepositoryAccessor;

import java.util.Map;



public abstract class AbstractRepositoryService {


    protected Map<String, RepositoryAccessor> repositoryAccessors;
    protected ProjectAccessor projectAccessor;


    public AbstractRepositoryService(Map<String, RepositoryAccessor> repositoryAccessors, ProjectAccessor projectAccessor
    ) {
        this.repositoryAccessors = repositoryAccessors;
        this.projectAccessor = projectAccessor;
    }

    protected RepositoryAccessor getRepositoryAccessor(String componentType){
        return repositoryAccessors.get(componentType.toLowerCase() + "RepositoryAccessor");
    }



}
