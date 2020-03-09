package com.redhat.unit.ServiceTests.PolarionTests;

import com.redhat.tasksyncer.presentation.Polarion.*;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.issues.TrelloIssue;
import com.redhat.tasksyncer.exceptions.InvalidPolarionStateException;
import com.redhat.tasksyncer.presentation.Polarion.xmlCreators.AbstractXmlCreator;
import com.redhat.tasksyncer.presentation.Polarion.xmlCreators.RequirementsXmlCreator;
import com.redhat.tasksyncer.presentation.Polarion.xmlCreators.ResultsXmlCreator;
import com.redhat.tasksyncer.presentation.Polarion.xmlCreators.TestCasesXmlCreator;
import com.redhat.tasksyncer.services.presentation.PolarionService;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;


@RunWith(MockitoJUnitRunner.class)
public class ServiceTests {

    @Mock
    private ResultsXmlCreator resultsXmlCreator;

    @Mock
    private TestCasesXmlCreator testCasesXmlCreator;

    @Mock
    private RequirementsXmlCreator requirementsXmlCreator;

    @Mock
    private PolarionImporter importer;

    @InjectMocks
    private PolarionService polarionService;

    private Document document;
    private String docName = "docName";

    private AbstractIssue issue = new TrelloIssue();
    private List<AbstractIssue> issues = new ArrayList<>();
    private String projectName = "3Scale";
    private String url = "http://polarion-devel.engineering.redhat.com/polarion/import";
    private String testcycle = "2.8";
    private String password = "12345";
    private String username = "admin";


    @Before
    public void setup() throws InvalidPolarionStateException {
        document = DocumentHelper.createDocument();
        document.setName(docName);

        Mockito.when(resultsXmlCreator.createXml(any(), any(), any(), any())).thenReturn(document);
        Mockito.when(testCasesXmlCreator.createXml(any(), any(), any(), any())).thenReturn(document);
        Mockito.when(requirementsXmlCreator.createXml(any(), any(), any(), any())).thenReturn(document);

        issue.setTitle("Issue title");
        issues.add(issue);
    }


    @Test
    public void createRequirementsImportsRequirementsUsingCorrectUrl() throws InvalidPolarionStateException {

        verifyImporter("requirement", requirementsXmlCreator);
    }

    @Test
    public void createTestCasesImportsTestCasesUsingCorrectUrl() throws InvalidPolarionStateException {

        verifyImporter("testcase", testCasesXmlCreator);
    }

    @Test
    public void createResultsImportsResultsUsingCorrectUrl() throws InvalidPolarionStateException {

        verifyImporter("xunit", resultsXmlCreator);
    }

    private void verifyImporter(String type, AbstractXmlCreator creator) throws InvalidPolarionStateException {

        polarionService.importToPolarion(creator, issues, projectName, testcycle,  url, type, username, password);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(importer, Mockito.times(1))
                .importToPolarion(any(), captor.capture(), captor.capture(), any(), any());

        String foundUrl = captor.getAllValues().get(0);
        String foundType = captor.getAllValues().get(1);

        assertThat(foundType).isEqualTo(type);
        assertThat(foundUrl).isEqualTo(url + "/" + type);
    }
}
