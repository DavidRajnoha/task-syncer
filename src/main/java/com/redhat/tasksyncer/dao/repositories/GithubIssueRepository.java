package com.redhat.tasksyncer.dao.repositories;

import com.redhat.tasksyncer.dao.entities.GithubIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface GithubIssueRepository extends JpaRepository<GithubIssue, Long> {
}
