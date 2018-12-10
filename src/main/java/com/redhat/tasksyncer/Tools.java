package com.redhat.tasksyncer;

import com.redhat.tasksyncer.dao.entities.Issue;
import org.gitlab.api.models.GitlabIssue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Filip Cap
 */
public class Tools {
//    public static void issueToTrelloCard();
    public static List<Issue> toIssues(List<GitlabIssue> input) {
        List<Issue> issues = new ArrayList<>();

        for(GitlabIssue item : input) {
            issues.add(new Issue(item));
        }
        return issues;
    }
}
