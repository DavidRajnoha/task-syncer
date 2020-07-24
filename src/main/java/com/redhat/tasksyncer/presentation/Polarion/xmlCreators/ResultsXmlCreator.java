package com.redhat.tasksyncer.presentation.Polarion.xmlCreators;

import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.exceptions.InvalidPolarionStateException;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class ResultsXmlCreator extends AbstractXmlCreator {


    /**
     * Takes an issue and creates a xml structure representing a polarion result, issue state corresponds to the
     * polarion result
     * @param issue to convert to a result
     * @return the xml structure representing the issue as a polarion result
     */
    public Element issueToResult(AbstractIssue issue) throws InvalidPolarionStateException {
        Element testResult = DocumentHelper.createElement("testcase");
        testResult.addAttribute("name", resultTitle(issue.getTitle()));

        if (!STATES.contains(issue.getState())){
            throw new InvalidPolarionStateException(issue.getState() + " is not valid test result");
        }

        if (!issue.getState().equals("passed")){
            String result = issue.getState();
            testResult.addElement(result)
                    .addAttribute("type", result);
        }

        testResult.addElement("properties")
                .addElement("property")
                .addAttribute("name", "polarion-testcase-id")
                .addAttribute("value", testCaseTitle(issue.getTitle()));

        return testResult;
    }


    /**
     * Composes a document used to import the issues to polarion as test results
     *
     * @param issues a list of issues to import
     * @param projectId an ID of the polarion project
     * @param approver a name of the polarion user with a permission to approve the issue, should be also the user whose
     *                 credentials are going to be used while importing the issues
     * @param testCycle a name of rhe current testcycle
     * @return a document used to import the issues to polarion as results
     */
    public Document createXml(List<AbstractIssue> issues, String projectId, String approver,  String testCycle)
            throws InvalidPolarionStateException {

        // Could be nice lambda expression but exceptions are stupid
        List<Element> elements = new ArrayList<>();
        for (AbstractIssue issue : issues) {
            Element issueToResult = issueToResult(issue);
            elements.add(issueToResult);
        }

        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("testsuites");

        Element properties = root.addElement("properties");

        properties.addElement("property")
                .addAttribute("name", "polarion-project-id")
                .addAttribute("value", projectId);
        properties.addElement("property")
                .addAttribute("name", "polarion-lookup-method")
                .addAttribute("value", LOOKUP_METHOD);
        properties.addElement("property")
                .addAttribute("name", "polarion-testrun-title")
                .addAttribute("value", testCycle + " tests");

        Element testsuite = root.addElement("testsuite")
                .addAttribute("tests", "" + elements.size());

        for (Element element : elements) {
            testsuite.add(element);
        }

        return document;
    }
}
