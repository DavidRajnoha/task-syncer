package com.redhat.integration.repositories;

import com.redhat.tasksyncer.Application;
import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.ProjectRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan("com.redhat.tasksyncer")
@SpringBootTest(classes = Application.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    Project project;
    AbstractRepository repository;
    AbstractBoard board;
    AbstractColumn column;

    @Before
    public void setUp() {
        project = new Project();
        project.setName("New Project");

        repository = new GithubRepository();
        repository.setRepositoryName("Repo Name");

        board = new TrelloBoard();
        board.setBoardName("board");

        column = new TrelloColumn();
        column.setName("column");

        List<AbstractColumn> columns = new ArrayList<>();
        columns.add(column);
        board.setColumns(columns);
        project.setBoard(board);
    }

    @Test
    public void contextLoads() {

    }

    @Test
    public void whenFindByName_thenReturnProject() {

        entityManager.persist(project);
        entityManager.flush();

        Project projectFound = null;
        if (projectRepository.findProjectByName(project.getName()).isPresent()) {
            projectFound = projectRepository.findProjectByName(project.getName()).get()  ;
        }

        assertThat(project.getName()).isEqualTo(projectFound.getName());

    }
}
