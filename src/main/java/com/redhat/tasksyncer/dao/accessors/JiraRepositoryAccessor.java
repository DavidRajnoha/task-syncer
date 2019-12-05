package com.redhat.tasksyncer.dao.accessors;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.JiraIssue;
import com.redhat.tasksyncer.dao.entities.JiraRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * @author David Rajnoha
 * */

@Component
public class JiraRepositoryAccessor extends RepositoryAccessor {
    // JQL for searching issues corresponding to particular project, the KEY will be replaced by the project key
    private static final String JQL = "project=KEY";

    // private static final String URL = "https://NAMESPACE.atlassian.net";
    private static final String URL = "http://NAMESPACE";
    private JiraRestClient jiraRestClient;

    @Autowired
    public JiraRepositoryAccessor(AbstractRepositoryRepository repositoryRepository){
        this.repositoryRepository = repositoryRepository;
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
                .spliterator(), false);


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
                .collect(Collectors.toList());
    }


    @Override
    public void createWebhook(String webhook) throws IOException, GitLabApiException {
        //this functionality is not supported yet
    }

    @Override
    public AbstractRepository createRepositoryOfType() {
        return new JiraRepository();
    }

    @Override
    public Map<String, String> isMappingValid(Map<String, String> mapping) {
        Map<String, String> upperCaseMap = new LinkedHashMap<>();

        for (String key : mapping.keySet()){
            String value  = mapping.get(key);
            key = key.toUpperCase();
            upperCaseMap.put(key, value);
        }

        return upperCaseMap;
    }
}
