package com.redhat.tasksyncer.dao.entities;

import javax.persistence.*;
import java.util.List;

/**
 * @author Filip Cap
 */
@Entity(name = "endpoint")
public class Endpoint {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(targetEntity = Project.class, optional = false, fetch = FetchType.LAZY)
    private Project project;

    private EndpointType endpointType;

    private String field;

    @Transient
    private List<Endpoint> connectedTo;

    public Endpoint() {}

    public Endpoint(EndpointType type, String field) {
        this.field = field;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public EndpointType getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(EndpointType endpointType) {
        this.endpointType = endpointType;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public List<Endpoint> getConnectedTo() {
        return connectedTo;
    }

    public void setConnectedTo(List<Endpoint> connectedTo) {
        this.connectedTo = connectedTo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public enum EndpointType {
        GITHUB, GITLAB, TRELLO
    }


}
