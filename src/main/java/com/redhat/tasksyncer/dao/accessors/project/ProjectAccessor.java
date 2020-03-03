package com.redhat.tasksyncer.dao.accessors.project;

import com.redhat.tasksyncer.dao.entities.projects.Project;

import java.util.List;


/**
 * @author David Rajnoha
 */
public interface ProjectAccessor {


    /**
     * Deletes the project defined by the projectName
     */
    void deleteProject(String projectName);

    /**
     * Creates project with the name as the argument, adds the columnNames
     * passed in the secondArg to it
     *
     * @param projectName - name of the newly created project, unique
     * @param columnNames - names of the column used for the mapping when
     *                    getting issues from remote repositories
     */
    void createProject(String projectName, List<String> columnNames);

    /**
     * Gets project based on the passed name
     *
     * @param projectName unique identifier of the project
     * @return project with projectName
     */
    Project getProject(String projectName);
}