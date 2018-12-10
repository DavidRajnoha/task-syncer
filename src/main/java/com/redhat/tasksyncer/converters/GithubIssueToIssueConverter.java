package com.redhat.tasksyncer.converters;

import com.redhat.tasksyncer.dao.entities.Issue;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;

import java.util.HashSet;

/**
 * @author Filip Cap
 */
public class GithubIssueToIssueConverter implements ObjectToIssueConverter<GHIssue> {
    @Override
    public Issue convert(GHIssue object) {
        return new Issue(
                String.valueOf(object.getId()),
                String.valueOf(object.getNumber()),
                object.getTitle(),
                object.getBody(),
                (object.getState() == GHIssueState.OPEN),
                /*object.getLabels().stream().map(GHLabel::getName).collect(Collectors.toSet()),*/ //todo;
                new HashSet<>(),
                Issue.GITHUB_ISSUE
        );
    }
}
