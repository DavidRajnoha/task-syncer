package com.redhat.tasksyncer.dao.repositories;

import com.redhat.tasksyncer.dao.entities.GithubIssue;
import org.springframework.data.repository.CrudRepository;

public interface GithubIssueRepository extends CrudRepository<GithubIssue, Long> {
}
