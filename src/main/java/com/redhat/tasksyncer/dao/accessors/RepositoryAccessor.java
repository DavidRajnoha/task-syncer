package com.redhat.tasksyncer.dao.accessors;

import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;
import org.gitlab4j.api.GitLabApiException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author Filip Cap, David Rajnoha
 */
public abstract class RepositoryAccessor {
    protected AbstractRepositoryRepository repositoryRepository;


    /**
     * Creates new subclass of repositoryAccessor, based on the class of the repository that is passed in the argument
     * */
    public static RepositoryAccessor getInstance(AbstractRepository repository, AbstractRepositoryRepository repositoryRepository,
                                                          AbstractIssueRepository issueRepository) throws IOException, RepositoryTypeNotSupportedException {
        Class<? extends AbstractRepository> repoType = repository.getClass();
        RepositoryAccessor repositoryAccessor;
        if (GitlabRepository.class.equals(repoType)) {
            repositoryAccessor = new GitlabRepositoryAccessor((GitlabRepository) repository, repositoryRepository, issueRepository);
        }
        else if (GithubRepository.class.equals(repoType)) {
            repositoryAccessor = new GithubRepositoryAccessor((GithubRepository) repository, repositoryRepository, issueRepository);
        } else if (JiraRepository.class.equals(repoType)) {
            repositoryAccessor = new JiraRepositoryAccessor((JiraRepository) repository, repositoryRepository, issueRepository);
        } else if (TrelloRepository.class.equals(repoType)){
            repositoryAccessor = new TrelloRepositoryAccessor((TrelloRepository) repository, repositoryRepository);
        } else {
            throw new RepositoryTypeNotSupportedException("");
        }

        return repositoryAccessor;
    }

    public static RepositoryAccessor getConnectedInstance(AbstractRepository repository, AbstractRepositoryRepository repositoryRepository,
                                                 AbstractIssueRepository issueRepository) throws IOException, RepositoryTypeNotSupportedException {
        RepositoryAccessor repositoryAccessor = getInstance(repository, repositoryRepository, issueRepository);
        repositoryAccessor.connectToRepository();
        return repositoryAccessor;
    }

    public abstract void connectToRepository() throws IOException;

    public abstract List<AbstractIssue> downloadAllIssues() throws IOException, GitLabApiException;

    public abstract AbstractIssue saveIssue(AbstractIssue issue);

    public abstract Optional<AbstractIssue> getIssue(AbstractIssue issue);

    public abstract void save();

    public abstract AbstractRepository createItself();

    public abstract AbstractRepository getRepository();

    public abstract void createWebhook(String webhook) throws IOException, GitLabApiException;

    public void deleteRepository(AbstractRepository repository) {
        // TODO: When error is thrown while creating an issue, the repository is not deleted
        repository.setProject(null);
        repositoryRepository.delete(repository);
    }
}
