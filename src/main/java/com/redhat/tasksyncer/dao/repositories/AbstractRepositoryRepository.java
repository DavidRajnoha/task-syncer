package com.redhat.tasksyncer.dao.repositories;

import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author Filip Cap
 */
public interface AbstractRepositoryRepository extends CrudRepository<AbstractRepository, Long> {
    AbstractRepository findByRepositoryNameAndProject_Id(String repositoryName, Long projectId);
    List<AbstractRepository> findByProject_Name(String projectName);
}
