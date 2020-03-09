package com.redhat.unit.polarionTests;

import com.redhat.tasksyncer.presentation.Polarion.xmlCreators.RequirementsXmlCreator;
import com.redhat.tasksyncer.presentation.Polarion.xmlCreators.ResultsXmlCreator;
import com.redhat.tasksyncer.presentation.Polarion.xmlCreators.TestCasesXmlCreator;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.issues.TrelloIssue;
import com.redhat.tasksyncer.exceptions.InvalidPolarionStateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.dom4j.util.NodeComparator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class xmlCreatorTests {

    private RequirementsXmlCreator requirementsXmlCreator = new RequirementsXmlCreator();
    private TestCasesXmlCreator testCasesXmlCreator = new TestCasesXmlCreator();
    private ResultsXmlCreator resultsXmlCreator = new ResultsXmlCreator();
    private List<AbstractIssue> issues = new ArrayList<>();
    private final String approver = "drajnoha";
    private String projectId = "3Scale";
    private String testCycle = "2.8_ER1";


    @Before
    public void setUp(){
        AbstractIssue issueOne = new TrelloIssue();
        AbstractIssue issueTwo = new TrelloIssue();
        AbstractIssue issueThree = new TrelloIssue();
        AbstractIssue issueFour = new TrelloIssue();

        AbstractIssue issueWrongState  = new TrelloIssue();
        AbstractIssue issueDeleted = new TrelloIssue();


        Set<String> labelsOne = new HashSet<>();
        labelsOne.add("Automation done");
        labelsOne.add("Something else");

        setFields(issueOne, "issueOne", "descOne", labelsOne, "failure", Boolean.FALSE);
        setFields(issueTwo, "issueTwo", "descTwo", null, "skipped", Boolean.FALSE);
        setFields(issueThree, "issueThree", "descThree", labelsOne, "error", Boolean.FALSE);
        setFields(issueFour, "issueFour", "descFour", null, "passed", Boolean.FALSE);

        setFields(issueWrongState, "wrongState", "wrongDesc", null, "invalidState",
                Boolean.FALSE);
        setFields(issueDeleted, "issueDeleted", "deleted", null, "passed", Boolean.TRUE);

        issues.remove(issueWrongState);
    }

    private void setFields(AbstractIssue issue, String title, String description, Set<String> labels, String state, Boolean deleted){
        issue.setTitle(title);
        issue.setDescription(description);
        issue.setLabel(labels);
        issue.setState(state);
        issue.setDeleted(deleted);

        issues.add(issue);
    }

    @Test
    public void requirementsParsedCorrectly() throws DocumentException {
        SAXReader reader = new SAXReader();

        Document createdDoc = requirementsXmlCreator.createXml(
                issues, projectId, approver, testCycle);
        createdDoc.setName("requirements.xml");

        Document desiredDoc = reader.read("requirements.xml");


        assertThat(createdDoc.asXML().replaceAll("[\\s]", ""))
                .isEqualTo(desiredDoc.asXML().replaceAll("[\\s]", ""));

    }

    @Test
    public void testCasesParsedCorrectly() throws DocumentException {
        SAXReader reader = new SAXReader();

        Document createdDoc = testCasesXmlCreator.createXml(issues,
                projectId, approver, testCycle);

        Document desiredDoc = reader.read("test-cases.xml");

        NodeComparator comparator = new NodeComparator();
        comparator.compare(createdDoc, desiredDoc);

        assertThat(createdDoc.asXML().replaceAll("[\\s]", ""))
                .isEqualTo(desiredDoc.asXML().replaceAll("[\\s]", ""));
    }


    @Test
    public void resultsParsedCorrectly() throws InvalidPolarionStateException, DocumentException {
        SAXReader reader = new SAXReader();

        Document createdDoc = resultsXmlCreator.createXml(issues, projectId,approver, testCycle);

        Document desiredDoc = reader.read("results.xml");

        NodeComparator comparator = new NodeComparator();
        comparator.compare(createdDoc, desiredDoc);

        assertThat(createdDoc.asXML().replaceAll("[\\s]", ""))
                .isEqualTo(desiredDoc.asXML().replaceAll("[\\s]", ""));

    }

}
