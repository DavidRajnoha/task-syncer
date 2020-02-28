package com.redhat.tasksyncer.dao.accessors.project;

import com.redhat.tasksyncer.dao.entities.projects.Project;

import java.util.List;


/**
 * @author Filip Cap, David Rajnoha
 */
public interface ProjectAccessor {


    /**
     * @param project - the project you wish to access and modify with this project accessor
     *                <p>
     *                Adds project to this.project field, so the project can be modified by other methods of the projectAccessor
     * @return project passed as an argument with filled in id
     */
    Project saveProject(Project project);


    /**
     * Saves the project associated with the projectAccessor
     */
    void save();

    /**
     * Deletes the project passed as argument
     */
    void deleteProject(Project project);


    void setColumnNames(List<String> columnNames);


    void createProject(String projectName, List<String> columnNames);

    Project getProject(String projectName);
}