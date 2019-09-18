package com.redhat.integration.repositories;

import com.redhat.tasksyncer.Application;
import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import com.redhat.tasksyncer.dao.repositories.ProjectRepository;
import org.gitlab4j.api.models.Issue;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;



@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan("com.redhat.tasksyncer")
@SpringBootTest(classes = Application.class)
@AutoConfigureTestDatabase
public class IssueRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AbstractIssueRepository issueRepository;

    @Autowired
    private ProjectRepository projectRepository;


    private String ghRepositoryName = "ghRepository";
    private String glRepositoryName = "glRepository";

    private String issueRemoteId = "1";

    private Project project;

    private AbstractRepository ghRepository;
    private AbstractRepository glRepository;

    private AbstractBoard board;
    private AbstractColumn column;

    private AbstractIssue ghIssue;
    private AbstractIssue glIssue;

    private AbstractIssue ghIssue2;

    @Before
    public void setUp() {
        project = new Project();
        project.setName("New Project");

        ghRepository = new GithubRepository();
        ghRepository.setRepositoryName(ghRepositoryName);

        glRepository = new GitlabRepository();
        glRepository.setRepositoryName(glRepositoryName);

        project.addRepository(glRepository);
        project.addRepository(ghRepository);

        board = new TrelloBoard();
        board.setBoardName("board");

        column = new TrelloColumn();
        column.setName("column");

        List<AbstractColumn> columns = new ArrayList<>();
        columns.add(column);
        board.setColumns(columns);
        project.setBoard(board);

        ghIssue = new GithubIssue();
        ghIssue.setRemoteIssueId(issueRemoteId);
        ghIssue.setRepository(ghRepository);


        glIssue = new GitlabIssue();
        glIssue.setRepository(glRepository);
        glIssue.setRemoteIssueId(issueRemoteId);

        projectRepository.save(project);
    }

    @Test
    public void contextLoads() {

    }

    @Test
    public void whenTwoGHIssueWithTheSameRemoteIssueAndRepositorySaved_thenFindIssueByRemoteIssueIdAndRepository_NameThenReturnProjectThrowsError() {
        AbstractIssue ghIssue2 = new GithubIssue();
        ghIssue2.setRemoteIssueId(ghIssue.getRemoteIssueId());
        ghIssue2.setRepository(ghIssue.getRepository());

        whenTwoIssueWithTheSameRemoteIssueAndRepositorySaved_thenFindIssueByRemoteIssueIdAndRepository_NameThenReturnProjectThrowsError(ghIssue, ghIssue2);
    }

    @Test
    public void whenTwoGLIssueWithTheSameRemoteIssueAndRepositorySaved_thenFindIssueByRemoteIssueIdAndRepository_NameThenReturnProjectThrowsError() {
        AbstractIssue glIssue2 = new GitlabIssue();
        glIssue2.setRemoteIssueId(glIssue.getRemoteIssueId());
        glIssue2.setRepository(glIssue.getRepository());

        whenTwoIssueWithTheSameRemoteIssueAndRepositorySaved_thenFindIssueByRemoteIssueIdAndRepository_NameThenReturnProjectThrowsError(glIssue, glIssue2);
    }

    private void whenTwoIssueWithTheSameRemoteIssueAndRepositorySaved_thenFindIssueByRemoteIssueIdAndRepository_NameThenReturnProjectThrowsError(AbstractIssue issue1, AbstractIssue issue2){
        issueRepository.save(issue1);
        issueRepository.save(issue2);

        assertThatThrownBy(() -> issueRepository.findByRemoteIssueIdAndRepository_repositoryName(issue1.getRemoteIssueId(), issue1.getRepository().getRepositoryName()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
    


    //probably not in this package

}
