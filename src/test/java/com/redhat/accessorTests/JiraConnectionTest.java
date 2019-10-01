package com.redhat.accessorTests;

import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import org.junit.Test;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JiraConnectionTest {

    String jiraUri = "https://drajnoha.atlassian.net";
    String jiraUserName = "drajnoha@seznam.cz";
    String jiraPassword = "I0AECdr84uhABHboz94CA2F1";

    @Test
    public void GetIsssuesFromJira(){
        JiraRestClient jiraRestClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(URI.create(jiraUri), jiraUserName, jiraPassword);


        String newJQL = "";

        Stream<Issue> issuesStream = StreamSupport.stream(jiraRestClient
                .getSearchClient()
                .searchJql(newJQL)
                .claim()
                .getIssues()
                .spliterator(), false);
        List<Issue> realList = issuesStream.collect(Collectors.toList());

        System.out.println(issuesStream.findFirst().isPresent());
    }
}
