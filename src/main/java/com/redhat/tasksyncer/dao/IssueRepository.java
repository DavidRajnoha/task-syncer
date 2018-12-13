package com.redhat.tasksyncer.dao;

import com.redhat.tasksyncer.dao.entities.Issue;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * @author Filip Cap
 */
public interface IssueRepository extends CrudRepository<Issue, Long> {
    Optional<Issue> findByRidAndType(String rid, String type);
}
