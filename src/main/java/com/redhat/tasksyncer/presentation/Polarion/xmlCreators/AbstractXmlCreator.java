package com.redhat.tasksyncer.presentation.Polarion.xmlCreators;

import com.google.common.collect.ImmutableSet;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.exceptions.InvalidPolarionStateException;
import org.dom4j.Document;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractXmlCreator {
    protected static final Set<String> STATES = ImmutableSet.of("failure", "skipped", "error", "passed");
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

    protected List<AbstractIssue> getActiveIssues(List<AbstractIssue> issues){
        return issues.stream().filter(issue -> !issue.getDeleted()).collect(Collectors.toList());
    }

    public abstract Document createXml(List<AbstractIssue> issues, String projectId, String approver, String testCycle)
            throws InvalidPolarionStateException;


}
