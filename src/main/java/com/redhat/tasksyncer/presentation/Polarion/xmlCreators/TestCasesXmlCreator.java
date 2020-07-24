package com.redhat.tasksyncer.presentation.Polarion.xmlCreators;

import com.google.common.collect.ImmutableMap;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class TestCasesXmlCreator extends AbstractXmlCreator {


    /**
     * Takes an issue and creates a xml structure representing a polarion result, issue state corresponds to the
     * polarion test case
     * @param issue to convert to a result
     * @return the xml structure representing the issue as a polarion test case
     */
    public Element issueToTestCase(AbstractIssue issue, String approver){
        Element testCase = DocumentHelper.createElement("testcase");
        testCase.addAttribute("id", testCaseTitle(issue.getTitle()))
                .addAttribute("approver-ids", approver + ":approved")
                .addAttribute("status-id", "approved");

        testCase.addElement("title")
                .addText(testCaseTitle(issue.getTitle()));


        testCase.addElement("description")
                .addText(!issue.getDescription().equals("") ? issue.getDescription() : "Desc");


        String caseAutomation;
        if (issue.getLabels().isPresent()){
            caseAutomation = issue.getLabels().get().contains("Automation done") ? "automated" : "notautomated";
        } else {
            caseAutomation = "notautomated";
        }

        testCase.add(getCustomFields(caseAutomation, "high", "acceptance", "negative",
                "functional", "All automation scripts are available at https://github.com/3scale/3scale-amp-tests/"));

        testCase.addElement("linked-work-items")
                .addElement("linked-work-item")
                .addAttribute("lookup-method", "name")
                .addAttribute("workitem-id", requirementTitle(issue.getTitle()))
                .addAttribute("role-id", "verifies");

        return testCase;
    }


    /**
     * Composes a document used to import the issues to polarion as test cases
     *
     * @param issues a list of issues to import
     * @param projectId an ID of the polarion project
     * @param approver a name of the polarion user with a permission to approve the issue, should be also the user whose
     *                 credentials are going to be used while importing the issues
     * @param testCycle a name of rhe current testcycle
     * @return a document used to import the issues to polarion as test cases
     */
    public Document createXml(List<AbstractIssue> issues, String projectId, String approver, String testCycle){

        List<Element> elements = issues.stream()
                .map((AbstractIssue issue1) -> issueToTestCase(issue1, approver))
                .collect(Collectors.toList());

        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("testcases")
                .addAttribute("project-id", projectId);

        Element properties = root.addElement("properties");
        properties.addElement("property")
                .addAttribute("name", "lookup-method")
                .addAttribute("value", LOOKUP_METHOD);

        for (Element element : elements) {
            root.add(element);
        }

        return document;

    }

    private Element getCustomFields(String caseAutomation, String caseImportance, String caseLevel, String casePosNeg,
                                    String testType, String automation_script) {
        Map<String, String> custom_fields = ImmutableMap.<String, String>builder()
                .put("caseautomation", caseAutomation)
                .put("caseimportance", caseImportance)
                .put("caselevel", caseLevel)
                .put("caseposneg", casePosNeg)
                .put("testtype", testType)
                .put("automation_script", automation_script)
                .build();
        Element customFields = DocumentHelper.createElement("custom-fields");
        custom_fields.forEach((key, value) -> {
            customFields.addElement("custom-field")
                    .addAttribute("content", value)
                    .addAttribute("id", key);
        });

        return customFields;
    }
}
