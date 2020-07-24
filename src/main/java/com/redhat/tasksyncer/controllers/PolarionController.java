package com.redhat.tasksyncer.controllers;


import com.redhat.tasksyncer.services.presentation.PolarionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PolarionController {
    /**
     * Controller class for the Polarion sync.
     *
     * Provides an interface for the users in http endpoints.
     */

    private PolarionService polarionService;

    @Autowired
    public PolarionController(PolarionService polarionService){
        this.polarionService = polarionService;
    }


    /**
     * Takes the up now synced issues and pushes them to polarion, creating requirements, test cases and a test run
     *
     * @param projectName name of the project that the issues are synced to
     * @param polarionProjectId id of the polarion project
     * @param url url of the polarion instance
     * @param username username polarion login credential
     * @param password polarion pasword
     * @param testCycle name of the created testrun in polarion
     * @param ignoreTitles a list of issue titles that should not be pushed to the polarion
     * @return
     */
    @RequestMapping(path = "/v1/project/{projectName}/polarion/{polarionProjectId}")
    public ResponseEntity<String> pushToPolarion(@PathVariable String projectName,
                                                 @PathVariable String polarionProjectId,
                                                 @RequestParam String url,
                                                 @RequestParam String username,
                                                 @RequestParam String password,
                                                 @RequestParam String testCycle,
                                                 @RequestParam(required = false) List<String> ignoreTitles
    ){
        try {
            polarionService.pushToPolarion(projectName, polarionProjectId, url, username, password, testCycle,
                    ignoreTitles);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Something went wrong");
        }

        return ResponseEntity.ok().body("Polarion successfully synced");
    }


    /**
     * Takes the saved issues, transforms them into the xunit testrun result and pushes these results to polarion,
     * creating a new polarion testrun.
     *
     * @param projectName name of the project that the issues are synced to
     * @param polarionProjectId id of the polarion project
     * @param url url of the polarion instance
     * @param username username polarion login credential
     * @param password polarion pasword
     * @param testCycle name of the created testrun in polarion
     * @param ignoreTitles a list of issue titles that should not be pushed to the polarion
     */
    @RequestMapping(path = "/v1/project/{projectName}/polarion/{polarionProjectId}/results")
    public ResponseEntity<String> pushOnlyResultsToPolarion(@PathVariable String projectName,
                                                 @PathVariable String polarionProjectId,
                                                 @RequestParam String url,
                                                 @RequestParam String username,
                                                 @RequestParam String password,
                                                 @RequestParam String testCycle,
                                                 @RequestParam(required = false) List<String> ignoreTitles

    ){
        polarionService.pushOnlyResultsToPolarion(projectName, polarionProjectId, url, username, password, testCycle,
                ignoreTitles);

        return ResponseEntity.ok().body("Polarion successfully synced");
    }

}
