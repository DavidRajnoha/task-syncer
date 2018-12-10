package com.redhat.tasksyncer.dao;

import com.redhat.tasksyncer.dao.entities.Issue;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Filip Cap
 */
public interface IssueRepository extends CrudRepository<Issue, Long> {
}
