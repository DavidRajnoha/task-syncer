package com.redhat.tasksyncer.presentation.Polarion.xmlCreators;

import com.google.common.collect.ImmutableSet;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.exceptions.InvalidPolarionStateException;
import org.dom4j.Document;

import java.util.List;
import java.util.Set;

import static com.redhat.tasksyncer.services.presentation.PolarionService.DO_NOT_TEST;


/**
 * An abstract class representing a xml document creator for polarion import.
 * Provides a methods for creating a unified titles for polarion obejcts based on the issue title.
 *
 */
public abstract class AbstractXmlCreator {
    protected static final Set<String> STATES = ImmutableSet.of("failure", "skipped", "error", "passed", DO_NOT_TEST);
    protected static final String LOOKUP_METHOD = "custom";

    protected String resultTitle(String title) {
        return "Result: " + title;
    }
    protected String requirementTitle(String title) {
        return title + " REQ";
    }
    protected String testCaseTitle(String title) {
        return "Test: " + title;
    }

    /**
     * Creates a xml doc to import to polarion based on the passed arguments
     * @param issues a list of issues to import
     * @param projectId an ID of the polarion project
     * @param approver a name of the polarion user with a permission to approve the issue, should be also the user whose
     *                 credentials are going to be used while importing the issues
     * @param testCycle a name of rhe current testcycle
     * @return the processed xml document ready to be updated
     * @throws InvalidPolarionStateException when a issue is on a state not mapped to any polarion test result
     */
    public abstract Document createXml(List<AbstractIssue> issues, String projectId, String approver, String testCycle)
            throws InvalidPolarionStateException;


}
