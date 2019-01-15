package com.redhat.tasksyncer.dao.entities;

import javax.persistence.*;

/**
 * @author Filip Cap
 */
@Entity
@Inheritance
public abstract class AbstractCard {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String description;
    private String remoteCardId;

    @OneToOne(targetEntity = AbstractIssue.class, fetch = FetchType.LAZY, optional = false, mappedBy = "card")
    private AbstractIssue issue;


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
        this.issue = issue;
    }
}
