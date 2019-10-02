package com.redhat.tasksyncer.dao.accessors;

import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import org.kohsuke.github.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GithubRepositoryAccessor extends RepositoryAccessor {

    private AbstractRepository repository;

    private AbstractRepositoryRepository repositoryRepository;
    private AbstractIssueRepository issueRepository;

    private GitHub gitHub;
    private GHRepository ghRepository;

    public GithubRepositoryAccessor(GithubRepository repository, AbstractRepositoryRepository repositoryRepository, AbstractIssueRepository issueRepository) throws IOException {
        this.repository = repository;
        this.repositoryRepository = repositoryRepository;
        this.issueRepository = issueRepository;
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
    public void connectToRepository() throws IOException {
        this.gitHub = getConnection(repository.getFirstLoginCredential(), repository.getSecondLoginCredential());
        //Creating string to find the particular repository
        String namsespaceAndRepository = repository.getRepositoryNamespace() + "/" + repository.getRepositoryName();
        ghRepository = gitHub.getRepository(namsespaceAndRepository);
    }

    private GitHub getConnection(String firstLoginCredential, String secondLoginCredential) throws IOException {
        return GitHub.connectUsingPassword(repository.getFirstLoginCredential(), repository.getSecondLoginCredential());
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
        this.repository = this.repositoryRepository.save(repository);
    }

    public void createWebhook(String webHookUrlString) throws IOException {
        //using library to set Webhook to the webhookURL from argument (should be URL pointing to this app's endpoint
        Set<GHEvent> events = new HashSet<>();
        //The webhook is triggered by all issues events - not comments!!
        events.add(GHEvent.ISSUES);

        URL webHookUrl = new URL(webHookUrlString);

        // ghRepository.createWebHook(webHookUrl, events);

        // it is necessary to use the .createHook instead of .createWebHook method, the createWebHook creates the webhook
        // of the other content_type than JSON
        // .createHook enables to pass config further, and in config is possible to set the content_type to JSON
        Map<String, String> config = new HashMap<>();
        config.put("url", webHookUrl.toExternalForm());
        config.put("content_type", "json");
        ghRepository.createHook("web", config, events, true);
    }

}
