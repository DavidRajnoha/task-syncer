package com.redhat.tasksyncer.dao.entities;


import javax.persistence.Inheritance;

/**
 * @author Filip Cap
 */
@Inheritance
public interface Board {
    String getBoardType();
    String getBoardName();
    String getBoardId();
    boolean isCreated();
    TrelloBoard createItself();

    /**
     * Creates or updates card on server
     * @param card
     * @return new Card according to remote state
     */
    Card update(Card card);

}
