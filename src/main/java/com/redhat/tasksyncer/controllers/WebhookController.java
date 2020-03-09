package com.redhat.tasksyncer.controllers;

import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.exceptions.InvalidWebhookCallbackException;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;
import com.redhat.tasksyncer.exceptions.TrelloCalllbackNotAboutCardException;
import com.redhat.tasksyncer.services.presentation.TrelloService;
import com.redhat.tasksyncer.services.webhooks.WebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class WebhookController {

    private WebhookService webhookService;
    private TrelloService trelloService;

    public WebhookController(WebhookService webhookService, TrelloService trelloService){
        this.webhookService = webhookService;
        this.trelloService = trelloService;
    }

    /**
     *  Endpoint for processing webhooks
     * @param serviceName - name of the service you are trying to connect
     *                    Available values: "gitlab", "github, "jira"
     * @param projectName - name of the project in this app you want to have your webhooks send
     * */
    @RequestMapping(path = "/v1/service/{serviceName}/project/{projectName}/hook",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            method = {RequestMethod.POST}
    )
    public ResponseEntity<String> hookEndpoint(@PathVariable String serviceName,
                                               @PathVariable String projectName,
                                               HttpServletRequest request
    )  {
        try {
            webhookService.processHook(projectName, request, serviceName);
        } catch (RepositoryTypeNotSupportedException | InvalidWebhookCallbackException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (TrelloCalllbackNotAboutCardException ignored) {
        }

        return ResponseEntity.status(HttpStatus.OK).body("Webhook processed");
    }

    /**
     * When creating a trello endpoint via webhook, trello sends a HEAD request and is awaiting 200 response that would not come
     * from POST Endpoint
     * */
    @RequestMapping(path = {"/v1/service/{serviceName}/project/{projectName}/hook",
                            "/v1/service/{serviceName}/project/{projectName}/hook/trello"},
            method = {RequestMethod.GET}
    ) public ResponseEntity<String> yesTrelloThisEndpointWorks(){
        return ResponseEntity.status(HttpStatus.OK).body("");
    }


    /**
     *  Endpoint for processing webhooks
     * @param serviceName - name of the service you are trying to connect
     *                    Available values: "gitlab", "github, "jira"
     * @param projectName - name of the project in this app you want to have your webhooks send
     * */
    @RequestMapping(path = "/v1/service/{serviceName}/project/{projectName}/hook/trello",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            method = {RequestMethod.POST}
    )
    public ResponseEntity<String> hookEndpointTrelloPush(@PathVariable String serviceName,
                                               @PathVariable String projectName,
                                               HttpServletRequest request
    ) throws TrelloCalllbackNotAboutCardException, InvalidWebhookCallbackException, RepositoryTypeNotSupportedException {

        AbstractIssue issue = webhookService.processHook(projectName, request, serviceName);

        trelloService.updateCard(issue);

        return ResponseEntity.status(HttpStatus.OK).body("Webhook processed");
    }


}
