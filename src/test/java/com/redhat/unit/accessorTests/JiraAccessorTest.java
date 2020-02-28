//package com.redhat.unit.accessorTests;
//
//import com.atlassian.jira.rest.client.api.domain.Issue;
//import com.atlassian.jira.rest.client.api.domain.Status;
//import com.atlassian.jira.rest.client.api.domain.Subtask;
//import com.redhat.tasksyncer.dao.accessors.repository.JiraRepositoryAccessor;
//import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
//import com.redhat.tasksyncer.dao.entities.repositories.JiraRepository;
//import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
//import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
//import com.redhat.tasksyncer.exceptions.InvalidMappingException;
//import org.joda.time.DateTime;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.net.URI;
//import java.util.*;
//import java.util.stream.Stream;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//import static org.junit.Assert.assertTrue;
//
//public class JiraAccessorTest {
//
//
//    JiraRepositoryAccessor repositoryAccessorUnderTest;
//
//    @Mock
//    AbstractRepositoryRepository repositoryRepository;
//    @Mock
//    AbstractIssueRepository issueRepository;
//
//
//    JiraRepository repository;
//
//    String label_name = "label";
//    String assigneeName = "assignee";
//    String titleOne = "Title_one";
//    String description = "description";
//    Date dueDate = new Date();
//    Date createdAt = new Date();
//    Date closedAt = new Date();
//    private Issue jiraIssueOne;
//    private Subtask subtaskOne;
//    private Subtask subtaskTwo;
//
//    String issueKeyOne = "sub1";
//    String issueKeyTwo = "sub2";
//    String subDescOne = "subDesc1";
//    String subDescTwo = "subDesc2";
//
//    HashSet<Issue> issues;
//    Map<String, String> columnMapping = new LinkedHashMap<>();
//
//    String keyOne = "Open";
//    String keyTwo = "Closed";
//
//    @Before
//    public void setUp() throws NoSuchFieldException {
//        MockitoAnnotations.initMocks(this);
//
//        repositoryAccessorUnderTest = new JiraRepositoryAccessor(repositoryRepository);
//
//        columnMapping.put(keyOne.toUpperCase(), AbstractIssue.STATE_OPENED);
//        columnMapping.put(keyTwo.toUpperCase(), AbstractIssue.STATE_OPENED);
//
//        repository = new JiraRepository();
//        repository.setColumnMapping(columnMapping);
//
//        repositoryAccessorUnderTest.initializeRepository(repository);
//
//        subtaskOne = new Subtask(issueKeyOne, null, subDescOne, null, null);
//        subtaskTwo = new Subtask(issueKeyTwo, null, subDescTwo, null, null);
//
//        Set<Subtask> subtasks = new HashSet<>();
//        subtasks.add(subtaskOne);
//        subtasks.add(subtaskTwo);
//
//        Status status = new Status(URI.create("uri"), 1L, "opened", "issue is open", null);
//
//        jiraIssueOne = new Issue(titleOne, null, null, 2L, null, null, status, description, null, null, null, null, null,
//                new DateTime(createdAt), null, new DateTime(dueDate), null, null, null, null, null, new HashSet<>(), null, null,
//                null, null, null, null, subtasks, null, null, null);
//
//        issues = new HashSet<>();
//        issues.add(jiraIssueOne);
//
//    }
//
//    @Test
//    public void whenConvertingInputFromJira_ThenSubtasksAreCorectlyCreated(){
//        Stream<Issue> issueStream = issues.stream();
//        List<AbstractIssue> abstractIssues = repositoryAccessorUnderTest.proccessIssueStream(issueStream);
//
//        assertThat(abstractIssues.get(0).getChildIssues()).isNotNull();
//
//        Map<String, AbstractIssue> abstractSubIssues = abstractIssues.get(0).getChildIssues();
//        assertThat(abstractSubIssues.size()).isEqualTo(2);
//
//        assertThat(abstractIssues.get(0).getTitle()).isEqualTo(titleOne);
//    }
//
//    @Test
//    public void settingUpCorrectCollumnsSetsCorrectMapping(){
//        Map<String, String> correctMapping = new LinkedHashMap<>();
//        correctMapping.put(keyOne, AbstractIssue.STATE_OPENED);
//        correctMapping.put(keyTwo, AbstractIssue.STATE_CLOSED);
//
//        Map<String, String> foundMapping = repositoryAccessorUnderTest.isMappingValid(correctMapping);
//
//        assertTrue(foundMapping.containsKey(keyOne.toUpperCase()));
//        assertTrue(foundMapping.containsKey(keyTwo.toUpperCase()));
//        assertTrue(foundMapping.containsValue(AbstractIssue.STATE_OPENED));
//        assertTrue(foundMapping.containsValue(AbstractIssue.STATE_CLOSED));
//    }
//
//
//    @Test
//    public void settingUpNonExistingColumnsThrowsMapping(){
//        Map<String, String> incorrectMapping = new LinkedHashMap<>();
//        incorrectMapping.put("not Correct", AbstractIssue.STATE_OPENED);
//        incorrectMapping.put("definitely not Correct", AbstractIssue.STATE_CLOSED);
//
//        assertThatThrownBy(() -> repositoryAccessorUnderTest.isMappingValid(incorrectMapping))
//                .isInstanceOf(InvalidMappingException.class);
//    }
//
//
//}
