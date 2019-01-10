package com.redhat.tasksyncer.dao.entities;

import com.redhat.tasksyncer.Label;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.webhook.IssueEvent;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Filip Cap
 */
@Entity(name = "issue")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"rid", "type"})})  //todo: there might be more instances of gitlab with non-disjoint IDs
public class Issue {
    public static final String GITHUB_ISSUE = "github-issue";
    public static final String GITLAB_ISSUE = "gitlab-issue";

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String description;
    private boolean opened;

    @Column(name = "rid")
    private String rid;

    private String riid;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Card card;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AbstractRepository repository;

    @Transient
    private Set<Label> labels;

    @Column(name = "type")
    private String type;

    // todo: add last edit datetime

    public Issue() {}

    public Issue(String rid, String iid, String title, String description, boolean opened, Set<Label> labels, String type) {
        this.title = title;
        this.description = description;
        this.opened = opened;
        this.labels = labels;
        this.rid = rid;
        this.riid = iid;
        this.type = type;
    }

    public Issue(IssueEvent.ObjectAttributes attributes) {
        this(
                String.valueOf(attributes.getId()),
                String.valueOf(attributes.getIid()),
                attributes.getTitle(),
                attributes.getDescription(),
                attributes.getState().equals(Constants.IssueState.OPENED.toValue()),  // todo: or state reopened
                new HashSet<>(),
                Issue.GITLAB_ISSUE
        );
    }

    public Issue(GHIssue issue) {
        this(
                String.valueOf(issue.getId()),  //TODO: assure id is unique and not confused with number
                String.valueOf(issue.getNumber()),
                issue.getTitle(),
                issue.getBody(),
                issue.getState() == GHIssueState.OPEN,  // todo: or state reopened
                new HashSet<>(),
                Issue.GITHUB_ISSUE
        );
    }

    public Issue(org.gitlab4j.api.models.Issue issue) {
        this(
                String.valueOf(issue.getId()),
                String.valueOf(issue.getIid()),
                issue.getTitle(),
                issue.getDescription(),
                issue.getState() == Constants.IssueState.OPENED || issue.getState() == Constants.IssueState.REOPENED,
                new HashSet<>(),
                Issue.GITLAB_ISSUE
        );
    }

    public void updateLocally(Issue i) {
        this.title = i.title;
        this.description = i.description;
        this.opened = i.opened;
        this.labels = new HashSet<>(i.labels);

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

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getRiid() {
        return riid;
    }

    public void setRiid(String riid) {
        this.riid = riid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(AbstractRepository repository) {
        this.repository = repository;
    }
}
