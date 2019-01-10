package com.redhat.tasksyncer.dao;

import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Filip Cap
 */
public interface AbstractRepositoryRepository extends CrudRepository<AbstractRepository, Long> {
}
