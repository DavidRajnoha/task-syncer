package com.redhat.tasksyncer.dao.entities;

import com.julienvey.trello.domain.Card;

import javax.persistence.Entity;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Filip Cap
 */
@Entity
public class TrelloCard extends AbstractCard {
    public TrelloCard() {
        super();
    }

    public static class IssueToCardConverter {
        public static TrelloCard convert(AbstractIssue issue, List<AbstractColumn> columns) {
            TrelloCard card = new TrelloCard();

            card.setTitle(issue.getTitle());
            card.setDescription(issue.getDescription());

            // todo: use mapping to determine proper column
            if(issue.getState().equals(AbstractIssue.STATE_OPENED) || issue.getState().equals(AbstractIssue.STATE_REOPENED)) {
                card.setColumn(columns.stream().filter(c -> c.getName().equals("TODO")).collect(Collectors.toList()).get(0));
            } else {
                card.setColumn(columns.stream().filter(c -> c.getName().equals("DONE")).collect(Collectors.toList()).get(0));
            }

            return card;
        }
    }

    public static class TrelloCardToCardConverter {
        public static AbstractCard convert(Card input, AbstractCard abstractCard) {

            abstractCard.setRemoteCardId(input.getId());
            abstractCard.setTitle(input.getName());
            abstractCard.setDescription(input.getDesc());

            return abstractCard;
        }
    }

    public static class CardToTrelloCardConverter {
        public static Card convert(AbstractCard input) {
            Card card = new Card();

            card.setId(input.getRemoteCardId());
            card.setName(input.getTitle());
            card.setDesc(input.getDescription());

            return card;
        }
    }
}
