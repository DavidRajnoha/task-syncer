package com.redhat.accessorTests;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.redhat.tasksyncer.dao.accessors.JiraRepositoryAccessor;
import com.redhat.tasksyncer.dao.accessors.RepositoryAccessor;
import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.JiraRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class JiraAccessorTest {

    @Autowired
    AbstractRepositoryRepository repositoryRepository;

    @Autowired
    AbstractIssueRepository issueRepository;

    JiraRepositoryAccessor repositoryAccessor;

    JiraRepository repository;
    String label_name = "label";
    String assigneeName = "assignee";
    String titleOne = "Title_one";
    String description = "description";
    Date dueDate = new Date();
    Date createdAt = new Date();
    Date closedAt = new Date();
    private Issue jiraIssueOne;
    private Subtask subtaskOne;
    private Subtask subtaskTwo;

    String issueKeyOne = "sub1";
    String issueKeyTwo = "sub2";
    String subDescOne = "subDesc1";
    String subDescTwo = "subDesc2";

    HashSet<Issue> issues;


    @Before
    public void setUp(){
        repository = new JiraRepository();
        repositoryAccessor = new JiraRepositoryAccessor(repository, repositoryRepository, issueRepository);
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
        List<AbstractIssue> abstractIssues = repositoryAccessor.proccessIssueStream(issueStream);

        assertThat(abstractIssues.get(0).getChildIssues()).isNotNull();

        Set<AbstractIssue> abstractSubIssues = abstractIssues.get(0).getChildIssues();
        assertThat(abstractSubIssues.size()).isEqualTo(2);

        assertThat(abstractIssues.get(0).getTitle()).isEqualTo(titleOne);
    }


}
