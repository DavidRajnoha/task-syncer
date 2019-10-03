package com.redhat.accessorTests;

import com.julienvey.trello.Trello;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.RestTemplateHttpClient;
import com.redhat.tasksyncer.dao.accessors.TrelloBoardAccessor;
import com.redhat.tasksyncer.dao.entities.TrelloBoard;
import com.redhat.tasksyncer.dao.repositories.AbstractBoardRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractCardRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractColumnRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


import java.io.IOException;

public class TrelloBoardAccessorTests {

    @Autowired
    private AbstractBoardRepository abstractBoardRepository;

    @Autowired
    private AbstractColumnRepository abstractColumnRepository;

    @Autowired
    private AbstractCardRepository abstractCardRepository;

    private String appKey = "9942cba7d6c0f1148edb1b711a79b79c";
    private String accToken = "3d9b6ad63c66b9c509773b9a34fa4b275cc167ea5ff9262ba744c6ebd42bffb5";

    @Test
    public void deleteBoard() throws IOException {
        TrelloBoard board = new TrelloBoard();
        board.setRemoteBoardId("5d6e248bdb7d675254c889b2");
        TrelloBoardAccessor trelloBoardAccessor = new TrelloBoardAccessor(board, appKey, accToken, abstractBoardRepository, abstractCardRepository, abstractColumnRepository);
        String response = trelloBoardAccessor.deleteBoard(appKey, accToken);


        assertThat(response).isNotEmpty();
    }
}
