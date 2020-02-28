package com.redhat.tasksyncer.dao.entities.issues.components;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * @author David Rajnoha
 * */
@Entity
public class Comment {
    @Id
    @GeneratedValue
    private Long id;
    private String body;
    private Date createdAt;
    private String author;
    //assert creation and updates
    @ManyToOne(targetEntity = AbstractIssue.class, fetch = FetchType.LAZY)
    @JsonBackReference
    private AbstractIssue issue;

    public Comment(){}

    public Comment(String body, Date createdAt, String author) {
        this.body = body;
        this.createdAt = createdAt;
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getAuthor() {
        return author;
    }


    public AbstractIssue getIssue() {
        return issue;
    }

    public void setIssue(AbstractIssue issue) {
        if (Objects.equals(this.issue, issue)) return;

        if (this.issue != null) this.issue.removeComment(this);

        this.issue = issue;

        if (this.issue != null) this.issue.addComment(this);
    }
}
