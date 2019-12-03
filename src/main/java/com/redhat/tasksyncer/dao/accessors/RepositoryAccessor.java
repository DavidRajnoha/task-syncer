package com.redhat.tasksyncer.dao.accessors;

import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.CannotConnectToRepositoryException;
import com.redhat.tasksyncer.exceptions.InvalidMappingException;
import org.gitlab4j.api.GitLabApiException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Filip Cap, David Rajnoha
 */
public abstract class RepositoryAccessor {
    protected AbstractRepositoryRepository repositoryRepository;
    protected AbstractRepository repository;


    public abstract void connectToRepository() throws IOException;

    public abstract List<AbstractIssue> downloadAllIssues() throws IOException, GitLabApiException;

    public abstract void createWebhook(String webhook) throws IOException, GitLabApiException;

    public abstract AbstractRepository createRepositoryOfType();

    protected abstract Map<String, String> isMappingValid(Map<String, String> mapping) throws InvalidMappingException;


    /**
     * Creates new repository accessor that is connected to the external service (for example the trelloApi etc. fields
     * are initiated and ready to communicate with the ext. service)
     * */
    public RepositoryAccessor getConnectedInstance(AbstractRepository repository) throws CannotConnectToRepositoryException {

        initializeRepository(repository);

        try {
            this.connectToRepository();
        } catch (IOException exception){
            throw new CannotConnectToRepositoryException(exception.getMessage());
        }

        return this;
    }

    public void initializeRepository(AbstractRepository repository){
        this.repository = repository;
    }

    public void save() {
        this.repository = repositoryRepository.save(repository);
    }

    public AbstractRepository createItself() {
        this.save();
        return repository;
    }
    public AbstractRepository getRepository() {
        return repository;
    }

    public void deleteRepository(AbstractRepository repository) {
        // TODO: When error is thrown while creating an issue, the repository is not deleted
        repository.setProject(null);
        repositoryRepository.delete(repository);
    }


    public AbstractRepository createRepository(String firstLoginCredential, String secondLoginCredential,
                                               String repoName, String repoNamespace){
        AbstractRepository repository = createRepositoryOfType();
        repository.setFirstLoginCredential(firstLoginCredential);
        repository.setSecondLoginCredential(secondLoginCredential);
        repository.setRepositoryName(repoName);
        repository.setRepositoryNamespace(repoNamespace);

        return repository;
    };


    public void setColumnMapping(Map<String, String> columnMapping) throws InvalidMappingException {
        repository.setColumnMapping(isMappingValid(columnMapping));
    }
}


/**
 * Creates new subclass of repositoryAccessor, based on the class of the repository that is passed in the argument
 * */
//    public static RepositoryAccessor getInstance(AbstractRepository repository, AbstractRepositoryRepository repositoryRepository,
//                                                          AbstractIssueRepository issueRepository) throws  RepositoryTypeNotSupportedException {
//        Class<? extends AbstractRepository> repoType = repository.getClass();
//        RepositoryAccessor repositoryAccessor;
//        if (GitlabRepository.class.equals(repoType)) {
//            repositoryAccessor = new GitlabRepositoryAccessor((GitlabRepository) repository, repositoryRepository, issueRepository);
//        }
//        else if (GithubRepository.class.equals(repoType)) {
//            repositoryAccessor = new GithubRepositoryAccessor((GithubRepository) repository, repositoryRepository, issueRepository);
//        } else if (JiraRepository.class.equals(repoType)) {
//            repositoryAccessor = new JiraRepositoryAccessor((JiraRepository) repository, repositoryRepository, issueRepository);
//        } else if (TrelloRepository.class.equals(repoType)){
//            repositoryAccessor = new TrelloRepositoryAccessor((TrelloRepository) repository, repositoryRepository);
//        } else {
//            throw new RepositoryTypeNotSupportedException("");
//        }
//
//        return repositoryAccessor;
//    }