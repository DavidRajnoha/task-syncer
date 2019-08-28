package com.redhat.tasksyncer.dao.entities;

import javax.persistence.Entity;

@Entity
public class GithubRepository extends AbstractRepository {
    private String githubUsername;
    private String githubPassword;

    public GithubRepository() {
        super();
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public String getGithubPassword() {
        return githubPassword;
    }

    public void setGithubPassword(String githubPassword) {
        this.githubPassword = githubPassword;
    }
}
