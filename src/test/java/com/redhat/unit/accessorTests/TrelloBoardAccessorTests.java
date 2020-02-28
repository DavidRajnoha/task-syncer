//package com.redhat.unit.accessorTests;
//
//import com.julienvey.trello.Trello;
//import com.julienvey.trello.domain.Board;
//import com.julienvey.trello.domain.Card;
//import com.julienvey.trello.domain.TList;
//import com.redhat.tasksyncer.dao.accessors.trello.TrelloBoardAccessor;
//import com.redhat.tasksyncer.dao.accessors.trello.TrelloColumnAccessor;
//import com.redhat.tasksyncer.dao.entities.*;
//import com.redhat.tasksyncer.dao.repositories.AbstractBoardRepository;
//import com.redhat.tasksyncer.dao.repositories.AbstractCardRepository;
//import com.redhat.tasksyncer.dao.repositories.AbstractColumnRepository;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.internal.util.reflection.FieldSetter;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.mockito.AdditionalAnswers.returnsFirstArg;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//
//@RunWith(MockitoJUnitRunner.class)
//public class TrelloBoardAccessorTests {
//
//
//    @Mock
//    private AbstractCardRepository mockCardRepository = Mockito.mock(AbstractCardRepository.class);
//    @Mock
//    private AbstractBoardRepository mockBoardRepository;
//    @Mock
//    private AbstractColumnRepository mockColumnRepository;
//    @Mock
//    private TrelloColumnAccessor mockColumnAccessor = Mockito.mock(TrelloColumnAccessor.class);
//
//
//
//    @Mock
//    private Trello mockTrelloApi = Mockito.mock(Trello.class);
//    @Mock
//    private TList mockTList = Mockito.mock(TList.class);
//    @Mock
//    private Card mockTCard = Mockito.mock(Card.class);
//
//    private TrelloBoardAccessor trelloBoardAccessorUnderTest;
//
//    @Mock
//    private AbstractBoard mockBoard = Mockito.mock(TrelloBoard.class);
//
//    private AbstractCard newCard;
//    private String newTitle = "newTitle";
//    private String newDescription = "newDescription";
//    private String remoteColumnIssueId = "remoteColId";
//    private AbstractColumn column;
//
//    private AbstractCard existingCard;
//    private Long existingCardId = 1L;
//
//
//
//
//    @Before
//    public void setup() throws NoSuchFieldException {
//        trelloBoardAccessorUnderTest = new TrelloBoardAccessor(
//                mockBoardRepository);
//
//
//        FieldSetter.setField(trelloBoardAccessorUnderTest, trelloBoardAccessorUnderTest
//                .getClass().getDeclaredField("trelloApi"), mockTrelloApi);
//        FieldSetter.setField(trelloBoardAccessorUnderTest, trelloBoardAccessorUnderTest.getClass()
//        .getDeclaredField("board"), mockBoard);
//
//        newCard = new TrelloCard();
//
//
//        column = new TrelloColumn();
//        column.setRemoteColumnId(remoteColumnIssueId);
//
//
//        newCard.setColumn(column);
//
//        existingCard = new TrelloCard();
//        existingCard.setColumn(column);
//        existingCard.setId(existingCardId);
//
//
//
//        // Mocking
//        doCallRealMethod().when(mockTList).setId(anyString());
//        Mockito.when(mockTList.getId()).thenCallRealMethod();
//
//        doCallRealMethod().when(mockBoard).setRemoteBoardId(anyString());
//        Mockito.when(mockBoard.getRemoteBoardId()).thenCallRealMethod();
//        Mockito.when(mockBoard.getColumns()).thenCallRealMethod();
//        doCallRealMethod().when(mockBoard).addColumn(any());
//
//        doAnswer(i -> {
//            Card argument = i.getArgument(0);
//            argument.setIdList(mockTList.getId());
//            return argument;
//        }).when(mockTList).createCard(any());
//
//        Mockito.when(mockTrelloApi.getList(any())).thenAnswer(i -> {
//            String remoteId = (i.getArgument(0));
//            mockTList.setId(remoteId);
//            return mockTList;
//        });
//
//        doAnswer(returnsFirstArg()).when(mockCardRepository).save(any());
//
//        doAnswer(returnsFirstArg()).when(mockBoardRepository).save(any());
//
//        doAnswer(i -> {
//            Board trelloBoard = new Board();
//            trelloBoard.setId("id");
//            return trelloBoard;
//        }).when(mockTrelloApi).createBoard(any(), any(), any());
//
//    }
//
//    @Test
//    public void creatingNewTrelloCard() {
//        //Preparation
//        ArgumentCaptor<Card> createdCardArgument = ArgumentCaptor.forClass(Card.class);
//
//
//        verify(mockTList, Mockito.times(1)).createCard(createdCardArgument.capture());
//        Card createdCard = createdCardArgument.getValue();
//
//        assertThat(createdCard.getDesc()).isEqualTo(newDescription);
//        assertThat(createdCard.getName()).isEqualTo(newTitle);
//        assertThat(createdCard.getIdList()).isEqualTo(remoteColumnIssueId);
//    }
//
//    @Test
//    public void updatingExistingCard(){
//        //Preparation
//        ArgumentCaptor<Card> createdCardArgument = ArgumentCaptor.forClass(Card.class);
//
//
//        verify(mockTrelloApi, times(1)).updateCard(createdCardArgument.capture());
//        Card createdCard = createdCardArgument.getValue();
//
//        assertThat(createdCard.getDesc()).isEqualTo(newDescription);
//        assertThat(createdCard.getName()).isEqualTo(newTitle);
//        assertThat(createdCard.getIdList()).isEqualTo(remoteColumnIssueId);
//    }
//
//    @Test
//    public void createBoardCreatesColumns(){
//        ArgumentCaptor<String> capturedColumnNames = ArgumentCaptor.forClass(String.class);
//
//        List<String> columnNames = new ArrayList<>();
//        columnNames.add("DONE");
//        columnNames.add("IN PROGRESS");
//        columnNames.add("TODO");
//
//
//        List<String> createdNames = capturedColumnNames.getAllValues();
//
//        assertThat(createdNames).isEqualTo(columnNames);
//
//
//    }
//
//
//}
