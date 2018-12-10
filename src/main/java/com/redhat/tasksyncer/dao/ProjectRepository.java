package com.redhat.tasksyncer.dao;

import com.redhat.tasksyncer.dao.entities.Project;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Filip Cap
 */
public interface ProjectRepository extends CrudRepository<Project, Long> {
}
