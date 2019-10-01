package com.redhat.tasksyncer.dao.accessors;

import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.JiraIssue;
import com.redhat.tasksyncer.dao.entities.JiraRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import org.gitlab4j.api.GitLabApiException;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JiraRepositoryAccessor extends RepositoryAccessor {
    private static final String JQL = "";
    //TODO: This is configured for the JIRA Cloud Solution, the URL of the local server solution may (and will) be different
    private static final String URL = "https://NAMESPACE.atlassian.net";
    private AbstractRepository repository;
    private AbstractRepositoryRepository repositoryRepository;
    private JiraRestClient jiraRestClient;


    public JiraRepositoryAccessor(JiraRepository jiraRepository, AbstractRepositoryRepository repositoryRepository, AbstractIssueRepository issueRepository){
        this.repository = jiraRepository;
        this.repositoryRepository = repositoryRepository;
    }



    @Override
    public void connectToRepository() throws IOException {
        URI jiraUri = getJiraUri();
        jiraRestClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(jiraUri, repository.getSecondLoginCredential(), repository.getFirstLoginCredential());
    }


    //TODO: Set correct credential parameters
    private URI getJiraUri() {
        return URI.create(URL.replace("NAMESPACE", repository.getRepositoryNamespace()));
    }


    @Override
    public List<AbstractIssue> downloadAllIssues() throws Exception {

       // String newJQL = JQL.replace("project_name", repository.getRepositoryName());
        String newJQL = JQL;

        Stream<Issue> issuesStream = StreamSupport.stream(jiraRestClient
                .getSearchClient()
                .searchJql(newJQL)
                .claim()
                .getIssues()
                .spliterator(), false);

        return issuesStream
                .map(JiraIssue.ObjectToJiraIssueConverter::convert)
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
        this.repository = repositoryRepository.save(repository);
    }

    @Override
    public AbstractRepository createItself() {
        this.save();
        return repository;
    }

    @Override
    public AbstractRepository getRepository() {
        return repository;
    }

    @Override
    public void createWebhook(URL webhook) throws IOException, GitLabApiException {
        //this functionality is not supported yet
    }


}
