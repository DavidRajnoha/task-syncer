package com.redhat.tasksyncer.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.redhat.tasksyncer.dao.enumerations.IssueType.*;

/**
 * @author Filip Cap
 */

@Table(
        //TODO: Same repoistory on multiple projects
        uniqueConstraints = @UniqueConstraint(columnNames = {"repository_repositoryName", "repositoryNamespace"})
)

@Entity
@Inheritance
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class AbstractRepository {

    @Id
    @GeneratedValue
    private Long id;

    private String remoteRepositoryId;

    private String repositoryNamespace;

    @Column(name = "repository_repositoryName")
    private String repositoryName;

    @JsonIgnore
    private String firstLoginCredential;
    @JsonIgnore
    private String secondLoginCredential;

    @OneToMany(targetEntity = AbstractIssue.class, fetch = FetchType.LAZY, mappedBy = "repository", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<AbstractIssue> issues;

    @ManyToOne
    @JsonBackReference
    private Project project;


    public AbstractRepository() {
    }

    /**
     * Factory method
     * Creates new repository of the type that is passed as IssueType, then sets the security credential fields and the repositoryName and repositoryNamespace
     * */
    public static AbstractRepository newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace(String servcieType, String firstLoginCredential, String secondLoginCredential,
                                                                                             String repositoryName, String repositoryNamespace) throws RepositoryTypeNotSupportedException {
        AbstractRepository repository;
        switch (servcieType){
            case "gitlab":
                repository = new GitlabRepository();
                break;
            case "github":
                repository = new GithubRepository();
                break;
            case "jira":
                repository = new JiraRepository();
                break;
            case "trello":
                repository = new TrelloRepository();
                break;
            default:
                throw new RepositoryTypeNotSupportedException("");
        }

        repository.setFirstLoginCredential(firstLoginCredential);
        repository.setSecondLoginCredential(secondLoginCredential);
        repository.setRepositoryName(repositoryName);
        repository.setRepositoryNamespace(repositoryNamespace);

        return repository;
    };

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

    public String getFirstLoginCredential() {
        return firstLoginCredential;
    }

    public void setFirstLoginCredential(String firstLoginCredential) {
        this.firstLoginCredential = firstLoginCredential;
    }

    public String getSecondLoginCredential() {
        return secondLoginCredential;
    }

    public void setSecondLoginCredential(String secondLoginCredential) {
        this.secondLoginCredential = secondLoginCredential;
    }

    public String getRemoteRepositoryId() {
        return remoteRepositoryId;
    }

    public void setRemoteRepositoryId(String remoteRepositoryId) {
        this.remoteRepositoryId = remoteRepositoryId;
    }
}
