package com.redhat.tasksyncer.dao.accessors.trello;

import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.redhat.tasksyncer.dao.entities.trello.AbstractCard;
import com.redhat.tasksyncer.dao.entities.trello.AbstractColumn;
import com.redhat.tasksyncer.dao.entities.trello.TrelloCard;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class TrelloCardAccessor extends AbstractTrelloAccessor {


    public AbstractIssue createCard(AbstractIssue issue, List<AbstractColumn> columns){

        AbstractColumn column = findColumn(columns, issue.getState());

        TList tList = trelloApi.getList(column.getRemoteColumnId());

        AbstractCard abstractCard = new TrelloCard();
        abstractCard.setColumn(column);
        issue.setCard(abstractCard);

        Card trelloCard = convertIssue(issue);

        trelloCard = tList.createCard(trelloCard);

        updateIssueCard(trelloCard, issue.getCard());

        return issue;
    }

    public AbstractIssue updateCard(AbstractIssue issue, List<AbstractColumn> columns) {

        issue.getCard().setColumn(findColumn(columns, issue.getState()));

        Card trelloCard = convertIssue(issue);

        trelloCard = trelloApi.updateCard(trelloCard);  // we're ignoring response, we assume that everything went ok since no exception thrown

        updateIssueCard(trelloCard, issue.getCard());

        return issue;
    }

    private void updateIssueCard(Card input, AbstractCard abstractCard) {
            abstractCard.setRemoteCardId(input.getId());
    }



    private Card convertIssue(AbstractIssue issue) {
            Card card = new Card();

            card.setId(issue.getCard().getRemoteCardId());
            card.setName(issue.getTitle());
            card.setDesc(issue.getDescription());
            card.setDue(issue.getDueDate());
            card.setIdList(issue.getCard().getColumn().getRemoteColumnId());

            return card;
    }

    private AbstractColumn findColumn(List<AbstractColumn> columns, String columnName){
        AbstractColumn column;
        try {
            column = columns.stream().filter(c -> c.getName().equals(columnName))
                    .collect(Collectors.toList()).get(0);
        } catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            column = columns.get(0);
        }
        return column;
    }
}
