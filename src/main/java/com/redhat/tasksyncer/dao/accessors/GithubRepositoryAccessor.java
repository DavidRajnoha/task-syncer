package com.redhat.tasksyncer.dao.accessors;

import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.GithubRepository;
import com.redhat.tasksyncer.dao.entities.GitlabRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.kohsuke.github.GHEvent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GithubRepositoryAccessor extends RepositoryAccessor {

    private GithubRepository repository;

    private AbstractRepositoryRepository repositoryRepository;
    private AbstractIssueRepository issueRepository;

    private GitHub gitHub;
    private GHRepository ghRepository;

    public GithubRepositoryAccessor(GithubRepository repository, AbstractRepositoryRepository repositoryRepository, AbstractIssueRepository issueRepository) throws IOException {
        this.repository = repository;
        this.repositoryRepository = repositoryRepository;
        this.issueRepository = issueRepository;

        gitHub = GitHub.connectUsingPassword(repository.getGithubUsername(), repository.getGithubPassword());
        ghRepository = gitHub.getRepository(repository.getRepositoryName());

    }

    public GithubRepository createItself() {
        // todo according to repository.isCreated create remote instance
        this.save();
        return repository;
    }

    @Override
    public List<AbstractIssue> downloadAllIssues() throws Exception {
        return null;
    }

    @Override
    public AbstractIssue saveIssue(AbstractIssue issue) {
        return null;
    }

    @Override
    public Optional<AbstractIssue> getIssue(AbstractIssue issue) {
        return Optional.empty();
    }

    @Override
    public void save() {

    }

    public void createWebhook(URL webHookUrl) throws IOException {
        Set<GHEvent> events = new HashSet<>();
        events.add(GHEvent.ISSUES);
        ghRepository.createWebHook(webHookUrl, events);
    }

}
