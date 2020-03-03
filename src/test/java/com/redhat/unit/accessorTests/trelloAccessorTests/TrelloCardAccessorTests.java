package com.redhat.unit.accessorTests.trelloAccessorTests;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.redhat.tasksyncer.dao.accessors.trello.AbstractTrelloAccessor;
import com.redhat.tasksyncer.dao.accessors.trello.TrelloCardAccessor;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.issues.TrelloIssue;
import com.redhat.tasksyncer.dao.entities.trello.AbstractCard;
import com.redhat.tasksyncer.dao.entities.trello.AbstractColumn;
import com.redhat.tasksyncer.dao.entities.trello.TrelloCard;
import com.redhat.tasksyncer.dao.entities.trello.TrelloColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TrelloCardAccessorTests {
    @Mock
    private Trello mockTrelloApi;

    @Mock
    private TList mockTList;

    @Mock
    private Card mockTCard;

    @InjectMocks
    private TrelloCardAccessor cardAccessorUnderTests;

    private AbstractIssue issueToCreate;
    private AbstractColumn column = new TrelloColumn();
    private String columnName = "TODO";
    private List<AbstractColumn> columns = new ArrayList<>();


    private AbstractCard newCard;
    private String newTitle = "newTitle";
    private String newDescription = "newDescription";
    private String remoteColumnIssueId = "remoteColId";

    private AbstractCard existingCard;
    private Long existingCardId = 1L;




    @Before
    public void setup() throws NoSuchFieldException {
        cardAccessorUnderTests = new TrelloCardAccessor();


        FieldSetter.setField(cardAccessorUnderTests, AbstractTrelloAccessor.class
                .getDeclaredField("trelloApi"), mockTrelloApi);


        newCard = new TrelloCard();

        column = new TrelloColumn();
        column.setRemoteColumnId(remoteColumnIssueId);
        column.setName(columnName);

        newCard.setColumn(column);

        existingCard = new TrelloCard();
        existingCard.setColumn(column);
        existingCard.setId(existingCardId);


        issueToCreate = new TrelloIssue();
        issueToCreate.setTitle(newTitle);
        issueToCreate.setDescription(newDescription);
        issueToCreate.setState(columnName);

        columns.add(column);



        // Mocking
        doCallRealMethod().when(mockTList).setId(anyString());
        Mockito.when(mockTList.getId()).thenCallRealMethod();

        doAnswer(i -> {
            Card argument = i.getArgument(0);
            argument.setIdList(mockTList.getId());
            return argument;
        }).when(mockTList).createCard(any());

        doAnswer(returnsFirstArg()).when(mockTrelloApi).updateCard(any());

        Mockito.when(mockTrelloApi.getList(any())).thenAnswer(i -> {
            String remoteId = (i.getArgument(0));
            mockTList.setId(remoteId);
            return mockTList;
        });

        doAnswer(i -> {
            Board trelloBoard = new Board();
            trelloBoard.setId("id");
            return trelloBoard;
        }).when(mockTrelloApi).createBoard(any(), any(), any());

    }

    @Test
    public void creatingNewTrelloCard() {
        //Preparation
        ArgumentCaptor<Card> createdCardArgument = ArgumentCaptor.forClass(Card.class);

        AbstractIssue createdIssue = cardAccessorUnderTests.createCard(issueToCreate, columns);

        verify(mockTList, Mockito.times(1)).createCard(createdCardArgument.capture());
        Card createdCard = createdCardArgument.getValue();

        assertThat(createdCard.getDesc()).isEqualTo(newDescription);
        assertThat(createdCard.getName()).isEqualTo(newTitle);
        assertThat(createdCard.getIdList()).isEqualTo(remoteColumnIssueId);
    }

    @Test
    public void updatingExistingCard(){
        //Preparation
        ArgumentCaptor<Card> createdCardArgument = ArgumentCaptor.forClass(Card.class);
        issueToCreate.setCard(newCard);

        cardAccessorUnderTests.updateCard(issueToCreate, columns);

        verify(mockTrelloApi, times(1)).updateCard(createdCardArgument.capture());
        Card createdCard = createdCardArgument.getValue();

        assertThat(createdCard.getDesc()).isEqualTo(newDescription);
        assertThat(createdCard.getName()).isEqualTo(newTitle);
        assertThat(createdCard.getIdList()).isEqualTo(remoteColumnIssueId);

        issueToCreate.setCard(null);
    }


}
