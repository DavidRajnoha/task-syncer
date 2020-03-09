package com.redhat.unit.entitiesTests;

import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.issues.GitlabIssue;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.models.Assignee;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.Label;
import org.gitlab4j.api.models.User;
import org.gitlab4j.api.webhook.IssueEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GitlabIssueTests {

    private Issue glIssueOne;
    private Issue glIssueTwo;
    private Issue glIssueThree;
    private Assignee assignee;
    private List<String> labels;

    private Map<String, String> columnMapping = new HashMap<>();

    private Label label;
    private String label_name = "label";
    private User user;
    private String assigneeName = "assignee";
    private String titleOne = "Title_one";
    private String description = "description";
    private Date dueDate = new Date();
    private Date createdAt = new Date();
    private Date closedAt = new Date();

    private static final String CLOSED = "CLOSED";
    private static final String OPENED = "OPENED";
    private static final String REOPENED = "REOPENED";
    private static final String DONE_CUSTOM = "DONE";
    private static final String TODO_CUSTOM = "NEXT";


    private IssueEvent issueEventStates = new IssueEvent();
    private IssueEvent.ObjectAttributes objectAttributesStates = new IssueEvent.ObjectAttributes();


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
        glIssueTwo.setState(Constants.IssueState.OPENED);

        glIssueThree = new Issue();
        glIssueThree.setId(5);

        columnMapping.put(AbstractIssue.STATE_OPENED, TODO_CUSTOM);
        columnMapping.put(AbstractIssue.STATE_CLOSED, DONE_CUSTOM);
        columnMapping.put(AbstractIssue.STATE_REOPENED, TODO_CUSTOM);

        objectAttributesStates.setId(4);

    }

    @Test
    public void whenConvertingInput_thenAllFieldsShouldBeTransmitted(){
        AbstractIssue convertedIssue = GitlabIssue.ObjectToGitlabIssueConverter.convert(glIssueOne, columnMapping);
        assertThat(convertedIssue.getIssueType()).isEqualTo(IssueType.GITLAB);
        assertThat(convertedIssue.getTitle()).isEqualTo(titleOne);
        assertThat(convertedIssue.getDescription()).isEqualTo(description);
        assertThat(convertedIssue.getDueDate()).isEqualTo(dueDate);
        assertThat(convertedIssue.getCreatedAt()).isEqualTo(createdAt);
        assertThat(convertedIssue.getClosedAt()).isEqualTo(closedAt);
        assertThat(convertedIssue.getAssignee()).isEqualTo(assigneeName);
        assertThat(convertedIssue.getClosedBy()).isEqualTo(assigneeName);
        assertThat(convertedIssue.getRemoteIssueId()).isEqualTo(String.valueOf(1));


        assertThat(convertedIssue.getLabels().isPresent());
        assertThat(convertedIssue.getLabels().get().contains(label_name));
    }

    @Test
    public void whenConvertingClosedInput_thenStateIsMappedAccordingToMapping(){
        whenConvertingInput_thenStateIsMappedAccordingToMapping(
                Constants.IssueState.CLOSED, DONE_CUSTOM
        );
    }

    @Test
    public void whenConvertingOpenedInput_thenStateIsMappedAccordingToMapping(){
        whenConvertingInput_thenStateIsMappedAccordingToMapping(
                Constants.IssueState.OPENED, TODO_CUSTOM
        );
    }

    @Test
    public void whenConvertingReopenedInput_thenStateIsMappedAccordingToMapping(){
        whenConvertingInput_thenStateIsMappedAccordingToMapping(
                Constants.IssueState.REOPENED, TODO_CUSTOM);
    }

    private void whenConvertingInput_thenStateIsMappedAccordingToMapping(
            Constants.IssueState setState, String customState){
        glIssueThree.setState(setState);

        AbstractIssue convertedIssue = GitlabIssue.ObjectToGitlabIssueConverter
                .convert(glIssueThree, columnMapping);

        assertThat(convertedIssue.getState()).isEqualTo(customState);
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


        AbstractIssue convertedIssue = GitlabIssue.ObjectToGitlabIssueConverter
                .convert(issueEvent.getObjectAttributes(), columnMapping);

        assertThat(convertedIssue.getIssueType()).isEqualTo(IssueType.GITLAB);
        assertThat(convertedIssue.getTitle()).isEqualTo(titleOne);
        assertThat(convertedIssue.getDescription()).isEqualTo(description);
        assertThat(convertedIssue.getRemoteIssueId()).isEqualTo(String.valueOf(3));


        // IssueEvent.ObjectAttributes does not have method to
        // assertThat(convertedIssue.getCreatedAt()).isEqualTo(createdAt);
    }


    @Test
    public void whenConvertingOpenedIssueEvent_thenStateIsSetAccordingToMapping(){
       whenConvertingIssueEvent_thenStateIsSetAccordingToMapping("opened", TODO_CUSTOM);
    }


    @Test
    public void whenConvertingClosedIssueEvent_thenStateIsSetAccordingToMapping(){
        whenConvertingIssueEvent_thenStateIsSetAccordingToMapping("closed", DONE_CUSTOM);
    }

    @Test
    public void whenConvertingReopenedIssueEvent_thenStateIsSetAccordingToMapping(){
        whenConvertingIssueEvent_thenStateIsSetAccordingToMapping("reopened", TODO_CUSTOM);
    }


    private void whenConvertingIssueEvent_thenStateIsSetAccordingToMapping(String setState,
                                                                           String customState){
        objectAttributesStates.setState(setState);
        issueEventStates.setObjectAttributes(objectAttributesStates);

        AbstractIssue convertedIssue = GitlabIssue.ObjectToGitlabIssueConverter
                .convert(issueEventStates.getObjectAttributes(), columnMapping);

        assertThat(convertedIssue.getState()).isEqualTo(customState);
    }

    @Test
    public void whenConvertingInputWithNullValuesButId_thenIsIssueErrorlessProcessed(){
        AbstractIssue foundIssue = GitlabIssue.ObjectToGitlabIssueConverter
                .convert(glIssueTwo, columnMapping);

        assertThat(foundIssue.getRemoteIssueId()).isEqualTo(String.valueOf(2));
    }
}
