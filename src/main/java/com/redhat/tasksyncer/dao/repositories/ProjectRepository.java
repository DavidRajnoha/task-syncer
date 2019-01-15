package com.redhat.tasksyncer.dao.repositories;

import com.redhat.tasksyncer.dao.entities.Project;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * @author Filip Cap
 */
public interface ProjectRepository extends CrudRepository<Project, Long> {
    Optional<Project> findProjectByName(String name);

}
