package com.redhat.tasksyncer.dao.entities.issues;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.redhat.tasksyncer.dao.entities.trello.AbstractCard;
import com.redhat.tasksyncer.dao.entities.repositories.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.issues.components.Comment;
import com.redhat.tasksyncer.dao.enumerations.IssueType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * @author Filip Cap, David Rajnoha
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

    public static final int DESC_LENGTH = 8168;

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    @Column(length = DESC_LENGTH)
    private String description;
    @NotNull
    private String remoteIssueId;
    private Date dueDate;
    private Date createdAt;
    private Date closedAt;
    private String closedBy;
    private String assignee;
    @ElementCollection
    private Set<String> labels;
//    private Milestone milestone;
    private Boolean hasCard;
    private Boolean deleted;

    @OneToMany(targetEntity = Comment.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Comment> comments;


    private String state;

    private IssueType issueType;

    @ManyToOne(targetEntity = AbstractRepository.class, fetch = FetchType.LAZY, optional = false)
    @JsonBackReference
    @JoinColumn(name = "repository_repositoryName")
    @NotNull
    private AbstractRepository repository;

    @OneToOne(targetEntity = AbstractCard.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private AbstractCard card;


    @JsonManagedReference
    @OneToMany(targetEntity = AbstractIssue.class, mappedBy = "parentIssue", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Map<String, AbstractIssue> childIssues;


    @JsonBackReference
    @ManyToOne
    private AbstractIssue parentIssue;


    public AbstractIssue(IssueType issueType) {
        this.issueType = issueType;
    }

    public void updateProperties(AbstractIssue issue) {
        title =         issue.title != null         ? issue.title : title;
        description =   issue.description != null   ? issue.description : description;
        state =         issue.state != null         ? issue.state : state;
        createdAt =     issue.createdAt != null     ? issue.createdAt : createdAt;
        remoteIssueId = issue.remoteIssueId != null ? issue.remoteIssueId : remoteIssueId;
        closedAt =      issue.closedAt != null      ? issue.closedAt : closedAt;
        dueDate =       issue.dueDate != null       ? issue.dueDate : dueDate;
        closedBy =      issue.closedBy != null      ? issue.closedBy : closedBy;
        assignee =      issue.assignee != null      ? issue.assignee : assignee;
        labels =        issue.labels != null        ? issue.labels : labels;
        comments =      issue.comments != null      ? issue.comments : comments;
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

    public void setParentIssue(AbstractIssue parentIssue){
        if (Objects.equals(parentIssue, this.parentIssue)) return;

        if (this.parentIssue != null) this.parentIssue.removeChildIssue(this);

        this.parentIssue = parentIssue;

        if (this.parentIssue != null) this.parentIssue.addChildIssue(this);
    }

    public AbstractIssue getParentIssue(){
        return parentIssue;
    }

    public void addChildIssue(AbstractIssue childIssue){
        if (this.childIssues == null){
            this.childIssues = new HashMap<>();
        }

        if (this.childIssues.containsValue(childIssue)) return;

        this.childIssues.put(childIssue.getRemoteIssueId(), childIssue);
        childIssue.setParentIssue(this);

    }

    public void removeChildIssue(AbstractIssue childIssue){
        if (this.childIssues == null || !this.childIssues.containsKey(childIssue.getRemoteIssueId())) return;

        this.childIssues.remove(childIssue.getRemoteIssueId());
        childIssue.setParentIssue(null);
    }

    // removes parent issue from this, but leaves this as the child issue at the parent issue
    // to prevent concurrent modification exception while deleting
    private void removeJustParentIssue(){
        this.parentIssue = null;
    }


    public Map<String, AbstractIssue> getChildIssues(){
        return childIssues;
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

    public void addComment(Comment comment){
        if (this.comments == null) {
            this.comments = new HashSet<>();
        }
        if (this.comments.contains(comment)) return;
        this.comments.add(comment);
        comment.setIssue(this);
    }

    public void removeComment(Comment comment){
        if (this.comments == null || !this.comments.contains(comment)) return;

        this.comments.remove(comment);

        comment.setIssue(null);
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

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Optional<Set<String>> getLabels() {
        return Optional.ofNullable(labels);
    }

    public void setLabel(Set<String> labels) {
        this.labels = labels;
    }

//    public Milestone getMilestone() {
//        return milestone;
//    }
//
//    public void setMilestone(Milestone milestone) {
//        this.milestone = milestone;
//    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
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

    public boolean hasCard() {
        return hasCard;
    }

    public void setHasCard(Boolean hasCard) {
        this.hasCard = hasCard;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
