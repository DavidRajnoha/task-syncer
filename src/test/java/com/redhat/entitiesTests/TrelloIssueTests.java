package com.redhat.entitiesTests;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Label;
import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.TrelloIssue;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TrelloIssueTests {



    String label_name = "label";
    String assigneeName = "assignee";
    String titleOne = "Title_one";
    String description = "description";
    String remoteIssueId = "1";
    Date dueDate = new Date();
    Date createdAt = new Date();
    Date closedAt = new Date();

    List<Label> tlabels;

    @Before
    public void setup(){
        tlabels = new ArrayList<>();
        Label tlabel = new Label();
        tlabel.setName(label_name);
        tlabels.add(tlabel);
    }


    @Test
    public void whenDecodingIssueFromTCard_thenAbstractIssueIsCreated(){
        Card trelloCard = new Card();
        trelloCard.setName(titleOne);
        trelloCard.setDue(dueDate);
        trelloCard.setDesc(description);
        trelloCard.setIdShort(remoteIssueId);
        trelloCard.setLabels(tlabels);

        AbstractIssue convertedIssue = TrelloIssue.ObjectToTrelloIssueConvertor.convert(trelloCard);

        assertThat(convertedIssue.getIssueType()).isEqualTo(IssueType.TRELLO);
        assertThat(convertedIssue.getTitle()).isEqualTo(titleOne);
//        assertThat(convertedIssue.getState()).isEqualTo(AbstractIssue.STATE_OPENED);
        assertThat(convertedIssue.getDescription()).isEqualTo(description);
        assertThat(convertedIssue.getDueDate()).isEqualTo(dueDate);
//        assertThat(convertedIssue.getCreatedAt()).isEqualTo(createdAt);
//        assertThat(convertedIssue.getClosedAt()).isEqualTo(closedAt);
//        assertThat(convertedIssue.getAssignee()).isEqualTo(assigneeName);
//        assertThat(convertedIssue.getClosedBy()).isEqualTo(assigneeName);
        assertThat(convertedIssue.getRemoteIssueId()).isEqualTo(String.valueOf(remoteIssueId));


        assert(convertedIssue.getLabels().contains(label_name));

    }

    @Test
    public void whenDecodingEmptyTCard_thenNoNullPointerEsceptionAreThrown(){
        Card trelloCard = new Card();
        trelloCard.setIdShort(remoteIssueId);

        AbstractIssue convertedEmptyIssue = TrelloIssue.ObjectToTrelloIssueConvertor.convert(trelloCard);

        assertThat(convertedEmptyIssue.getRemoteIssueId()).isEqualTo(remoteIssueId);
    }

    // Testing method from the abstract parent class
    @Test
    public void whenConvertingUpdatedIssue_thenAllFieldsAreConverted(){
        AbstractIssue oldIssue = new TrelloIssue();
        oldIssue.setTitle(titleOne);
        oldIssue.setDescription(description);
        oldIssue.setDueDate(dueDate);

        AbstractIssue newIssue = new TrelloIssue();
        newIssue.setTitle("new Title");
        newIssue.setDescription("new Description");
        newIssue.setDueDate(new Date());

        oldIssue.updateProperties(newIssue);

        assertThat(oldIssue.getTitle()).isEqualTo(newIssue.getTitle());
        assertThat(oldIssue.getDescription()).isEqualTo(newIssue.getDescription());
        assertThat(oldIssue.getDueDate()).isEqualTo(newIssue.getDueDate());
    }

    // Testing method from the abstract parent class
    @Test
    public void whenConvertingUpdatedIssueWithoutSomeFields_thenTheseFieldsAreNotChangedInTheOldIssue(){
        AbstractIssue oldIssue = new TrelloIssue();
        oldIssue.setTitle(titleOne);
        oldIssue.setDescription(description);
        oldIssue.setDueDate(dueDate);

        AbstractIssue newIssue = new TrelloIssue();
        newIssue.setTitle("new Title");
        newIssue.setDescription(null);
        newIssue.setDueDate(new Date());

        assertThat(oldIssue.getDescription()).isNotEmpty();
        assertThat(oldIssue.getDescription()).isEqualTo(description);
    }
}
