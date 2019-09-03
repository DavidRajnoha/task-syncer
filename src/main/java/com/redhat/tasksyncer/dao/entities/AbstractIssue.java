package com.redhat.tasksyncer.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.redhat.tasksyncer.dao.enumerations.IssueType;

import javax.persistence.*;

/**
 * @author Filip Cap
 */
@Entity
@Inheritance
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class AbstractIssue {

    public static final String STATE_OPENED = "opened";
    public static final String STATE_CLOSED = "closed";
    public static final String STATE_REOPENED = "reopened";

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String description;
    private String remoteIssueId;

    private String state;

    private IssueType issueType;

    private String repositoryName;

    @ManyToOne(targetEntity = AbstractRepository.class, fetch = FetchType.LAZY, optional = false)
    @JsonBackReference
    private AbstractRepository repository;

    @OneToOne(targetEntity = AbstractCard.class, fetch = FetchType.LAZY, optional = false)
    @JsonManagedReference
    private AbstractCard card;

    public AbstractIssue(IssueType issueType) {
        this.issueType = issueType;
    }

    public void updateProperties(AbstractIssue issue) {
        title = issue.title;
        description = issue.description;
        state = issue.state;
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

    public String getRemoteIssueId() {
        return remoteIssueId;
    }

    public void setRemoteIssueId(String remoteIssueId) {
        this.remoteIssueId = remoteIssueId;
    }

    public AbstractRepository getRepository() {
        return repository;
    }

    public void setRepository(AbstractRepository repository) {
        this.repository = repository;
    }

    public AbstractCard getCard() {
        return card;
    }

    public void setCard(AbstractCard card) {
        this.card = card;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public IssueType getIssueType() {
        return issueType;
    }

    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }
}
