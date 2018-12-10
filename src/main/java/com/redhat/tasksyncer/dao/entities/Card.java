package com.redhat.tasksyncer.dao.entities;

import javax.persistence.*;

/**
 * @author Filip Cap
 */
@Entity(name = "card")
public class Card {
    public static final String TRELLO_CARD = "trello-card";

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String description;

    private String type;

    private String cuid;

    @OneToOne(fetch = FetchType.LAZY)
    private Issue issue;

    public Card() {}

    public Card(com.julienvey.trello.domain.Card card) {
        this.title = card.getName();
        this.description = card.getDesc();
        this.cuid = card.getId();
        this.type = TRELLO_CARD;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }
}
