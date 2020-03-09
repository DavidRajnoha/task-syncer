package com.redhat.tasksyncer.dao.enumerations;

/**
 * @author David Rajnoha
 * */

public enum IssueType {
    GITLAB, GITHUB, JIRA, TRELLO;


    public static IssueType fromString(String type){
        type = type.toLowerCase();

        switch (type){
            case "gitlab":
                return GITLAB;
            case "github":
                return GITHUB;
            case "jira":
                return JIRA;
            case "trello":
                return TRELLO;
            default:
                throw new IllegalArgumentException("Issue Type Not Found");
        }

    }

}

