package com.redhat.tasksyncer.dao.entities;

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
public abstract class AbstractBoard {

    @Id
    @GeneratedValue
    private Long id;

    private String boardName;
    private String remoteBoardId;

    @OneToOne
    private Project project;

    @OneToMany(targetEntity = AbstractColumn.class, fetch = FetchType.LAZY, mappedBy = "board", cascade = CascadeType.REMOVE)
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

    public void removeColumn(AbstractColumn column){
        if (this.columns == null || !this.columns.contains(column)) return;

        this.columns.remove(column);

        column.setBoard(null);
    }

    public void addColumn(AbstractColumn column){
        if (this.columns == null) {
            this.columns = new ArrayList<>();
        }

        if (this.columns.contains(column)) return;

        this.columns.add(column);

        column.setBoard(this);
    }

    public void setColumns(List<AbstractColumn> columns) {
        this.columns = columns;
    }

    public Project getProjectImpl() {
        return project;
    }

    public void setProjectImpl(Project project) {
        //prevets endless loop
        if (sameAsFormerProject(project))
            return;

        //Updates the project here
        Project oldProject = this.project;
        this.project = project;

        //Removes this board from the previous project
        if (oldProject != null) {
            oldProject.setBoard(null);
        }

        //Set this board on the project
        if (project != null){
            project.setBoard(this);
        }
    }

    private boolean sameAsFormerProject(Project newProject) {
        return Objects.equals(project, newProject);
    }
}
