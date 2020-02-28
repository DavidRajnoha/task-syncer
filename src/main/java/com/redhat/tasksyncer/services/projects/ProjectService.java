package com.redhat.tasksyncer.services.projects;

import com.redhat.tasksyncer.dao.accessors.project.ProjectAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    ProjectAccessor projectAccessor;

    @Autowired
    public ProjectService(ProjectAccessor projectAccessor){
        this.projectAccessor = projectAccessor;
    }


    public void createProject(String projectName, List<String> columnNames){
        projectAccessor.createProject(projectName, columnNames);
    }


}
