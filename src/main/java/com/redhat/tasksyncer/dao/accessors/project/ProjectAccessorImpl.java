package com.redhat.tasksyncer.dao.accessors.project;


import com.redhat.tasksyncer.dao.entities.projects.Project;
import com.redhat.tasksyncer.dao.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Filip Cap, David Rajnoha
 */

@Service
@EntityScan(basePackages = {"com.redhat.tasksyncer.dao.entities"})
@EnableJpaRepositories(basePackages = {"com.redhat.tasksyncer.dao.repositories"})
public class ProjectAccessorImpl implements ProjectAccessor{


    private ProjectRepository projectRepository;


    @Value("${trello.appKey}")
    private String trelloApplicationKey;

    @Value("${trello.token}")
    private String trelloAccessToken;

    private Project project;



    @Autowired
    public ProjectAccessorImpl(ProjectRepository projectRepository){
        this.projectRepository = projectRepository;
    }


    @Override
    public Project saveProject(Project project) {
        this.project = projectRepository.save(project);
        return this.project;
    }

    public void save() {
        project = projectRepository.save(project);
    }


    public void setColumnNames(List<String> columnNames){
        project.setColumnNames(columnNames);
        projectRepository.save(project);
    }

    public void deleteProject(Project project) {
        if (this.project == project) this.project = null;
        projectRepository.delete(project);
    }

    public void createProject(String projectName, List<String> columnNames){
        Project project = new Project();
        project.setName(projectName);
        project.setColumnNames(columnNames);

        projectRepository.save(project);
    }

    public Project getProject(String projectName){
        return projectRepository.findProjectByName(projectName).orElseThrow(() ->
                new IllegalArgumentException("Project with name " + projectName + " does not exist"));
    }

}