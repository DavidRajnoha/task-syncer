package com.redhat.entitiesTests;

import com.redhat.tasksyncer.Application;
import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.ProjectRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class RelationshipConsistenceTests {


    @Autowired
    private ProjectRepository projectRepository;

    Project project;
    AbstractRepository repository;
    AbstractBoard board;
    AbstractColumn column;
    AbstractIssue issue;
    AbstractIssue childIssueOne;
    AbstractIssue childIssueTwo;
    AbstractCard card;

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

        issue = new GitlabIssue();
        issue.setDescription("Issue");

        childIssueOne = new GitlabIssue();
        childIssueOne.setDescription("child One");

        childIssueTwo = new GitlabIssue();
        childIssueTwo.setDescription("child Two");
        
        card = new TrelloCard();
        card.setDescription("Description");

        List<AbstractColumn> columns = new ArrayList<>();
        columns.add(column);
    }
    
    //Project to board integration
    @Test
    public void whenBoardSetToProject_thenProjectSetToBoard(){
        project.setBoard(board);
        assertThat(project).isEqualTo(board.getProject());
    }

    @Test
    public void whenBoardRemovedFromProject_thenProjectRemovedFromBoard(){
        project.setBoard(board);
        project.setBoard(null);
        assertThat(board.getProject()).isEqualTo(null);
    }

    @Test
    public void whenProjectSetToBoard_thenBoardSetToProject(){
        board.setProject(project);
        assertThat(board).isEqualTo(project.getBoard());
    }

    @Test
    public void whenProjectRemovedFromBoard_thenBoardRemovedFromProject(){
        board.setProject(project);
        board.setProject(null);
        assertThat(project.getBoard()).isEqualTo(null);
    }

    
    //Project ot Repository Integration
    @Test
    public void whenRepositoryAddedToProject_thenProjectSetToRepository(){
        project.addRepository(repository);
        assertThat(project).isEqualTo(repository.getProject());
    }

    @Test
    public void whenProjectSetToRepository_thenRepositoryAddedToProject(){
        repository.setProject(project);
        assertTrue(project.getRepositories().contains(repository));
    }

    @Test
    public void whenRepositoryRemovedFromProject_thenProjectRemovedFromRepository(){
        repository.setProject(project);
        project.removeRepository(repository);
        assertThat(repository.getProject()).isEqualTo(null);
    }

    @Test
    public void whenProjectRemovedFromRepository_thenRepositoryRemovedFromProject(){
        project.addRepository(repository);
        repository.setProject(null);
        assertFalse(project.getRepositories().contains(repository));
    }
    
    
    //RepositoryToIssue
    @Test
    public void whenIssueAddedToRepository_thenRepositorySetToIssue(){
        repository.addIssue(issue);
        assertThat(repository).isEqualTo(issue.getRepository());
    }

    @Test
    public void whenRepositorySetToIssue_thenIssueAddedToRepository(){
        issue.setRepository(repository);
        assertTrue(repository.getIssues().contains(issue));
    }

    @Test
    public void whenIssueRemovedFromRepository_thenRepositoryRemovedFromIssue(){
        issue.setRepository(repository);
        repository.removeIssue(issue);
        assertThat(issue.getRepository()).isEqualTo(null);
    }

    @Test
    public void whenRepositoryRemovedFromIssue_thenIssueRemovedFromRepository(){
        repository.addIssue(issue);
        issue.setRepository(null);
        assertFalse(repository.getIssues().contains(issue));
    }



    //Board to Columns
    @Test
    public void whenColumnAddedToBoard_thenBoardSetToColumn(){
        board.addColumn(column);
        assertThat(board).isEqualTo(column.getBoard());
    }

    @Test
    public void whenBoardSetToColumn_thenColumnAddedToBoard(){
        column.setBoard(board);
        assertTrue(board.getColumns().contains(column));
    }

    @Test
    public void whenColumnRemovedFromBoard_thenBoardRemovedFromColumn(){
        column.setBoard(board);
        board.removeColumn(column);
        assertThat(column.getBoard()).isEqualTo(null);
    }

    @Test
    public void whenBoardRemovedFromColumn_thenColumnRemovedFromBoard(){
        board.addColumn(column);
        column.setBoard(null);
        assertFalse(board.getColumns().contains(column));
    }
    
    
    //Column to Cards
    @Test
    public void whenCardAddedToColumn_thenColumnSetToCard(){
        column.addCard(card);
        assertThat(column).isEqualTo(card.getColumn());
    }

    @Test
    public void whenColumnSetToCard_thenCardAddedToColumn(){
        card.setColumn(column);
        assertTrue(column.getCards().contains(card));
    }

    @Test
    public void whenCardRemovedFromColumn_thenColumnRemovedFromCard(){
        card.setColumn(column);
        column.removeCard(card);
        assertThat(card.getColumn()).isEqualTo(null);
    }

    @Test
    public void whenColumnRemovedFromCard_thenCardRemovedFromColumn(){
        column.addCard(card);
        card.setColumn(null);
        assertFalse(column.getCards().contains(card));
    }
    
    
    //CardToIssue
    @Test
    public void whenCardSetToIssue_thenIssueSetToCard(){
        issue.setCard(card);
        assertThat(issue).isEqualTo(card.getIssue());
    }

    @Test
    public void whenCardRemovedFromIssue_thenIssueRemovedFromCard(){
        issue.setCard(card);
        issue.setCard(null);
        assertThat(card.getIssue()).isEqualTo(null);
    }

    @Test
    public void whenIssueSetToCard_thenCardSetToIssue(){
        card.setIssue(issue);
        assertThat(card).isEqualTo(issue.getCard());
    }

    @Test
    public void whenIssueRemovedFromCard_thenCardRemovedFromIssue(){
        card.setIssue(issue);
        card.setIssue(null);
        assertThat(issue.getCard()).isEqualTo(null);
    }

    @Test
    public void whenChildIssueIsSetToParentIssue_thenParentIsSetToChild(){
        issue.addChildIssue(childIssueOne);
        assertTrue(issue.getChildIssues().contains(childIssueOne));
        assertThat(childIssueOne.getParentIssue()).isEqualTo(issue);
    }


    @Test
    public void whenChildIssueIsRemovedFromParentIssue_thenParentIsRemovedFromChild(){
        issue.addChildIssue(childIssueOne);
        issue.removeChildIssue(childIssueOne);

        assertThat(childIssueOne.getParentIssue()).isNull();
    }

    @Test
    public void whenParentIssueIsSetToChild_thenChildIsSetToParent(){
        childIssueOne.setParentIssue(issue);

        assertThat(childIssueOne.getParentIssue()).isEqualTo(issue);
        assertTrue(issue.getChildIssues().contains(childIssueOne));
    }

    @Test
    public void whenParentIssueIsRemovedFromChild_thenChildIsRemovedFromParent(){
        childIssueOne.setParentIssue(issue);
        childIssueOne.setParentIssue(null);

        assertThat(childIssueOne.getParentIssue()).isEqualTo(null);
        assertFalse(issue.getChildIssues().contains(childIssueOne));
    }

    @Test
    public void removeIssues_removesAllIssuesFromParentAndPrentFromAllChilds(){
        issue.addChildIssue(childIssueOne);
        issue.addChildIssue(childIssueTwo);

        issue.removeChildIssues();

        assertThat(issue.getChildIssues().size()).isEqualTo(0);
        assertThat(childIssueOne.getParentIssue()).isNull();
        assertThat(childIssueTwo.getParentIssue()).isNull();
    }





}
