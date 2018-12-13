package com.redhat.tasksyncer.dao.entities;

import com.redhat.tasksyncer.Label;
import org.gitlab.api.models.GitlabIssue;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Filip Cap
 */
@Entity(name = "issue")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"rid", "type"})})
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

    @ManyToOne(targetEntity = Project.class, optional = false, fetch = FetchType.LAZY)
    private Project project;

    @Transient
    private Set<Label> labels;

    @Column(name = "type")
    private String type;

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

    public Issue(GitlabIssue gli) {
        this(
                String.valueOf(gli.getId()),
                String.valueOf(gli.getIid()),
                gli.getTitle(),
                gli.getDescription(),
                gli.getState().equals(GitlabIssue.STATE_OPENED),
                new HashSet<>(),
                Issue.GITLAB_ISSUE
        );

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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
