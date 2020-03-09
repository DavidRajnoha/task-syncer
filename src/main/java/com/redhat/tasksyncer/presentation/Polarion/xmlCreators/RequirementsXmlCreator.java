package com.redhat.tasksyncer.presentation.Polarion.xmlCreators;

import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class RequirementsXmlCreator extends AbstractXmlCreator {


    public RequirementsXmlCreator(){
    }


    public Element issueToRequirement(AbstractIssue issue, String approver, String testCycle){
        Element requirement = DocumentHelper.createElement("requirement");
        requirement.addAttribute("approver-ids", approver + ":approved")
                .addAttribute("id", requirementTitle(issue.getTitle()))
                .addAttribute("planned-in-ids", testCycle)
                .addAttribute("initial-estimate", "1d")
                .addAttribute("priority-id", "high")
                .addAttribute("severity-id", "should_have")
                .addAttribute("status-id", "approved");
        requirement.addElement("title")
                .addText(requirementTitle(issue.getTitle()));
        requirement.addElement("description")
                .addText(issue.getDescription());

        Element customFields = requirement.addElement("custom-fields");
        customFields.addElement("custom-field")
                .addAttribute("id", "reqtype")
                .addAttribute("content", "functional");

        return requirement;
    }


    public Document createXml(List<AbstractIssue> issues, String projectId, String approver, String testCycle){
        issues = getActiveIssues(issues);

        List<Element> requirements = issues.stream().map(issue -> issueToRequirement(issue, approver, testCycle))
                .collect(Collectors.toList());

        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("requirements")
                .addAttribute("project-id", projectId);

        Element properties = root.addElement("properties");
        properties.addElement("property")
                .addAttribute("name", "lookup-method")
                .addAttribute("value", "name");

        for (Element requirement : requirements){
            root.add(requirement);
        }

        return document;
    }






}
