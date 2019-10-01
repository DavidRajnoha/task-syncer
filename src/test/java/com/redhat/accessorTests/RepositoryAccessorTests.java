//Do not know how to mock JiraRestClient and insert it into the method




//package com.redhat.accessorTests;
//
//import com.atlassian.jira.rest.client.api.JiraRestClient;
//import com.atlassian.jira.rest.client.api.SearchRestClient;
//import com.atlassian.jira.rest.client.api.domain.Issue;
//import com.atlassian.jira.rest.client.api.domain.SearchResult;
//import com.atlassian.util.concurrent.Promise;
//import com.redhat.tasksyncer.dao.accessors.JiraRepositoryAccessor;
//import com.redhat.tasksyncer.dao.entities.AbstractIssue;
//import com.redhat.tasksyncer.dao.entities.JiraRepository;
//import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
//import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Spliterator;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//
//@RunWith(MockitoJUnitRunner.class)
//public class RepositoryAccessorTests {
//
//
//    @Mock
//    JiraRestClient jiraRestClient = Mockito.mock(JiraRestClient.class);
//
//    @Mock
//    AbstractRepositoryRepository repositoryRepository;
//
//    @Mock
//    AbstractIssueRepository issueRepository = Mockito.mock(AbstractIssueRepository.class);
//
//    @Mock
//    SearchRestClient mockClient;
//
//    @Mock
//    private Promise<SearchResult> mockPromise;
//
//    @Mock
//    SearchResult mockResult;
//
//    @InjectMocks
//    JiraRepository jiraRepository = Mockito.mock(JiraRepository.class);
//
//
//    String titleOne = "summartyOne";
//    String titleTwo  = "summaryTwo";
//
//    Issue jiraIssueOne = new Issue(titleOne, null, null, 1L, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
//    Issue jiraIssueTwo = new Issue(titleTwo, null, null, 2L, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
//
//    private String jql = "project = \"project_name\"";
//
//    public JiraRepositoryAccessor jiraRepositoryAccessor;
//
//    @Before
//    public void setup(){
//        List<Issue> issueList = new ArrayList<>();
//        issueList.add(jiraIssueOne);
//        issueList.add(jiraIssueTwo);
//
//        Mockito.when(jiraRestClient.getSearchClient()).thenReturn(mockClient);
//        Mockito.when(mockClient.searchJql(jql)).thenReturn(mockPromise);
//        Mockito.when(mockPromise.claim()).thenReturn(mockResult);
//        Mockito.when(mockResult.getIssues()).thenReturn(issueList);
//        Mockito.when(jiraRepository.getRepositoryName()).thenReturn("project_name");
//
//        jiraRepositoryAccessor = new JiraRepositoryAccessor(jiraRepository, repositoryRepository, issueRepository);
//
//
//    }
//
//    @Test
//    public void jiraDoSync_returnsJiraIssues() throws Exception {
//        List<AbstractIssue> foundList = jiraRepositoryAccessor.downloadAllIssues();
//        assertThat(foundList.get(1).getTitle()).isEqualTo(titleOne);
//        assertThat(foundList.get(2).getTitle()).isEqualTo(titleTwo);
//    }
//}
