package com.redhat.unit.ServiceTests.PolarionTests;

import com.redhat.tasksyncer.dao.accessors.issue.AbstractIssueAccessor;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.issues.TrelloIssue;
import com.redhat.tasksyncer.exceptions.InvalidPolarionStateException;
import com.redhat.tasksyncer.exceptions.ProjectNotFoundException;
import com.redhat.tasksyncer.presentation.Polarion.PolarionImporter;
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
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

/**
 * Tests testing the correct behaviour of the Polarion Service class
 */
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

    @Mock
    private AbstractIssueAccessor issueAccessor;

    @InjectMocks
    private PolarionService polarionService;

    private Document document;
    private String docName = "docName";

    private AbstractIssue issue = new TrelloIssue();
    private AbstractIssue deletedIssue = new TrelloIssue();
    private AbstractIssue doNotTestIssue = new TrelloIssue();
    private AbstractIssue ignoreThisIssue = new TrelloIssue();
    private AbstractIssue ignoreLabelsIssue = new TrelloIssue();

    private Set<String> ignoreLabelsIssueLabels = new HashSet<>();
    private String ignoreLabel = "ignoreLabel";

    private String ignoreTitle = "ignore";

    private List<String> ignoreTitles = new ArrayList<>();

    private List<AbstractIssue> issues = new ArrayList<>();
    private String projectName = "3Scale";
    private String url = "http://polarion-devel.engineering.redhat.com/polarion/import";
    private String testcycle = "2.8";
    private String password = "12345";
    private String username = "admin";
    private String polarionId = "APIM";

    @Captor
    private ArgumentCaptor<List<AbstractIssue>> argumentCaptor;


    /**
     * Initial setup
     *
     * Initializes the mockXmlCreators to return empty document (we are not testing that)
     * Sets the mock issueAccessor to return the predefined set of issues
     *
     * takes the previously defined issues and populates them with random or already defined values
     *
     * @throws InvalidPolarionStateException
     */
    @Before
    public void setup() throws InvalidPolarionStateException {
        document = DocumentHelper.createDocument();
        document.setName(docName);

        Mockito.when(resultsXmlCreator.createXml(argumentCaptor.capture(), any(), any(), any())).thenReturn(document);
        Mockito.when(testCasesXmlCreator.createXml(argumentCaptor.capture(), any(), any(), any())).thenReturn(document);
        Mockito.when(requirementsXmlCreator.createXml(argumentCaptor.capture(), any(), any(), any())).thenReturn(document);

        Mockito.when(issueAccessor.getProject(projectName)).thenReturn(issues);

        issue.setTitle("Issue title");
        issue.setState("State");
        issue.setDeleted(false);

        deletedIssue.setTitle("deleted");
        deletedIssue.setDeleted(true);
        deletedIssue.setState("State");

        doNotTestIssue.setTitle("doNotTestInER1");
        doNotTestIssue.setDeleted(false);
        doNotTestIssue.setState(PolarionService.DO_NOT_TEST);

        ignoreThisIssue.setTitle(ignoreTitle);
        ignoreThisIssue.setState("valid");
        ignoreThisIssue.setDeleted(false);

        ignoreLabelsIssueLabels.add(ignoreLabel);

        ignoreLabelsIssue.setTitle("ignoreList");
        ignoreLabelsIssue.setState("valid");
        ignoreLabelsIssue.setDeleted(false);
        ignoreLabelsIssue.setLabel(ignoreLabelsIssueLabels);

        issues.add(issue);
        issues.add(deletedIssue);
        issues.add(doNotTestIssue);
        issues.add(ignoreThisIssue);
        issues.add(ignoreLabelsIssue);

        ignoreTitles.add(ignoreTitle);
    }


    /**
     * Asserts that the importer is called the expected number of times and with the correct arguments
     */
    @Test
    public void createRequirementsImportsRequirementsUsingCorrectUrl() throws InvalidPolarionStateException {


        verifyImporter("requirement", requirementsXmlCreator);
    }

    /**
     * Asserts that the importer is called the expected number of times and with the correct arguments
     */
    @Test
    public void createTestCasesImportsTestCasesUsingCorrectUrl() throws InvalidPolarionStateException {

        verifyImporter("testcase", testCasesXmlCreator);
    }

    /**
     * Asserts that the importer is called the expected number of times and with the correct arguments
     */
    @Test
    public void createResultsImportsResultsUsingCorrectUrl() throws InvalidPolarionStateException {

        verifyImporter("xunit", resultsXmlCreator);
    }

    /**
     * When pushing to polarion, the issues that are closed should not be pushed to polarion
     */
    @Test
    public void verifyCorrectFilteringOfClosedIssues() throws InterruptedException, InvalidPolarionStateException, ProjectNotFoundException {

        polarionService.pushOnlyResultsToPolarion(projectName, polarionId, url, username, password, testcycle,
                null, null);

        Mockito.verify(resultsXmlCreator, Mockito.times(1))
                .createXml(argumentCaptor.capture(), any(), any(), any());

        List<AbstractIssue> capturedIssues = argumentCaptor.getValue();

        assertThat(capturedIssues).doesNotContain(deletedIssue);
        assertThat(capturedIssues).doesNotContain(doNotTestIssue);

        assertThat(capturedIssues).contains(ignoreThisIssue);
        assertThat(capturedIssues).contains(ignoreLabelsIssue);
        assertThat(capturedIssues).contains(issue);



    }

    /**
     * When pushing to polarion, the issues are ignored based on the title
     */
    @Test
    public void verifyFilteringOfIssuesToIgnore() throws InvalidPolarionStateException, ProjectNotFoundException {
        polarionService.pushOnlyResultsToPolarion(projectName, polarionId, url, username, password, testcycle,
                ignoreTitles, null);

        Mockito.verify(resultsXmlCreator, Mockito.times(1))
                .createXml(argumentCaptor.capture(), any(), any(), any());

        List<AbstractIssue> capturedIssues = argumentCaptor.getValue();

        assertThat(capturedIssues).doesNotContain(ignoreThisIssue);
        assertThat(capturedIssues).contains(ignoreLabelsIssue);
        assertThat(capturedIssues).contains(issue);
    }


    /**
     * When pushing to polarion and no issues should be ignored by the title nor by the label, everything is working fine
     */
    @Test
    public void verifyNoFilteringWhenNoIgnoreTitles() throws InvalidPolarionStateException, ProjectNotFoundException {
        polarionService.pushOnlyResultsToPolarion(projectName, polarionId, url, username, password, testcycle,
                null, null);

        Mockito.verify(resultsXmlCreator, Mockito.times(1))
                .createXml(argumentCaptor.capture(), any(), any(), any());

        List<AbstractIssue> capturedIssues = argumentCaptor.getValue();

        assertThat(capturedIssues).contains(ignoreThisIssue);
        assertThat(capturedIssues).contains(ignoreLabelsIssue);
        assertThat(capturedIssues).contains(issue);
    }

    /**
     * When pushing to polarion, the issues with the label inputted as the ignore labels param are ignored
     */
    @Test
    public void verifyFilteringOutByLabel() throws ProjectNotFoundException, InvalidPolarionStateException {
        polarionService.pushOnlyResultsToPolarion(projectName, polarionId, url, username, password, testcycle,
                null, new ArrayList<>(ignoreLabelsIssueLabels));

        Mockito.verify(resultsXmlCreator, Mockito.times(1))
                .createXml(argumentCaptor.capture(), any(), any(), any());

        List<AbstractIssue> capturedIssues = argumentCaptor.getValue();

        assertThat(capturedIssues).contains(ignoreThisIssue);
        assertThat(capturedIssues).contains(issue);
        assertThat(capturedIssues).doesNotContain(ignoreLabelsIssue);

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
