package com.redhat.tasksyncer.dao.accessors;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.RestTemplateHttpClient;
import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.TrelloIssue;
import com.redhat.tasksyncer.dao.entities.TrelloRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.InvalidMappingException;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author David Rajnoha
 * */

@Component
public class TrelloRepositoryAccessor extends RepositoryAccessor{
    private Trello trelloApi;

    @Autowired
    public TrelloRepositoryAccessor(AbstractRepositoryRepository repositoryRepository){
        this.repositoryRepository = repositoryRepository;
    }

    @Override
    public void connectToRepository() throws IOException {
        trelloApi = new TrelloImpl(repository.getFirstLoginCredential(), repository.getSecondLoginCredential(),
                new RestTemplateHttpClient());
    }

    @Override
    public List<AbstractIssue> downloadAllIssues() throws IOException, GitLabApiException {
        Stream<Card> trelloCards = trelloApi.getBoardCards((repository.getRepositoryName())).stream();

        return trelloCards.map(card -> TrelloIssue.ObjectToTrelloIssueConvertor
                .convert(card, repository.getColumnMapping())).collect(Collectors.toList());
    }

    @Override
    public void createWebhook(String webhook) throws IOException {
        URL url = new URL("https://api.trello.com/1/webhooks/");

        HttpURLConnection connection  = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        //TODO: Do based on reflection
        webhook = webhook.replace("<service>", "trello");


        Map<String, String> parameters = new HashMap<>();
       parameters.put("token", repository.getSecondLoginCredential());
       parameters.put("key", repository.getFirstLoginCredential());
       parameters.put("idModel", repository.getRepositoryName());
       parameters.put("description", "TaskSyncer repository: " + repository.getRepositoryName());
       parameters.put("callbackURL", webhook);


        try(DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            // TODO: Move ParameterStringBuilder out of the TrelloBoardAccessor
            outputStream.writeBytes(TrelloBoardAccessor.ParameterStringBuilder.getParamsString(parameters));
            outputStream.flush();
        }

        StringBuilder content;

        try (BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
            String line;
            content = new StringBuilder();
            while ((line = input.readLine()) != null) {
                // Append each line of the response and separate them
                content.append(line);
                content.append(System.lineSeparator());
            }
        }

        System.out.println(content.toString());

        System.out.println(connection.getResponseMessage());
    }

    @Override
    public AbstractRepository createRepositoryOfType() {
        return new TrelloRepository();
    }

    @Override
    public Map<String, String> isMappingValid(Map<String, String> mapping) throws InvalidMappingException {

//        TODO: make this work
//        List<String> tListIds = trelloApi.getBoard(repository.getProject().getBoard().getRemoteBoardId()).getLists()
//                .stream().map(TList::getId).collect(Collectors.toList());
//
//        for (String columnId : mapping.keySet()){
//            if (!tListIds.contains(columnId)){
//                throw new InvalidMappingException("Mapped list doesn't exist in your trello board");
//            }
//        }

        return mapping;
    }
}
