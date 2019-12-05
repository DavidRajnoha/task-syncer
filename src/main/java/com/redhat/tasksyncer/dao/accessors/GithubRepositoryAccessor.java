package com.redhat.tasksyncer.dao.accessors;

import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.InvalidMappingException;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
* @author David Rajnoha
* */
@Component
public class GithubRepositoryAccessor extends RepositoryAccessor {



    private GitHub gitHub;
    private GHRepository ghRepository;

    @Autowired
    public GithubRepositoryAccessor(AbstractRepositoryRepository repositoryRepository, AbstractIssueRepository issueRepository) {
        this.repositoryRepository = repositoryRepository;
    }


    @Override
    public void connectToRepository() throws IOException {
        this.gitHub = getConnection(repository.getFirstLoginCredential(), repository.getSecondLoginCredential());
        //Creating string to find the particular repository
        String namsespaceAndRepository = repository.getRepositoryNamespace() + "/" + repository.getRepositoryName();
        ghRepository = gitHub.getRepository(namsespaceAndRepository);
    }

    /**
     * Authenticates with the github and returns the connected object
     * */
    private GitHub getConnection(String firstLoginCredential, String secondLoginCredential) throws IOException {
        return GitHub.connectUsingPassword(repository.getFirstLoginCredential(), repository.getSecondLoginCredential());
    }

    @Override
    public List<AbstractIssue> downloadAllIssues() throws IOException {
        Stream<GHIssue> issuesStream = ghRepository.getIssues(GHIssueState.ALL)
                .stream();

        // converts every issue using the GithubIssue convertor
        return issuesStream
                .map(issue -> GithubIssue.ObjectToGithubIssueConverter.convert(issue, repository.getColumnMapping()))
                .collect(Collectors.toList());
    }

    public void createWebhook(String webHookUrlString) throws IOException {
        //using library to set Webhook to the webhookURL from argument (should be URL pointing to this app's endpoint
        Set<GHEvent> events = new HashSet<>();
        //The webhook is triggered by all issues events - not comments!!
        events.add(GHEvent.ISSUES);

        //TODO: DO based on reflection
        webHookUrlString = webHookUrlString.replace("<service>", "github");

        URL webHookUrl = new URL(webHookUrlString);

        // ghRepository.createWebHook(webHookUrl, events);

        // it is necessary to use the .createHook instead of .createWebHook method, the createWebHook creates the webhook
        // of the other content_type than JSON
        // .createHook enables to pass config further, and in config is possible to set the content_type to JSON
        Map<String, String> config = new HashMap<>();
        config.put("url", webHookUrl.toExternalForm());
        config.put("content_type", "json");
        ghRepository.createHook("web", config, events, true);
    }

    @Override
    public AbstractRepository createRepositoryOfType() {
        return new GithubRepository();
    }

    @Override
    public Map<String, String> isMappingValid(Map<String, String> mapping) throws InvalidMappingException {

        Map<String, String> upperCaseMap = new LinkedHashMap<>();

        for (String key : mapping.keySet()){
            String value  = mapping.get(key);

            key = key.toUpperCase();

            if (!key.equals(GHIssueState.OPEN.name()) &&
                    !key.equals(GHIssueState.CLOSED.name())){
                throw new InvalidMappingException("State " + key + " is not valid GitHub Issue state");
            }

            upperCaseMap.put(key, value);
        }

        return upperCaseMap;
    }

}
