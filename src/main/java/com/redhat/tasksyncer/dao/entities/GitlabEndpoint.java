package com.redhat.tasksyncer.dao.entities;

/**
 * @author Filip Cap
 */
public class GitlabEndpoint extends Endpoint {
    public GitlabEndpoint(String namespace, String repoName) {
        super(EndpointType.GITLAB, namespace + "/" + repoName);
    }

    public String getNamespace() {
        return super.getField().split("/")[0];
    }

    public String getRepoName() {
        return super.getField().split("/")[1];
    }
}
