package com.redhat.tasksyncer.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;
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
    @Column(length = AbstractIssue.DESC_LENGTH)
    private String description;
    private String remoteCardId;
    private Date dueDate;
    private Date createdAt;
    private Date closedAt;
    private String closedBy;
    private String assignee;
//    private Set<String> labels;
 //   private Milestone milestone;
//    private Set<Comment> comments;

    @OneToOne
    @JsonBackReference
    private AbstractIssue issue;

    @ManyToOne(targetEntity = AbstractColumn.class, fetch = FetchType.LAZY, optional = false)
    @JsonBackReference
    private AbstractColumn column;


    public AbstractCard() {
    }

    public void updateProperties(AbstractCard card) {
        title = card.title;
        description = card.description;
        column = card.column;
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


        AbstractIssue oldIssue = this.issue;

        //updates the issue here
        this.issue = issue;

        if (oldIssue != null) oldIssue.setCard(null);

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

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Date closedAt) {
        this.closedAt = closedAt;
    }

    public String getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(String closedBy) {
        this.closedBy = closedBy;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

/*
    public Set<String> getLabels() {
        return labels;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }
*/

//    public Milestone getMilestone() {
//        return milestone;
//    }
//
//    public void setMilestone(Milestone milestone) {
//        this.milestone = milestone;
//    }

//    public Set<Comment> getComments() {
//        return comments;
//    }
//
//    public void setComments(Set<Comment> comments) {
//        this.comments = comments;
//    }
}
