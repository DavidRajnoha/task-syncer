package com.redhat.tasksyncer.dao.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Filip Cap */
@Entity
public class Project {

    @Id
    @GeneratedValue
    @NotNull
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToOne(optional = false, fetch = FetchType.LAZY, mappedBy = "project", cascade = CascadeType.ALL)
    private AbstractBoard board;

    @OneToMany(targetEntity = AbstractRepository.class, fetch = FetchType.LAZY, mappedBy = "project", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<AbstractRepository> repositories;


    public Project() {
    }

    public AbstractBoard getBoard() {
        return board;
    }

    public void setBoard(AbstractBoard board) {
        this.board = board;
    }

    public List<AbstractRepository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<AbstractRepository> repository) {
        this.repositories = repository;
    }

    public void addRepository(AbstractRepository repository) {
        this.repositories.add(repository);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
