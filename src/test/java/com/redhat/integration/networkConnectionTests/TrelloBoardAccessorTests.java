//package com.redhat.integration.networkConnectionTests;
//
//import com.redhat.tasksyncer.dao.accessors.trello.BoardAccessor;
//import com.redhat.tasksyncer.dao.accessors.trello.TrelloBoardAccessor;
//import com.redhat.tasksyncer.dao.accessors.trello.TrelloColumnAccessor;
//import com.redhat.tasksyncer.dao.entities.trello.TrelloBoard;
//import com.redhat.tasksyncer.dao.repositories.AbstractBoardRepository;
//import com.redhat.tasksyncer.dao.repositories.AbstractCardRepository;
//import com.redhat.tasksyncer.dao.repositories.AbstractColumnRepository;
//import org.junit.Test;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.ArrayList;
//
//public class TrelloBoardAccessorTests {
//
//    @Autowired
//    AbstractBoardRepository boardRepository;
//    @Autowired
//    AbstractCardRepository abstractCardRepository;
//    @Autowired
//    AbstractColumnRepository columnRepository;
//    @Mock
//    TrelloColumnAccessor columnAccessor;
//
//    TrelloBoard trelloBoard = new TrelloBoard();
//
//    private String trelloKey = "not_functional_key";
//    private String trelloToken = "not_functional_token";
//
//    BoardAccessor boardAccessor = new TrelloBoardAccessor(boardRepository, abstractCardRepository,
//            columnRepository, columnAccessor);
//
//    @Test
//    public void whenLogingInWithFalseCredential_thenErrorIsThrown(){
//        boardAccessor.createBoard(new ArrayList<>());
//    }
//
//}
