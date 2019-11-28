package com.redhat.unit.accessorTests;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.redhat.tasksyncer.dao.accessors.TrelloBoardAccessor;
import com.redhat.tasksyncer.dao.entities.AbstractCard;
import com.redhat.tasksyncer.dao.entities.AbstractColumn;
import com.redhat.tasksyncer.dao.entities.TrelloCard;
import com.redhat.tasksyncer.dao.entities.TrelloColumn;
import com.redhat.tasksyncer.dao.repositories.AbstractBoardRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractCardRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractColumnRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class TrelloBoardAccessorTests {


    @MockBean
    private AbstractCardRepository mockCardRepository = Mockito.mock(AbstractCardRepository.class);
    @MockBean
    private AbstractBoardRepository mockBoardRepository;
    @MockBean
    private AbstractColumnRepository mockColumnRepository;


    @Mock
    private Trello mockTrelloApi = Mockito.mock(Trello.class);
    @Mock
    private TList mockTList = Mockito.mock(TList.class);
    @Mock
    private Card mockTCard = Mockito.mock(Card.class);

    private TrelloBoardAccessor trelloBoardAccessorUnderTest;

    private AbstractCard newCard;
    private String newTitle = "newTitle";
    private String newDescription = "newDescription";
    private String remoteColumnIssueId = "remoteColId";
    private AbstractColumn column;

    private AbstractCard existingCard;
    private Long existingCardId = 1L;




    @Before
    public void setup() throws NoSuchFieldException {
        trelloBoardAccessorUnderTest = new TrelloBoardAccessor(
                mockBoardRepository, mockCardRepository, mockColumnRepository);

        FieldSetter.setField(trelloBoardAccessorUnderTest, trelloBoardAccessorUnderTest
                .getClass().getDeclaredField("trelloApi"), mockTrelloApi);

        newCard = new TrelloCard();
        newCard.setTitle(newTitle);
        newCard.setDescription(newDescription);

        column = new TrelloColumn();
        column.setRemoteColumnId(remoteColumnIssueId);


        newCard.setColumn(column);

        existingCard = new TrelloCard();
        existingCard.setTitle(newTitle);
        existingCard.setDescription(newDescription);
        existingCard.setColumn(column);
        existingCard.setId(existingCardId);



        // Mocking
        doCallRealMethod().when(mockTList).setId(anyString());
        Mockito.when(mockTList.getId()).thenCallRealMethod();

        doAnswer(i -> {
            Card argument = i.getArgument(0);
            argument.setIdList(mockTList.getId());
            return argument;
        }).when(mockTList).createCard(any());

        Mockito.when(mockTrelloApi.getList(any())).thenAnswer(i -> {
            String remoteId = (i.getArgument(0));
            mockTList.setId(remoteId);
            return mockTList;
        });

        doAnswer(returnsFirstArg()).when(mockCardRepository).save(any());
    }

    @Test
    public void creatingNewTrelloCard() {
        //Preparation
        ArgumentCaptor<Card> createdCardArgument = ArgumentCaptor.forClass(Card.class);

        // Execution
        AbstractCard updatedCard = trelloBoardAccessorUnderTest.update(newCard);

        // Assertions
        assertThat(updatedCard.getDescription()).isEqualTo(newDescription);
        assertThat(updatedCard.getTitle()).isEqualTo(newTitle);

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


        //Execution
        AbstractCard updatedCard = trelloBoardAccessorUnderTest.update(existingCard);

        //Assertions
        assertThat(updatedCard.getDescription()).isEqualTo(newDescription);
        assertThat(updatedCard.getTitle()).isEqualTo(newTitle);

        verify(mockTrelloApi, times(1)).updateCard(createdCardArgument.capture());
        Card createdCard = createdCardArgument.getValue();

        assertThat(createdCard.getDesc()).isEqualTo(newDescription);
        assertThat(createdCard.getName()).isEqualTo(newTitle);
        assertThat(createdCard.getIdList()).isEqualTo(remoteColumnIssueId);
    }


}
