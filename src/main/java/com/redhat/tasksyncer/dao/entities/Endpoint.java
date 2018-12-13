package com.redhat.tasksyncer.dao.entities;

import javax.persistence.*;

/**
 * @author Filip Cap
 */
@Entity(name = "endpoint")
public class Endpoint {
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

    public enum EndpointType {
        GITHUB, GITLAB, TRELLO
    }

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(targetEntity = Project.class, optional = false, fetch = FetchType.LAZY)
    private Project project;

    private EndpointType endpointType;

    public Endpoint() {}

//    public Endpoint(EndpointType endpointType, )
    // todo: generify and make object hierarchy
}
