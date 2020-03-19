package com.redhat.tasksyncer.services.presentation;


import com.redhat.tasksyncer.presentation.Polarion.*;
import com.redhat.tasksyncer.dao.accessors.issue.AbstractIssueAccessor;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.exceptions.InvalidPolarionStateException;
import com.redhat.tasksyncer.presentation.Polarion.xmlCreators.AbstractXmlCreator;
import com.redhat.tasksyncer.presentation.Polarion.xmlCreators.RequirementsXmlCreator;
import com.redhat.tasksyncer.presentation.Polarion.xmlCreators.ResultsXmlCreator;
import com.redhat.tasksyncer.presentation.Polarion.xmlCreators.TestCasesXmlCreator;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PolarionService {

    private final RequirementsXmlCreator requirementsXmlCreator;
    private final PolarionImporter importer;
    private final AbstractIssueAccessor issueAccessor;
    private final TestCasesXmlCreator testCasesXmlCreator;
    private final ResultsXmlCreator resultsXmlCreator;


    @Autowired
    public PolarionService(RequirementsXmlCreator requirementsXmlCreator, TestCasesXmlCreator testCasesXmlCreator,
                           ResultsXmlCreator resultsXmlCreator, PolarionImporter importer,
                           AbstractIssueAccessor issueAccessor){
        this.requirementsXmlCreator = requirementsXmlCreator;
        this.testCasesXmlCreator = testCasesXmlCreator;
        this.resultsXmlCreator = resultsXmlCreator;
        this.importer = importer;
        this.issueAccessor = issueAccessor;
    }

    public void pushOnlyResultsToPolarion(String projectName, String polarionId,  String url, String username, String password, String testCycle){

        List<AbstractIssue> issues = issueAccessor.getProject(projectName);
        if (issues.size() == 0){
            // TODO: probably also throw exception
            return;
        }

        try{
            importToPolarion(resultsXmlCreator, issues, polarionId, testCycle, url, "xunit", username,
                    password);

        } catch (InvalidPolarionStateException e) {
            e.printStackTrace();
        }


    }

    public void pushToPolarion(String projectName, String polarionId,  String url, String username, String password, String testCycle)
            throws InterruptedException {

        List<AbstractIssue> issues = issueAccessor.getProject(projectName);
        if (issues.size() == 0){
            // TODO: probably also throw exception
            return;
        }


        try{
            importToPolarion(requirementsXmlCreator, issues, polarionId, testCycle, url, "requirement", username,
                    password);

            TimeUnit.SECONDS.sleep(30);

            importToPolarion(testCasesXmlCreator, issues, polarionId, testCycle, url, "testcase", username,
                    password);

            TimeUnit.SECONDS.sleep(60);


            importToPolarion(resultsXmlCreator, issues, polarionId, testCycle, url, "xunit", username,
                    password);

        } catch (InvalidPolarionStateException e) {
            e.printStackTrace();
        }
    }


    public void importToPolarion(AbstractXmlCreator xmlCreator, List<AbstractIssue> issues, String projectName,
                                 String testCycle, String url, String type, String username, String password)
            throws InvalidPolarionStateException {

        Document xml = xmlCreator.createXml(issues, projectName, username, testCycle);

        if(!url.endsWith("/")){
            url += "/";
        }
        url += type;

        String response = importer.importToPolarion(xml, url, type, username, password);
        System.out.println(response);
    }

}
