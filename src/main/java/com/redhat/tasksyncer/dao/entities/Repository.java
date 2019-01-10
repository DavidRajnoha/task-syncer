package com.redhat.tasksyncer.dao.entities;

import javax.persistence.Inheritance;
import java.util.List;

/**
 * @author Filip Cap
 */
@Inheritance
public interface Repository {
    /**
     * Freshly obtains issues and does not set project
     * @return
     * @throws Exception
     */
    List<Issue> getIssues() throws Exception;

    String getRepositoryType();
    String getRepositoryNamespace();
    String getRepositoryName();
    boolean isCreated();
}
