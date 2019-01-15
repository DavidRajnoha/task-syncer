package com.redhat.tasksyncer.dao.entities;

import com.julienvey.trello.domain.Card;

import javax.persistence.Entity;

/**
 * @author Filip Cap
 */
@Entity
public class TrelloCard extends AbstractCard {
    public TrelloCard() {
        super();
    }

    public static class IssueToCardConverter {
        public static TrelloCard convert(AbstractIssue issue) {
            TrelloCard card = new TrelloCard();

            card.setTitle(issue.getTitle());
            card.setDescription(issue.getDescription());

            return card;
        }
    }

    public static class TrelloCardToCardConverter {
        public static TrelloCard convert(Card input) {
            TrelloCard card = new TrelloCard();

            card.setRemoteCardId(input.getId());
            card.setTitle(input.getName());
            card.setDescription(input.getDesc());

            return card;
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
