package com.redhat.tasksyncer.services.presentation;


import com.redhat.tasksyncer.dao.accessors.issue.AbstractIssueAccessor;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.exceptions.InvalidPolarionStateException;
import com.redhat.tasksyncer.exceptions.ProjectNotFoundException;
import com.redhat.tasksyncer.presentation.Polarion.PolarionImporter;
import com.redhat.tasksyncer.presentation.Polarion.xmlCreators.AbstractXmlCreator;
import com.redhat.tasksyncer.presentation.Polarion.xmlCreators.RequirementsXmlCreator;
import com.redhat.tasksyncer.presentation.Polarion.xmlCreators.ResultsXmlCreator;
import com.redhat.tasksyncer.presentation.Polarion.xmlCreators.TestCasesXmlCreator;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class PolarionService {

    private final RequirementsXmlCreator requirementsXmlCreator;
    private final PolarionImporter importer;
    private final AbstractIssueAccessor issueAccessor;
    private final TestCasesXmlCreator testCasesXmlCreator;
    private final ResultsXmlCreator resultsXmlCreator;
    public static final String DO_NOT_TEST = "do-not-test";



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

    /**
     * Takes the saved issues, transforms them into the xunit testrun result and pushes these results to polarion,
     * creating a new polarion testrun.
     *
     * @param projectName name of the project that the issues are synced to
     * @param polarionId id of the polarion project
     * @param url url of the polarion instance
     * @param username username polarion login credential
     * @param password polarion pasword
     * @param testCycle name of the created testrun in polarion
     * @param ignoreTitles a list of issue titles that should not be pushed to the polarion
     */
    public void pushOnlyResultsToPolarion(String projectName, String polarionId,  String url, String username,
                                          String password, String testCycle, List<String> ignoreTitles,
                                          List<String> ignoreLabels) throws ProjectNotFoundException {


        List<AbstractIssue> issues = issueAccessor.getProject(projectName);
        if (issues.size() == 0){
            throw new ProjectNotFoundException("There are no issues for the given project, have you synced the issues" +
                    "from trello?");
        }

        issues = getActiveIssues(issues);

        if (ignoreTitles != null){
            issues = filterByTitles(issues, ignoreTitles);
        }

        if (ignoreLabels != null){
            issues = filterByLabel(issues, ignoreLabels);
        }


        try{
            importToPolarion(resultsXmlCreator, issues, polarionId, testCycle, url, "xunit", username,
                    password);

        } catch (InvalidPolarionStateException e) {
            e.printStackTrace();
        }


    }

    /**
     * Takes all synced issues and pushes them to polarion.
     *
     * Polarion Requirements and Test cases are created for all new issues.
     *
     * A new Test run is created and all issues are evaluated based on their state.
     *
     *
     * @param projectName name of the project that the issues are synced to
     * @param polarionId id of the polarion project
     * @param url url of the polarion instance
     * @param username username polarion login credential
     * @param password polarion pasword
     * @param testCycle name of the created testrun in polarion
     * @param ignoreTitles a list of issue titles that should not be pushed to the polarion
     * @throws InterruptedException
     */
    public void pushToPolarion(String projectName, String polarionId,  String url, String username, String password,
                               String testCycle, List<String> ignoreTitles, List<String> ignoreLabels)
            throws InterruptedException {

        List<AbstractIssue> issues = issueAccessor.getProject(projectName);
        if (issues.size() == 0){
            // TODO: throw exception
            return;
        }

        issues = getActiveIssues(issues);

        if (ignoreTitles != null){
            issues = filterByTitles(issues, ignoreTitles);
        }

        if (ignoreLabels != null){
            issues = filterByLabel(issues, ignoreLabels);
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

    /**
     * Creates a xml using the xmlCreator and pushes this doc into Polarion using the polarion importer.
     *
     * @param xmlCreator an object used to create a xmlDoc to push to the polarion, based by the implementing class
     *                   is decided, if the requirements, test cases or results are created
     * @param issues list of the issues to push to the polarion
     * @param projectName name of the polarion project
     * @param testCycle name of the testcycle
     * @param url url of the polarion instance
     * @param type what we want to do in polarion. On of (requirement, testcase, xunit)
     * @param username username polarion login credential
     * @param password polarion pasword
     * @throws InvalidPolarionStateException
     */
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

    /**
     * Filters out the issues with particular title
     * @param issues to filter through
     * @param titles to filter out
     * @return filtered out list of the issues
     */
    private List<AbstractIssue> filterByTitles(List<AbstractIssue> issues, List<String> titles){
        return issues.stream().filter(issue -> !titles.contains(issue.getTitle()))
                .collect(Collectors.toList());
    }

    private List<AbstractIssue> filterByLabel(List<AbstractIssue> issues, List<String> labels) {
        return issues.stream().filter(issue -> {
            boolean filterOut = false;
            for (String label: labels) {
                if ( issue.getLabels().isPresent() && issue.getLabels().get().contains(label)) {
                     filterOut = true;
                }
            }
            return ! filterOut;
        }).collect(Collectors.toList());
    }

    /**
     * Filters out the inactive issues
     *
     * @param issues to filter through
     * @return filtered list of just the active issues and issues not in a DO_NOT_TEST state
     */
    private List<AbstractIssue> getActiveIssues(List<AbstractIssue> issues){
        return issues.stream().filter(issue -> !issue.getDeleted())
                .filter(issue -> ! (issue.getState() == null))
                .filter(issue -> !issue.getState().equals(DO_NOT_TEST))
                .collect(Collectors.toList());
    }

}
