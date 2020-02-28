package com.redhat.unit.entitiesTests;


import com.redhat.tasksyncer.dao.entities.trello.AbstractCard;
import com.redhat.tasksyncer.dao.entities.trello.AbstractColumn;
import com.redhat.tasksyncer.dao.entities.trello.TrelloCard;
import com.redhat.tasksyncer.dao.entities.trello.TrelloColumn;
import org.junit.Before;


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

        newCard.setColumn(column);
    }

}
