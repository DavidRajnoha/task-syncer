package com.redhat.tasksyncer.dao.entities;

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
public abstract class AbstractBoard {

    @Id
    @GeneratedValue
    private Long id;

    private String boardName;
    private String remoteBoardId;

    @OneToOne
    private Project project;

    @OneToMany(targetEntity = AbstractColumn.class, fetch = FetchType.LAZY, mappedBy = "board")
    @JsonManagedReference
    private List<AbstractColumn> columns;


    public AbstractBoard() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRemoteBoardId() {
        return remoteBoardId;
    }

    public void setRemoteBoardId(String remoteBoardId) {
        this.remoteBoardId = remoteBoardId;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public boolean isCreated() {
        return remoteBoardId != null && !remoteBoardId.isEmpty();
    }

    public List<AbstractColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<AbstractColumn> columns) {
        this.columns = columns;
    }
}
