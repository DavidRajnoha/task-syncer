package com.redhat.tasksyncer.dao.entities;

/**
 * @author Filip Cap
 */
public class TrelloEndpoint extends Endpoint {
    public TrelloEndpoint(String boardName) {
        super(EndpointType.TRELLO, boardName);
    }
}
