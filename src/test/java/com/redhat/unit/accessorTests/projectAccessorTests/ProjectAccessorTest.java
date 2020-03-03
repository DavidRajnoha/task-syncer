package com.redhat.unit.accessorTests.projectAccessorTests;

import com.redhat.tasksyncer.dao.accessors.project.ProjectAccessor;
import com.redhat.tasksyncer.dao.accessors.project.ProjectAccessorImpl;
import com.redhat.tasksyncer.dao.entities.projects.Project;
import com.redhat.tasksyncer.dao.repositories.ProjectRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class ProjectAccessorTest {

    @Mock
    private ProjectRepository projectRepository;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks
    private ProjectAccessor projectAccessor = new ProjectAccessorImpl(projectRepository);

    private String newProjectName;

    private List<String> columnNames;
    private String name1;
    private String name2;

    private Project project;
    private String projectName;



    public ProjectAccessorTest() {
    }


    @Before
    public void setup(){

        columnNames = new ArrayList<>();
        columnNames.add(name1);
        columnNames.add(name2);

        project = new Project();
        project.setName(projectName);
        project.setColumnNames(columnNames);

        Mockito.when(projectRepository.findProjectByName(projectName)).thenReturn(java.util.Optional.ofNullable(project));
    }

    @Test
    public void whenCreatingProjectWithCorrectArgs_thenProjectIsCreated(){
        ArgumentCaptor<Project> argumentCaptor = ArgumentCaptor.forClass(Project.class);

        projectAccessor.createProject(newProjectName, columnNames);

        Mockito.verify(projectRepository, Mockito.times(1)).save(argumentCaptor.capture());

        Project savedProject = argumentCaptor.getValue();

        assertThat(savedProject.getName()).isEqualTo(newProjectName);
        assertThat(savedProject.getColumnNames().get()).isEqualTo(columnNames);
    }

    @Test
    public void whenCallingGetProjectWithValidName_thenProjectIsReturned(){
        Project foundProject = projectAccessor.getProject(projectName);

        assertThat(foundProject.getName()).isEqualTo(projectName);
        assertThat(foundProject.getColumnNames()).isEqualTo(Optional.of(columnNames));
    }

    @Test
    public void whenCallingGetProjectWithInvalidName_thenExceptionIsThrown(){
        assertThatThrownBy(() -> projectAccessor.getProject("somName"))
                .isInstanceOf(IllegalArgumentException.class);
    }


}
