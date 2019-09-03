package com.redhat.tasksyncer.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

/**
 * @author Filip Cap
 */
@Entity
@Inheritance
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class AbstractRepository {

    @Id
    @GeneratedValue
    private Long id;

    private String repositoryNamespace;
    private String repositoryName;

    @OneToMany(targetEntity = AbstractIssue.class, fetch = FetchType.LAZY, mappedBy = "repository")
    @JsonManagedReference
    private List<AbstractIssue> issues;

    @ManyToOne
    @JsonBackReference
    private Project project;


    public AbstractRepository() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRepositoryNamespace() {
        return repositoryNamespace;
    }

    public void setRepositoryNamespace(String repositoryNamespace) {
        this.repositoryNamespace = repositoryNamespace;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public List<AbstractIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<AbstractIssue> issues) {
        this.issues = issues;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
