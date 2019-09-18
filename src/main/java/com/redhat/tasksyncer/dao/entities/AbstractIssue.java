package com.redhat.tasksyncer.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.redhat.tasksyncer.dao.enumerations.IssueType;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Filip Cap
 */

@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"remoteIssueId", "repository_repositoryName"})
)


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

    @ManyToOne(targetEntity = AbstractRepository.class, fetch = FetchType.LAZY, optional = false)
    @JsonBackReference
    @JoinColumn(name = "repository_repositoryName")
    private AbstractRepository repository;

    @OneToOne(targetEntity = AbstractCard.class, fetch = FetchType.LAZY)
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
        if (Objects.equals(repository, this.repository)) return;
        
        if (this.repository != null) this.repository.removeIssue(this);
        
        this.repository = repository;
        
        if (this.repository != null) this.repository.addIssue(this);
    }

    public AbstractCard getCard() {
        return card;
    }

    public void setCard(AbstractCard card) {
        if (Objects.equals(card, this.card)) return;

        AbstractCard oldCard = this.card;
        this.card = card;

        if (oldCard != null) oldCard.setIssue(null);

        if (card != null) card.setIssue(this);
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

}
