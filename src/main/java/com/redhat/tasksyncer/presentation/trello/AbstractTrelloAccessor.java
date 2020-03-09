package com.redhat.tasksyncer.presentation.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.RestTemplateHttpClient;

public abstract class AbstractTrelloAccessor {
    Trello trelloApi;

    public void connectToTrello(String trelloApplicationKey, String trelloAccessToken){
        trelloApi = new TrelloImpl(trelloApplicationKey, trelloAccessToken, new RestTemplateHttpClient());
    }
}
