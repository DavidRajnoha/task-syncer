package com.redhat.tasksyncer.controllers;


import com.redhat.tasksyncer.services.presentation.PolarionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PolarionController {

    private PolarionService polarionService;

    @Autowired
    public PolarionController(PolarionService polarionService){
        this.polarionService = polarionService;
    }


    @RequestMapping(path = "/v1/project/{projectName}/polarion/{polarionProjectId}")
    public ResponseEntity<String> pushToPolarion(@PathVariable String projectName,
                                                 @PathVariable String polarionProjectId,
                                                 @RequestParam String url,
                                                 @RequestParam String username,
                                                 @RequestParam String password,
                                                 @RequestParam String testcycle
    ){
        try {
            polarionService.pushToPolarion(projectName, polarionProjectId, url, username, password, testcycle);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Something went wrong");
        }

        return ResponseEntity.ok().body("Polarion successfully synced");
    }

    @RequestMapping(path = "/v1/project/{projectName}/polarion/{polarionProjectId}/results")
    public ResponseEntity<String> pushOnlyResultsToPolarion(@PathVariable String projectName,
                                                 @PathVariable String polarionProjectId,
                                                 @RequestParam String url,
                                                 @RequestParam String username,
                                                 @RequestParam String password,
                                                 @RequestParam String testcycle
    ){
        polarionService.pushOnlyResultsToPolarion(projectName, polarionProjectId, url, username, password, testcycle);

        return ResponseEntity.ok().body("Polarion successfully synced");
    }

}
