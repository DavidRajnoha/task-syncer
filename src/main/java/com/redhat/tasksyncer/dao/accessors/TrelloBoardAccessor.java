package com.redhat.tasksyncer.dao.accessors;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.RestTemplateHttpClient;
import com.redhat.tasksyncer.dao.entities.AbstractBoard;
import com.redhat.tasksyncer.dao.entities.AbstractCard;
import com.redhat.tasksyncer.dao.entities.TrelloBoard;
import com.redhat.tasksyncer.dao.entities.TrelloCard;
import com.redhat.tasksyncer.dao.repositories.AbstractBoardRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractCardRepository;

import java.util.List;

/**
 * @author Filip Cap
 */
public class TrelloBoardAccessor extends BoardAccessor {
    private TrelloBoard board;
    private AbstractBoardRepository boardRepository;
    private AbstractCardRepository cardRepository;

    private Trello trelloApi;

    public TrelloBoardAccessor(TrelloBoard board, String trelloApplicationKey, String trelloAccessToken, AbstractBoardRepository boardRepository, AbstractCardRepository cardRepository) {
        this.board = board;
        this.boardRepository = boardRepository;
        this.cardRepository = cardRepository;

        trelloApi = new TrelloImpl(trelloApplicationKey, trelloAccessToken, new RestTemplateHttpClient());

    }

    @Override
    public AbstractBoard createItself() {
        if(this.board.isCreated())
            return this.board;

        Board trelloBoard = trelloApi.createBoard(board.getBoardName());
        this.board.setRemoteBoardId(trelloBoard.getId());

        this.save();
        return board;
    }

    @Override
    public AbstractCard update(AbstractCard input) {
        if(input.getId() == null) {
            List<TList> lists = trelloApi.getBoardLists(board.getRemoteBoardId());
            TList list = lists.get(0);  // todo: add card to proper column according to state

            Card trelloCard = new Card();
            trelloCard.setName(input.getTitle());
            trelloCard.setDesc(input.getDescription());

            trelloCard = list.createCard(trelloCard);
            TrelloCard card = TrelloCard.TrelloCardToCardConverter.convert(trelloCard);

            return cardRepository.save(card);
        }

        Card trelloCard = TrelloCard.CardToTrelloCardConverter.convert(input);
        trelloApi.updateCard(trelloCard);  // we're ignoring response, we assume that everything went ok since no exception thrown

        return cardRepository.save(input);
    }

    public void save() {
        this.board = boardRepository.save(board);
    }
}
