package com.redhat.tasksyncer.dao.accessors;

import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.Project;
import org.kohsuke.github.*;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        //GitHub object used to communicate with Github and ghRepository are created along with the Accessor
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
        Stream<GHIssue> issuesStream = ghRepository.getIssues(GHIssueState.ALL)
                .stream();

        return issuesStream
                .map(GithubIssue.ObjectToGithubIssueConverter::convert)
                .collect(Collectors.toList());
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
        //using library to set Webhook to the webhookURL from argument (should be URL pointing to this app's endpoint
        Set<GHEvent> events = new HashSet<>();
        //The webhook is triggered by all issues events - not comments!!
        events.add(GHEvent.ISSUES);
        ghRepository.createWebHook(webHookUrl, events);
    }

}
