package com.redhat.tasksyncer.presentation.trello;

import com.redhat.tasksyncer.dao.entities.projects.Project;

import java.io.IOException;

/**
 * @author Filip Cap
 */
public interface BoardAccessor {
    void createBoard(Project project);

    String deleteBoard(String trelloApplicationKey, String trelloAccessToken, Project project) throws IOException;
}
