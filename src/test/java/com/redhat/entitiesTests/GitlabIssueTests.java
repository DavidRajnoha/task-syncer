package com.redhat.entitiesTests;

import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.GitlabIssue;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.models.Assignee;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.Label;
import org.gitlab4j.api.models.User;
import org.gitlab4j.api.webhook.IssueEvent;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GitlabIssueTests {

    Issue glIssueOne;
    Issue glIssueTwo;
    Assignee assignee;
    List<String> labels;

    Label label;
    String label_name = "label";
    User user;
    String assigneeName = "assignee";
    String titleOne = "Title_one";
    String description = "description";
    Date dueDate = new Date();
    Date createdAt = new Date();
    Date closedAt = new Date();

    @Before
    public void setUp(){
        assignee = new Assignee();
        assignee.setName(assigneeName);

        user = new User();
        user.setName(assigneeName);

        labels = new ArrayList<>();

        label = new Label();
        label.setName(label_name);
        label.setColor("BLACK");

       labels.add(label_name);

        glIssueOne = new Issue();
        glIssueOne.setAssignee(assignee);
        glIssueOne.setId(1);
        glIssueOne.setTitle(titleOne);
        glIssueOne.setDescription(description);
        glIssueOne.setState(Constants.IssueState.CLOSED);
        glIssueOne.setDueDate(dueDate);
        glIssueOne.setClosedAt(closedAt);
        glIssueOne.setCreatedAt(createdAt);
        glIssueOne.setClosedBy(user);
        glIssueOne.setLabels(labels);

        glIssueTwo = new Issue();
        glIssueTwo.setId(2);

    }

    @Test
    public void whenConvertingInput_thenAllFieldsShouldBeTransmitted(){
        AbstractIssue convertedIssue = GitlabIssue.ObjectToGitlabIssueConverter.convert(glIssueOne);
        assertThat(convertedIssue.getIssueType()).isEqualTo(IssueType.GITLAB);
        assertThat(convertedIssue.getTitle()).isEqualTo(titleOne);
        assertThat(convertedIssue.getState()).isEqualTo(AbstractIssue.STATE_CLOSED);
        assertThat(convertedIssue.getDescription()).isEqualTo(description);
        assertThat(convertedIssue.getDueDate()).isEqualTo(dueDate);
        assertThat(convertedIssue.getCreatedAt()).isEqualTo(createdAt);
        assertThat(convertedIssue.getClosedAt()).isEqualTo(closedAt);
        assertThat(convertedIssue.getAssignee()).isEqualTo(assigneeName);
        assertThat(convertedIssue.getClosedBy()).isEqualTo(assigneeName);
        assertThat(convertedIssue.getRemoteIssueId()).isEqualTo(String.valueOf(1));


        assert(convertedIssue.getLabels().contains(label_name));
    }

    @Test
    public void whenConvertingIssueEvent_thenAllFieldsAreCorrectlyCreated(){
        IssueEvent issueEvent = new IssueEvent();
        IssueEvent.ObjectAttributes objectAttributes = new IssueEvent.ObjectAttributes();
        Assignee assignee = new Assignee();
        assignee.setName(assigneeName);

        objectAttributes.setId(3);
        objectAttributes.setTitle(titleOne);
        objectAttributes.setCreatedAt(createdAt);
        objectAttributes.setDescription(description);
        objectAttributes.setState("opened");
        issueEvent.setObjectAttributes(objectAttributes);


        AbstractIssue convertedIssue = GitlabIssue.ObjectToGitlabIssueConverter.convert(issueEvent.getObjectAttributes());

        assertThat(convertedIssue.getIssueType()).isEqualTo(IssueType.GITLAB);
        assertThat(convertedIssue.getTitle()).isEqualTo(titleOne);
        assertThat(convertedIssue.getDescription()).isEqualTo(description);
        assertThat(convertedIssue.getRemoteIssueId()).isEqualTo(String.valueOf(3));


        // IssueEvent.ObjectAttributes does not have method to
        // assertThat(convertedIssue.getCreatedAt()).isEqualTo(createdAt);


    }

    @Test
    public void whenConvertingInputWithNullValuesButId_thenIsIssueErrorlessProcessed(){
        AbstractIssue foundIssue = GitlabIssue.ObjectToGitlabIssueConverter.convert(glIssueTwo);

        assertThat(foundIssue.getRemoteIssueId()).isEqualTo(String.valueOf(2));
    }
}
