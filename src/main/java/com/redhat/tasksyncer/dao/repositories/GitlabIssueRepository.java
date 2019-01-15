package com.redhat.tasksyncer.dao.repositories;

import com.redhat.tasksyncer.dao.entities.GitlabIssue;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Filip Cap
 */
public interface GitlabIssueRepository extends CrudRepository<GitlabIssue, Long> {
}
