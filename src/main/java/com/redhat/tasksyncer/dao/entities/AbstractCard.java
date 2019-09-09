package com.redhat.tasksyncer.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Filip Cap
 */
@Entity
@Inheritance
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class AbstractCard {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String title;
    private String description;
    private String remoteCardId;

    @OneToOne
    private AbstractIssue issue;

    @ManyToOne(targetEntity = AbstractColumn.class, fetch = FetchType.LAZY, optional = false)
    @JsonBackReference
    private AbstractColumn column;


    public AbstractCard() {
    }

    public void updateProperties(AbstractCard card) {
        title = card.title;
        description = card.description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemoteCardId() {
        return remoteCardId;
    }

    public void setRemoteCardId(String remoteCardId) {
        this.remoteCardId = remoteCardId;
    }

    public AbstractIssue getIssue() {
        return issue;
    }

    public void setIssue(AbstractIssue issue) {
        //prevents inifinite loops
        if (Objects.equals(issue, this.issue)) return;

        //removes old reference
        if (this.issue != null) this.issue.setCard(null);

        //updates the issue here
        this.issue = issue;

        //creates new reference to this in issue
        if (this.issue != null) this.issue.setCard(this);
    }

    public AbstractColumn getColumn() {
        return column;
    }

    public void setColumn(AbstractColumn column) {
        //prevents inifinite loops
        if (Objects.equals(column, this.column)) return;

        //removes old reference
        if (this.column != null) this.column.removeCard(this);

        //updates the column here
        this.column = column;

        //creates new reference to this in column
        if (this.column != null) this.column.addCard(this);
    }
}
