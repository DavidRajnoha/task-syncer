package com.redhat.unit.accessorTests.remoteRepositoryAccessorTest;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.redhat.tasksyncer.dao.accessors.remoteRepository.JiraRemoteRepositoryAccessor;
import com.redhat.tasksyncer.dao.accessors.remoteRepository.RemoteRepositoryAccessor;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.repositories.JiraRepository;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;

import java.net.URI;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class JiraRemoteRepositoryAccessorTest {




    private JiraRemoteRepositoryAccessor repositoryAccessorUnderTest;
    private JiraRepository repository;


    String titleOne = "Title_one";
    String description = "description";
    Date dueDate = new Date();
    Date createdAt = new Date();
    private Issue jiraIssueOne;
    private Subtask subtaskOne;
    private Subtask subtaskTwo;

    String issueKeyOne = "sub1";
    String issueKeyTwo = "sub2";
    String subDescOne = "subDesc1";
    String subDescTwo = "subDesc2";

    HashSet<Issue> issues;
    Map<String, String> columnMapping = new LinkedHashMap<>();

    String keyOne = "Open";
    String keyTwo = "Closed";

    @Before
    public void setUp() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);

        repositoryAccessorUnderTest = new JiraRemoteRepositoryAccessor();

        repository = new JiraRepository();
        repository.setColumnMapping(columnMapping);

        FieldSetter.setField(repositoryAccessorUnderTest,
                RemoteRepositoryAccessor.class.getDeclaredField("repository"), repository);


        subtaskOne = new Subtask(issueKeyOne, null, subDescOne, null, null);
        subtaskTwo = new Subtask(issueKeyTwo, null, subDescTwo, null, null);

        Set<Subtask> subtasks = new HashSet<>();
        subtasks.add(subtaskOne);
        subtasks.add(subtaskTwo);

        Status status = new Status(URI.create("uri"), 1L, "opened", "issue is open", null);

        jiraIssueOne = new Issue(titleOne, null, null, 2L, null, null, status, description, null, null, null, null, null,
                new DateTime(createdAt), null, new DateTime(dueDate), null, null, null, null, null, new HashSet<>(), null, null,
                null, null, null, null, subtasks, null, null, null);

        issues = new HashSet<>();
        issues.add(jiraIssueOne);

    }


    @Test
    public void whenConvertingInputFromJira_ThenSubtasksAreCorectlyCreated(){
        Stream<Issue> issueStream = issues.stream();
        List<AbstractIssue> abstractIssues = repositoryAccessorUnderTest.proccessIssueStream(issueStream);

        assertThat(abstractIssues.get(0).getChildIssues()).isNotNull();

        Map<String, AbstractIssue> abstractSubIssues = abstractIssues.get(0).getChildIssues();
        assertThat(abstractSubIssues.size()).isEqualTo(2);

        assertThat(abstractIssues.get(0).getTitle()).isEqualTo(titleOne);
    }
}
