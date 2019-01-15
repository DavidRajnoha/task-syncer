package com.redhat.tasksyncer.dao.repositories;

import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.GitlabIssue;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * @author Filip Cap
 */
public interface AbstractIssueRepository extends CrudRepository<AbstractIssue, Long> {
    Optional<GitlabIssue> findByRemoteIssueId(String remoteIssueId);  // todo: rework
}
