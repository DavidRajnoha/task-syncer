package com.redhat.tasksyncer.dao.entities;

import javax.persistence.*;
import java.util.List;

/**
 * @author Filip Cap
 */
@Entity(name = "project")
public class Project {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    @OneToMany(targetEntity = Issue.class, mappedBy = "project")
    private List<Issue> issues;

    @Transient
    @OneToMany(targetEntity = Endpoint.class, mappedBy = "project")
    private List<Endpoint> endpoints;  // todo: resolve one to many or many to many or shared endpoints across projects


    private String boardId;

    public Project() {}

    public Project(String name) {
        this.name = name;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }
}
