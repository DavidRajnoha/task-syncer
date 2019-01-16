package com.redhat.tasksyncer.dao.accessors;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Argument;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.RestTemplateHttpClient;
import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.AbstractBoardRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractCardRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractColumnRepository;

import java.util.Collections;
import java.util.List;

/**
 * @author Filip Cap
 */
public class TrelloBoardAccessor extends BoardAccessor {
    private TrelloBoard board;
    private AbstractBoardRepository boardRepository;
    private AbstractCardRepository cardRepository;
    private AbstractColumnRepository columnRepository;

    private Trello trelloApi;

    public TrelloBoardAccessor(TrelloBoard board, String trelloApplicationKey, String trelloAccessToken, AbstractBoardRepository boardRepository, AbstractCardRepository cardRepository, AbstractColumnRepository columnRepository) {
        this.board = board;
        this.boardRepository = boardRepository;
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;

        trelloApi = new TrelloImpl(trelloApplicationKey, trelloAccessToken, new RestTemplateHttpClient());

    }

    @Override
    public AbstractBoard createItself() {
        if(this.board.isCreated())
            return this.board;

        Board trelloBoard = trelloApi.createBoard(
                board.getBoardName(),
                new Argument("defaultLabels", "false"),
                new Argument("defaultLists", "false")
        );

        this.board.setRemoteBoardId(trelloBoard.getId());

        this.save();

        this.createColumn("DONE");
        this.createColumn("TODO");

        return board;
    }

    @Override
    public AbstractCard update(AbstractCard input) {
        if(input.getId() == null) {
            TList list = trelloApi.getList(input.getColumn().getRemoteColumnId());

            Card trelloCard = TrelloCard.CardToTrelloCardConverter.convert(input);

            trelloCard = list.createCard(trelloCard);

            // todo: think about using convert for converting just parameters which are contained in trelloCard and then use Card.update(convertedCard)
            AbstractCard card = TrelloCard.TrelloCardToCardConverter.convert(trelloCard, input);

            return cardRepository.save(card);
        }

        // todo: handle column change
        Card trelloCard = TrelloCard.CardToTrelloCardConverter.convert(input);
        trelloApi.updateCard(trelloCard);  // we're ignoring response, we assume that everything went ok since no exception thrown

        return cardRepository.save(input);
    }

    @Override
    public List<AbstractColumn> getColumns() {
        return Collections.unmodifiableList(columnRepository.findAllByBoardId(board.getId()));
    }

    @Override
    public void save() {
        this.board = boardRepository.save(board);
    }

    @Override
    public ColumnAccessor createColumn(String name) {
        TrelloColumn newColumn = new TrelloColumn();
        newColumn.setName(name);
        newColumn.setBoard(board);

        TrelloColumnAccessor newColumnAccessor = new TrelloColumnAccessor(newColumn, columnRepository, trelloApi);
        newColumnAccessor = newColumnAccessor.createItself();

        // this makes sure that this board is not outdated
        this.board = (TrelloBoard) boardRepository.findById(this.board.getId()).get();  // todo generify

        return newColumnAccessor;
    }
}
