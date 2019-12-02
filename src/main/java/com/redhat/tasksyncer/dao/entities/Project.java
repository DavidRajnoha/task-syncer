package com.redhat.tasksyncer.dao.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Filip Cap */
@Entity
public class Project{

    @Id
    @GeneratedValue
    @NotNull
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToOne(targetEntity = AbstractBoard.class, optional = false, fetch = FetchType.LAZY, mappedBy = "project", cascade = CascadeType.ALL)
    @JsonManagedReference
    private AbstractBoard board;

    @OneToMany(targetEntity = AbstractRepository.class, fetch = FetchType.LAZY, mappedBy = "project", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<AbstractRepository> repositories;

    @ElementCollection
    private List<String> columnNames = new ArrayList<>();

    public Project() {
    }

    public AbstractBoard getBoard() {
        return board;
    }

    public void setBoard(AbstractBoard board) {
        //prevents infinite loops
        if (Objects.equals(this.board, board)) return;

        //updates the board here
        AbstractBoard oldBoard = this.board;
        this.board = board;

        //clears the project from the previous board
        if (oldBoard != null) oldBoard.setProject(null);

        //updates the new board
        if (board != null) board.setProject(this);
    }

    public List<AbstractRepository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<AbstractRepository> repository) {
        this.repositories = repository;
    }

    public void addRepository(AbstractRepository repository) {
        //creates new ArrayList when adding a new item
        if (this.repositories == null) {
            this.repositories = new ArrayList<>();
        }

        //prevents infinite loops
        if (this.repositories.contains(repository)) return;

        //adds the repository here
        this.repositories.add(repository);

        //sets the project field in repository
        repository.setProject(this);
    }

    public void removeRepository(AbstractRepository repository) {
        //prevents infinite looping
        if (this.repositories == null || !this.repositories.contains(repository)) return;

        repositories.remove(repository);

        repository.setProject(null);
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

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }
}
