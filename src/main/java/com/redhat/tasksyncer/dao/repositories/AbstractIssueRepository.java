package com.redhat.tasksyncer.dao.repositories;

import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.GitlabIssue;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;

/**
 * @author Filip Cap
 */
public interface AbstractIssueRepository extends CrudRepository<AbstractIssue, Long> {
    AbstractIssue findOneByRemoteIssueId(String remoteIssueId);  // todo: rework
    Optional<AbstractIssue> findByRemoteIssueIdAndIssueTypeAndRepository_repositoryName(String issueId, IssueType issueType, String repositoryName);
    Set<AbstractIssue> findByIssueType(IssueType issueType);
}
