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

    private String name;

    @OneToMany(targetEntity = Issue.class)
    private List<Issue> issues;

    public Project() {}


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
}
