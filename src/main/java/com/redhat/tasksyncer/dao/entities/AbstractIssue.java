package com.redhat.tasksyncer.dao.entities;

import javax.persistence.*;

/**
 * @author Filip Cap
 */
@Entity
@Inheritance
public abstract class AbstractIssue {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String description;
    private String remoteIssueId;

    @ManyToOne(targetEntity = AbstractRepository.class, fetch = FetchType.LAZY, optional = false)
    private AbstractRepository repository;

    @OneToOne(targetEntity = AbstractCard.class, fetch = FetchType.LAZY, optional = false)
    private AbstractCard card;

    public AbstractIssue() {
    }

    public void updateProperties(AbstractIssue issue) {
        title = issue.title;
        description = issue.description;
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
}
