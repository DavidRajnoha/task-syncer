package com.redhat.tasksyncer.dao.repositories;

import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Filip Cap
 */
public interface AbstractIssueRepository extends JpaRepository<AbstractIssue, Long> {
    AbstractIssue findOneByRemoteIssueId(String remoteIssueId);  // todo: rework
    Optional<AbstractIssue> findByRemoteIssueIdAndRepository_repositoryName(String issueId, String repositoryName);
    List<AbstractIssue> findByRepository_Project_nameAndRemoteIssueIdAndRepository_repositoryName(String name, String issueId, String repositoryName);
    List<AbstractIssue> findByRepository_Project_name(String projectName);
    Set<AbstractIssue> findByIssueType(IssueType issueType);
}
