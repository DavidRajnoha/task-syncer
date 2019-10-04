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
            card.setDescription(issue.getIssueType() + "\n" + issue.getDescription());

            card.setAssignee(issue.getAssignee());
            card.setCreatedAt(issue.getCreatedAt());
            card.setClosedAt(issue.getClosedAt());
            card.setClosedBy(issue.getClosedBy());
      //      card.setComments(issue.getComments());
            //      card.setLabels(issue.getLabels());
            card.setDueDate(issue.getDueDate());

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
            abstractCard.setDueDate(input.getDue());

            return abstractCard;
        }
    }

    public static class CardToTrelloCardConverter {
        public static Card convert(AbstractCard input) {
            Card card = new Card();

            card.setId(input.getRemoteCardId());
            card.setName(input.getTitle());
            card.setDesc(input.getDescription());
            card.setDue(input.getDueDate());

//            input.getComments().forEach(comment -> {
//                card.addComment(comment.getBody());
//            });

            return card;
        }
    }
}
