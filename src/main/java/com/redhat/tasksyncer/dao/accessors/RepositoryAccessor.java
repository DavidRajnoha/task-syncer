package com.redhat.tasksyncer.dao.accessors;

import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.GithubRepository;
import com.redhat.tasksyncer.dao.entities.GitlabRepository;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author Filip Cap
 */
public abstract class RepositoryAccessor {

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

    public abstract List<AbstractIssue> downloadAllIssues() throws Exception;

    public abstract AbstractIssue saveIssue(AbstractIssue issue);

    public abstract Optional<AbstractIssue> getIssue(AbstractIssue issue);

    public abstract void save();

    public abstract AbstractRepository createItself();

    public abstract AbstractRepository getRepository();


}
