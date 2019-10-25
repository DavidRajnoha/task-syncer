package com.redhat.integration.networkConnectionTests;

import com.redhat.tasksyncer.dao.accessors.BoardAccessor;
import com.redhat.tasksyncer.dao.accessors.TrelloBoardAccessor;
import com.redhat.tasksyncer.dao.entities.TrelloBoard;
import com.redhat.tasksyncer.dao.repositories.AbstractBoardRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractCardRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractColumnRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TrelloBoardAccessorTests {

    @Autowired
    AbstractBoardRepository boardRepository;
    @Autowired
    AbstractCardRepository abstractCardRepository;
    @Autowired
    AbstractColumnRepository columnRepository;

    TrelloBoard trelloBoard = new TrelloBoard();

    private String trelloKey = "not_functional_key";
    private String trelloToken = "not_functional_token";

    BoardAccessor boardAccessor = new TrelloBoardAccessor(boardRepository, abstractCardRepository, columnRepository);

    @Test
    public void whenLogingInWithFalseCredential_thenErrorIsThrown(){
        boardAccessor.createBoard();
    }

}
