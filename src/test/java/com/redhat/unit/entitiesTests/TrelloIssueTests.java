package com.redhat.unit.entitiesTests;

import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Label;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.issues.TrelloIssue;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import com.redhat.tasksyncer.exceptions.TrelloCalllbackNotAboutCardException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TrelloIssueTests {

    String toJSON = "{\"model\":{\"id\":\"5d665532a3b61963b8a4dc51\",\"name\":\"Task Syncer\",\"desc\":\"\",\"descData\":null,\"closed\":false,\"idOrganization\":null,\"idEnterprise\":null,\"pinned\":false,\"url\":\"https://trello.com/b/zwQYthQw/task-syncer\",\"shortUrl\":\"https://trello.com/b/zwQYthQw\",\"prefs\":{\"permissionLevel\":\"private\",\"hideVotes\":false,\"voting\":\"disabled\",\"comments\":\"members\",\"invitations\":\"members\",\"selfJoin\":true,\"cardCovers\":true,\"isTemplate\":false,\"cardAging\":\"regular\",\"calendarFeedEnabled\":false,\"background\":\"5d61fb3140493340077e0857\",\"backgroundImage\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/2560x1707/733ef9444105a5ab90825b99ebe74a9e/photo-1566578143640-f0c819f43c8e\",\"backgroundImageScaled\":[{\"width\":140,\"height\":93,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/140x93/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":256,\"height\":171,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/256x171/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":480,\"height\":320,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/480x320/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":960,\"height\":640,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/960x640/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":1024,\"height\":683,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/1024x683/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":2048,\"height\":1366,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/2048x1366/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":1280,\"height\":854,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/1280x854/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":1920,\"height\":1280,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/1920x1280/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":2400,\"height\":1600,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/2400x1600/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":2560,\"height\":1707,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/2560x1707/733ef9444105a5ab90825b99ebe74a9e/photo-1566578143640-f0c819f43c8e\"}],\"backgroundTile\":false,\"backgroundBrightness\":\"dark\",\"backgroundBottomColor\":\"#2c3630\",\"backgroundTopColor\":\"#b0b8be\",\"canBePublic\":true,\"canBeEnterprise\":true,\"canBeOrg\":true,\"canBePrivate\":true,\"canInvite\":true},\"labelNames\":{\"green\":\"Label One\",\"yellow\":\"\",\"orange\":\"\",\"red\":\"\",\"purple\":\"\",\"blue\":\"BLUEEEE\",\"sky\":\"\",\"lime\":\"\",\"pink\":\"\",\"black\":\"\"}},\"action\":{\"id\":\"5de8f902dd35274ec0a743bc\",\"idMemberCreator\":\"5c926c4a54bcda83718470ed\",\"data\":{\"old\":{\"idList\":\"5d665541334fc263e03baae4\"},\"card\":{\"idList\":\"5d66554459b0a950d8c69c0c\",\"id\":\"5d9b3a202e23192f906a6991\",\"name\":\"Testing Card Callback Changed Twice edited cool\",\"idShort\":23,\"shortLink\":\"s1GAW4kG\"},\"board\":{\"id\":\"5d665532a3b61963b8a4dc51\",\"name\":\"Task Syncer\",\"shortLink\":\"zwQYthQw\"},\"listBefore\":{\"id\":\"5d665541334fc263e03baae4\",\"name\":\"Backlog\"},\"listAfter\":{\"id\":\"5d66554459b0a950d8c69c0c\",\"name\":\"Next\"}},\"type\":\"updateCard\",\"date\":\"2019-12-05T12:33:06.816Z\",\"limits\":{},\"display\":{\"translationKey\":\"action_move_card_from_list_to_list\",\"entities\":{\"card\":{\"type\":\"card\",\"idList\":\"5d66554459b0a950d8c69c0c\",\"id\":\"5d9b3a202e23192f906a6991\",\"shortLink\":\"s1GAW4kG\",\"text\":\"Testing Card Callback Changed Twice edited cool\"},\"listBefore\":{\"type\":\"list\",\"id\":\"5d665541334fc263e03baae4\",\"text\":\"Backlog\"},\"listAfter\":{\"type\":\"list\",\"id\":\"5d66554459b0a950d8c69c0c\",\"text\":\"Next\"},\"memberCreator\":{\"type\":\"member\",\"id\":\"5c926c4a54bcda83718470ed\",\"username\":\"davidrajnoha\",\"text\":\"David Rajnoha\"}}},\"memberCreator\":{\"id\":\"5c926c4a54bcda83718470ed\",\"activityBlocked\":false,\"avatarHash\":null,\"avatarUrl\":null,\"fullName\":\"David Rajnoha\",\"idMemberReferrer\":null,\"initials\":\"DR\",\"nonPublic\":{},\"nonPublicAvailable\":false,\"username\":\"davidrajnoha\"}}}";
    JSONObject jsonObject;

    String label_name = "label";
    String assigneeName = "assignee";
    String titleOne = "Title_one";
    String description = "description";
    String remoteIssueId = "1";
    Date dueDate = new Date();
    Date createdAt = new Date();
    Date closedAt = new Date();

    List<Label> tlabels;

    Map<String, String> columnMapping = new LinkedHashMap<>();
    String secondMappedColumn = "DONE";

    @Before
    public void setup() throws JSONException {
        tlabels = new ArrayList<>();
        Label tlabel = new Label();
        tlabel.setName(label_name);
        tlabels.add(tlabel);

        jsonObject = new JSONObject(toJSON);

        columnMapping.put("idList", "TODO");
        columnMapping.put("5d66554459b0a950d8c69c0c", secondMappedColumn);
    }


    @Test
    public void whenDecodingIssueFromTCard_thenAbstractIssueIsCreated(){
        Card trelloCard = new Card();
        trelloCard.setName(titleOne);
        trelloCard.setDue(dueDate);
        trelloCard.setDesc(description);
        trelloCard.setIdShort(remoteIssueId);
        trelloCard.setLabels(tlabels);
        trelloCard.setIdList("idList");

        AbstractIssue convertedIssue = TrelloIssue.ObjectToTrelloIssueConvertor.convert(trelloCard, columnMapping);

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
        assertThat(convertedIssue.getState()).isEqualTo("TODO");

        assertThat(convertedIssue.getLabels().isPresent());
        assert(convertedIssue.getLabels().get().contains(label_name));

    }

    @Test
    public void whenDecodingEmptyTCard_thenNoNullPointerExceptionsAreThrown(){
        Card trelloCard = new Card();
        trelloCard.setIdShort(remoteIssueId);

        AbstractIssue convertedEmptyIssue = TrelloIssue.ObjectToTrelloIssueConvertor.convert(trelloCard, columnMapping);

        assertThat(convertedEmptyIssue.getRemoteIssueId()).isEqualTo(remoteIssueId);
    }

    @Test
    public void whenDecodingFromJsonInput_thenIsTheIssueCorrectlyCreated()
            throws JSONException, TrelloCalllbackNotAboutCardException {
        AbstractIssue convertedIssue = TrelloIssue.ObjectToTrelloIssueConvertor.convert(jsonObject, columnMapping);

        assertThat(convertedIssue.getState()).isEqualTo(secondMappedColumn);
        assertThat(convertedIssue.getTitle()).isEqualTo("Testing Card Callback Changed Twice edited cool");
        assertThat(convertedIssue.getRemoteIssueId()).isEqualTo("23");
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
