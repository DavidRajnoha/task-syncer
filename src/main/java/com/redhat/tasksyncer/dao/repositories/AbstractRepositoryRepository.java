package com.redhat.tasksyncer.dao.repositories;

import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Filip Cap
 */
public interface AbstractRepositoryRepository extends JpaRepository<AbstractRepository, Long> {
    AbstractRepository findByRepositoryNameAndProject_Id(String repositoryName, Long projectId);
    List<AbstractRepository> findByProject_Name(String projectName);
    Optional<AbstractRepository> findByRepositoryNameAndRepositoryNamespace(String repositoryName, String repositoryNamespace);
}
