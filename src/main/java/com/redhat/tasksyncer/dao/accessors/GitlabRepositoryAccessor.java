package com.redhat.tasksyncer.dao.accessors;

import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.GitlabIssue;
import com.redhat.tasksyncer.dao.entities.GitlabRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.Project;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Filip Cap
 */
public class GitlabRepositoryAccessor extends RepositoryAccessor {

    private AbstractRepository repository;
    private AbstractRepositoryRepository repositoryRepository;
    private AbstractIssueRepository issueRepository;

    private GitLabApi gitlabApi;


    public GitlabRepositoryAccessor(GitlabRepository repository, AbstractRepositoryRepository repositoryRepository, AbstractIssueRepository issueRepository, String gitlabURL, String gitlabAuthKey) {
        this.repository = repository;
        this.repositoryRepository = repositoryRepository;
        this.issueRepository = issueRepository;

        gitlabApi = new GitLabApi(gitlabURL, Constants.TokenType.PRIVATE, gitlabAuthKey);
    }

    public AbstractRepository createItself() {
        // todo according to repository.isCreated create remote instance
        this.save();
        return repository;
    }

    @Override
    public AbstractRepository getRepository() {
        return repository;
    }


    @Override
    public void save() {
        this.repository = repositoryRepository.save(repository);
    }

    @Override
    public List<AbstractIssue> downloadAllIssues() throws GitLabApiException {
        Project glProject = gitlabApi.getProjectApi()
                .getProject(this.repository.getRepositoryNamespace(), repository.getRepositoryName());

        Stream<Issue> issuesStream = gitlabApi.getIssuesApi()
                .getIssues(glProject,100)
                .stream();  // have to use pagination, we want all pages not just the first one

        return issuesStream
                .map(GitlabIssue.ObjectToGitlabIssueConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public AbstractIssue saveIssue(AbstractIssue issue) {
        return null;
    }

    public Optional<AbstractIssue> getIssue(AbstractIssue issue) {
        return null;
    }
}
