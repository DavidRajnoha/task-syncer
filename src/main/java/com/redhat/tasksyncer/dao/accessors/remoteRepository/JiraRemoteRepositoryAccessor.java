package com.redhat.tasksyncer.dao.accessors.remoteRepository;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.issues.JiraIssue;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * @author David Rajnoha
 * */

@Component
public class JiraRemoteRepositoryAccessor extends RemoteRepositoryAccessor {
    // JQL for searching issues corresponding to particular project, the KEY will be replaced by the project key
    private static final String JQL = "project=KEY";

    // private static final String URL = "https://NAMESPACE.atlassian.net";
    private static final String URL = "http://NAMESPACE";
    private JiraRestClient jiraRestClient;

    @Autowired
    public JiraRemoteRepositoryAccessor() {
    }


    @Override
    public void connectToRepository() throws IOException {
        URI jiraUri = getJiraUri();
        jiraRestClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(jiraUri, repository.getFirstLoginCredential(), repository.getSecondLoginCredential());
    }


    //TODO: Set correct credential parameters
    private URI getJiraUri() {
        return URI.create(URL.replace("NAMESPACE", repository.getRepositoryNamespace()));
    }


    @Override
    public List<AbstractIssue> downloadAllIssues() {

       // String newJQL = JQL.replace"project_name", repository.getRepositoryName());
        String newJQL = JQL.replace("KEY", repository.getRepositoryName());

        Stream<Issue> issuesStream = StreamSupport.stream(jiraRestClient
                .getSearchClient()
                .searchJql(newJQL)
                .claim()
                .getIssues()
                .spliterator()
                , false)
                ;


        //for each issue from jira converts this issue to abstract issue, adds subtasks and returns
        return proccessIssueStream(issuesStream);
    }


    /**
     * Takes stream of Issues downloaded from Jira and transforms it into the list of AbstractIssues
     *
    * */
    public List<AbstractIssue> proccessIssueStream(Stream<Issue> issuesStream){
        return  issuesStream
                .map(input -> {
                    // Converts each issue using
                    AbstractIssue abstractIssue = JiraIssue.ObjectToJiraIssueConverter.convert(input,
                            repository.getColumnMapping());

                    // For each subtasks from input appends this subtask to the Jira Issue and adds repository to the
                    // child issue
                    JiraIssue.ObjectToJiraIssueConverter.getSubtasks(input).forEach(childIssue -> {
                        abstractIssue.addChildIssue(childIssue);
                        childIssue.setRepository(repository);
                    });
                    abstractIssue.setRepository(repository);

                    return abstractIssue;
                })
                .peek(issue -> issue.setRepository(repository))
                .collect(Collectors.toList());
    }

    @Override
    public void createWebhook(String webhook) throws IOException, GitLabApiException {
        //this functionality is not supported yet
    }

}
