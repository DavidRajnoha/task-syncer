package com.redhat.unit.entitiesTests;


import com.redhat.tasksyncer.dao.entities.AbstractCard;
import com.redhat.tasksyncer.dao.entities.AbstractColumn;
import com.redhat.tasksyncer.dao.entities.TrelloCard;
import com.redhat.tasksyncer.dao.entities.TrelloColumn;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class AbstractCardTests {

    AbstractCard oldCard;
    AbstractCard newCard;

    String title = "new title";
    String description = "new description";
    AbstractColumn column = new TrelloColumn();

    @Before
    public void setup(){
        oldCard = new TrelloCard();
        newCard = new TrelloCard();

        newCard.setTitle(title);
        newCard.setDescription(description);
        newCard.setColumn(column);
    }

    @Test
    public void updateProperties_updatesProperties(){
        oldCard.updateProperties(newCard);
        assertThat(oldCard.getTitle()).isEqualTo(newCard.getTitle());
        assertThat(oldCard.getDescription()).isEqualTo(newCard.getDescription());
        assertThat(oldCard.getColumn()).isEqualTo(newCard.getColumn());
    }
}
