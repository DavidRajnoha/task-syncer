package com.redhat.tasksyncer.dao.entities;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.RestTemplateHttpClient;
import com.redhat.tasksyncer.dao.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;

/**
 * @author Filip Cap
 */
@Entity(name = "trelloBoard")
@PropertySource("classpath:other.properties")
public class TrelloBoard extends AbstractBoard {

    @Id
    @GeneratedValue
    private Long id;

    @Transient
    @Autowired
    private CardRepository cardRepository;

    @Transient
    @Value("${trello.appKey}")
    private String trelloApplicationKey;

    @Transient
    @Value("${trello.token}")
    private String trelloAccessToken;

    @Transient
    private Trello trelloApi;  // RestTemplateHttpClient should use builtin spring http client

    @PostConstruct
    public void init() {
        trelloApi = new TrelloImpl(trelloApplicationKey, trelloAccessToken, new RestTemplateHttpClient());
    }

    private String boardName;
    private String boardId;
    private boolean created;


    public TrelloBoard() {}

    public TrelloBoard(String boardName) {
        this.boardName = boardName;
    }

    @Override
    public Card update(Card card) {
        if(card.getId() == null) {
            List<TList> lists = trelloApi.getBoardLists(boardId);
            TList list = lists.get(0);  // todo: add card to proper column according to state

            com.julienvey.trello.domain.Card trelloCard = new com.julienvey.trello.domain.Card();
            trelloCard.setName(card.getTitle());
            trelloCard.setDesc(card.getDescription());

            trelloCard = list.createCard(trelloCard);

            return new Card(trelloCard);
        }

        com.julienvey.trello.domain.Card trelloCard = new com.julienvey.trello.domain.Card();
        trelloCard.setName(card.getTitle());
        trelloCard.setDesc(card.getDescription());
        trelloCard.setId(card.getCuid());
        trelloApi.updateCard(trelloCard);

        return card;
    }

    @Override
    public String getBoardType() {
        return this.getClass().getTypeName();
    }

    @Override
    public String getBoardName() {
        return boardName;
    }

    @Override
    public String getBoardId() {
        return boardId;
    }

    @Override
    public boolean isCreated() {
        return boardId != null && !boardId.isEmpty();
    }

    @Override
    public TrelloBoard createItself() {
        if(isCreated()) return this;

        com.julienvey.trello.domain.Board b = trelloApi.createBoard(boardName);
        boardId = b.getId();

        return this;
    }

    public void setBoardName(String name) {
        this.boardName = name;
    }
}
