package com.redhat.tasksyncer.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @OneToMany(targetEntity = AbstractIssue.class, fetch = FetchType.LAZY, mappedBy = "repository", cascade = CascadeType.ALL)
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

    public void addIssue(AbstractIssue issue) {
        if (this.issues == null){
            this.issues = new ArrayList<>();
        }

        //prevents infinite loops
        if (this.issues.contains(issue)) return;

        //adds issue here
        this.issues.add(issue);

        //adds repository to the issue
        issue.setRepository(this);
    }

    public void removeIssue(AbstractIssue issue) {
        //checks if issues is not null and prevents infinite loops
        if (this.issues == null || !this.issues.contains(issue)) return;

        //removes issue here and removes this repository from the issue
        this.issues.remove(issue);
        issue.setRepository(null);
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        //prevents infinite loops
        if (Objects.equals(this.project, project)) return;

        //removes this repository from the old project
        if (this.project != null) this.project.removeRepository(this);

        //sets a new project here
        this.project = project;

        //sets this repository to the new project
        if (this.project != null) this.project.addRepository(this);
    }
}
