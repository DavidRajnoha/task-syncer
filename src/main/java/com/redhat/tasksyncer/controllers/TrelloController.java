package com.redhat.tasksyncer.controllers;

import com.redhat.tasksyncer.services.issues.IssueService;
import com.redhat.tasksyncer.services.presentation.TrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrelloController {

    private IssueService issueService;
    private TrelloService trelloService;

    @Autowired
    public TrelloController(TrelloService trelloService, IssueService issueService){
        this.trelloService = trelloService;
        this.issueService = issueService;
    }


    @RequestMapping(path = "/v1/project/{projectName}/trello/create")
    public ResponseEntity<String> createBoard(@PathVariable String projectName,
                                              @RequestParam String trelloApplicationKey,
                                              @RequestParam String trelloAccessToken) throws Exception {
        trelloService.createBoard(projectName, trelloApplicationKey, trelloAccessToken);

        return null;
    }

    @RequestMapping(path = "/v1/project/{projectName}/trello/update")
    public ResponseEntity<String> updateBoard(@PathVariable String projectName,
                                              @RequestParam String trelloApplicationKey,
                                              @RequestParam String trelloAccessToken) throws Exception {
        trelloService.updateBoard(projectName, trelloApplicationKey, trelloAccessToken);

        return null;
    }


}
