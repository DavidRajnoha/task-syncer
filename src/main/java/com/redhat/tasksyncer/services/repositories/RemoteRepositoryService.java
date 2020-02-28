package com.redhat.tasksyncer.services.repositories;

import com.redhat.tasksyncer.dao.accessors.issue.AbstractIssueAccessor;
import com.redhat.tasksyncer.dao.accessors.project.ProjectAccessor;
import com.redhat.tasksyncer.dao.accessors.remoteRepository.RemoteRepositoryAccessor;
import com.redhat.tasksyncer.dao.accessors.repository.RepositoryAccessor;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.repositories.AbstractRepository;
import com.redhat.tasksyncer.exceptions.CannotConnectToRepositoryException;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service
public class RemoteRepositoryService extends AbstractRepositoryService {


    private Map<String, RemoteRepositoryAccessor> remoteRepositoryAccessors;
    private AbstractIssueAccessor issueAccessor;
    private RemoteRepositoryAccessor remoteRepositoryAccessor;


    @Autowired
    public RemoteRepositoryService(Map<String, RepositoryAccessor> repositoryAccessors,
                                   AbstractIssueAccessor issueAccessor,
                                   Map<String, RemoteRepositoryAccessor> remoteRepositoryAccessors,
                                   ProjectAccessor projectAccessor
                                        ) {
        super(repositoryAccessors, projectAccessor);
        this.remoteRepositoryAccessors = remoteRepositoryAccessors;
        this.issueAccessor = issueAccessor;
    }


    /**
     * Method takes takes object that extends the RepositoryAccessor abstract class and uses the downloadAllIssues()
     * method to get a list of issues from that particular repository, then updates and syncs those issues with the internal
     * IssueRepository and Trello using the update method
     * */
    public void downloadAndSaveIssues(String repositoryName, String projectName, String repoType)
            throws CannotConnectToRepositoryException, IOException, GitLabApiException {

        List<AbstractIssue> issues;

        getConnectedInstance(repositoryName, projectName, repoType);

        // downloads issue from external repository
        issues = remoteRepositoryAccessor.downloadAllIssues();

        issueAccessor.updateIssues(issues);
    }


    public void hookRepository(String repositoryName, String projectName, String repoType, String thisWebhookUri)
            throws CannotConnectToRepositoryException, IOException, GitLabApiException {
        getConnectedInstance(repositoryName, projectName, repoType);

        remoteRepositoryAccessor.createWebhook(thisWebhookUri.replace("{projectName}", projectName));
    }


    private void getConnectedInstance(String repositoryName, String projectName, String repoType)
            throws CannotConnectToRepositoryException {
        RepositoryAccessor repositoryAccessor = getRepositoryAccessor(repoType);

        this.remoteRepositoryAccessor = remoteRepositoryAccessors
                .get(repoType.toLowerCase() + "RemoteRepositoryAccessor");


        AbstractRepository repository = repositoryAccessor.getRepository(repositoryName, projectName);
        remoteRepositoryAccessor.getConnectedInstance(repository);
    }

}
