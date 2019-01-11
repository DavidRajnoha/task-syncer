package com.redhat.tasksyncer.dao.entities;

import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Filip Cap
 */
@Entity(name = "gitlabRepository")
@PropertySource("classpath:other.properties")
public class GitlabRepository extends AbstractRepository {

    @Transient
    private GitLabApi gitlabApi;

    @Transient
    @Value("${gitlabURL}")
    private String gitlabURL;

    @Transient
    @Value("${gitlabAuthKey}")
    private String gitlabAuthKey;

    private String repositoryNamespace;
    private String repositoryName;
    private boolean created = false;


    @OneToMany(targetEntity = Issue.class, mappedBy = "repository")
    private List<Issue> issues;

    @PostConstruct
    public void init() {
        gitlabApi = new GitLabApi(gitlabURL, Constants.TokenType.PRIVATE, gitlabAuthKey);
    }

    public GitlabRepository() {

    }

    public GitlabRepository(String repositoryNamespace, String repositoryName) {
        this.repositoryNamespace = repositoryNamespace;
        this.repositoryName = repositoryName;
    }




    @Override
    public List<Issue> getIssues() throws GitLabApiException {
        org.gitlab4j.api.models.Project glProject = gitlabApi.getProjectApi().getProject(this.getRepositoryNamespace(), this.getRepositoryName());

        Stream<org.gitlab4j.api.models.Issue> issuesStream = gitlabApi.getIssuesApi()
                .getIssues(glProject,100)
                .stream();  // have to use pagination, we want all pages not just the first one

        return issuesStream
                .map(Issue::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getRepositoryType() {
        return this.getClass().getTypeName();
    }

    @Override
    public String getRepositoryNamespace() {
        return repositoryNamespace;
    }

    @Override
    public String getRepositoryName() {
        return repositoryName;
    }

    @Override
    public boolean isCreated() {
        return created;
    }

    public void setRepositoryNamespace(String namespace) {
        this.repositoryNamespace = namespace;
    }

    public void setRepositoryName(String name) {
        this.repositoryName = name;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }
}
