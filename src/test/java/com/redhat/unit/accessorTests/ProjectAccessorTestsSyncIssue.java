package com.redhat.unit.accessorTests;


import com.redhat.tasksyncer.dao.accessors.BoardAccessor;
import com.redhat.tasksyncer.dao.accessors.ProjectAccessor;
import com.redhat.tasksyncer.dao.accessors.ProjectAccessorImpl;
import com.redhat.tasksyncer.dao.accessors.RepositoryAccessor;
import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.dao.repositories.ProjectRepository;
import com.redhat.tasksyncer.exceptions.CannotConnectToRepositoryException;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;


@RunWith(MockitoJUnitRunner.class)
public class ProjectAccessorTestsSyncIssue {

    @Mock
    private ProjectRepository projectRepository = Mockito.mock(ProjectRepository.class);

    @Mock
    private AbstractRepositoryRepository repositoryRepository = Mockito.mock(AbstractRepositoryRepository.class);

    @Mock
    private BoardAccessor boardAccessor = Mockito.mock(BoardAccessor.class);

    @Mock
    private AbstractIssueRepository issueRepository = Mockito.mock(AbstractIssueRepository.class);

    @Mock
    private Map mockMap = Mockito.mock(Map.class);

    @Mock
    private RepositoryAccessor mockRepositoryAccessor = Mockito.mock(RepositoryAccessor.class);

    private AbstractRepository githubRepository;
    private Project project;
    private AbstractBoard trelloBoard;


    private ProjectAccessor projectAccessor;

    private String remoteIssueId = "1";
    private String title = "Github Issue";
    private String description = "Description";
    private String ghRepositoryName = "GithubRepository";
    private String updatedDescription = "UPDATED!!!";


    //REALY stuppid test because it connects to Trello and it is not the thing it should be testing!!!!!!
    @Before
    public void setup() throws RepositoryTypeNotSupportedException {
        project = new Project();
        projectAccessor = new ProjectAccessorImpl(issueRepository, projectRepository, repositoryRepository, boardAccessor,
                mockMap);
        List<AbstractColumn> columns = new ArrayList<>();
        AbstractColumn abstractColumn = new TrelloColumn();
        AbstractColumn abstractColumnTwo = new TrelloColumn();
        abstractColumn.setName("TODO");
        abstractColumnTwo.setName("DONE");
        columns.add(abstractColumn);
        columns.add(abstractColumnTwo);
        Mockito.when(boardAccessor.getColumns()).thenReturn(columns);

        Mockito.when(projectRepository.save(project)).thenReturn(project);

        githubRepository = new GithubRepository();
        githubRepository.setFirstLoginCredential("userName");
        githubRepository.setSecondLoginCredential("passwd");
        githubRepository.setRepositoryName(ghRepositoryName);

        projectAccessor.saveAndInitialize(project);


    }

    @Test
    public void contextLoads() {
    }

    //Stubbing with mockito on spyProjectAccessorIsNotWorking, see if it will work on projectAccessorImpl

    @Test
    public void whenProjectAccessorUpdatesNewGithubIssue_thenNewGithubIssueIsSaved() {
        // Tested classes
        AbstractIssue newGithubIssue = getNewGithubIssue();
        projectAccessor.saveAndInitialize(project);

        // Mocking
        Mockito.when(issueRepository.findByRemoteIssueIdAndRepository_repositoryName(newGithubIssue.getRemoteIssueId(),
                newGithubIssue.getRepository().getRepositoryName())).thenReturn(Optional.of(newGithubIssue));
        Mockito.when(issueRepository.save(any(GithubIssue.class))).thenReturn((GithubIssue) newGithubIssue);

        ArgumentCaptor<AbstractIssue> argument = ArgumentCaptor.forClass(AbstractIssue.class);

        // Test
        projectAccessor.syncIssue(newGithubIssue);

        // Assertions
        Mockito.verify(issueRepository, Mockito.times(2)).save(argument.capture());

        AbstractIssue foundIssue = argument.getValue();

        assertThat(foundIssue).isNotNull();
        assertThat(foundIssue.getTitle()).isEqualTo(title);
    }

    //Test works in issolation, problem with board Creation
    @Test
    public void whenProjectAccessorUpdatesExistingGithubIssue_thenIsTheIssueReallyUpdated() throws Exception {

        // Tested objects
        AbstractIssue existingGithubIssue = getNewGithubIssue();
        existingGithubIssue.setId(1L);
        AbstractIssue updatedGithubIssue = getNewGithubIssue();
        updatedGithubIssue.setDescription(updatedDescription);
        projectAccessor.saveAndInitialize(project);

        // Mocking
        Mockito.when(issueRepository.findByRemoteIssueIdAndRepository_repositoryName(existingGithubIssue.getRemoteIssueId(),
                existingGithubIssue.getRepository().getRepositoryName())).thenReturn(Optional.of(existingGithubIssue));
        Mockito.when(issueRepository.save(existingGithubIssue)).thenReturn(existingGithubIssue);

        ArgumentCaptor<AbstractIssue> argument = ArgumentCaptor.forClass(AbstractIssue.class);

        //Test
        projectAccessor.syncIssue(updatedGithubIssue);


        //Assertion
        Mockito.verify(issueRepository, Mockito.times(2)).save(argument.capture());

        AbstractIssue foundIssue = argument.getValue();

        assertThat(foundIssue.getDescription()).isEqualTo(updatedDescription);
    }

    @Test
    public void createRepositoryAccessor() throws RepositoryTypeNotSupportedException, CannotConnectToRepositoryException {
        Mockito.when(mockMap.get("githubRepositoryAccessor")).thenReturn(mockRepositoryAccessor);
        Mockito.when(mockRepositoryAccessor.createItself()).thenReturn(githubRepository);

        RepositoryAccessor foundAccessor = projectAccessor.createRepositoryAccessor(githubRepository);

        assertThat(foundAccessor).isEqualTo(mockRepositoryAccessor);

    }


    private AbstractIssue getNewGithubIssue() {
        AbstractIssue newGithubIssue = new GithubIssue();
        newGithubIssue.setRemoteIssueId(remoteIssueId);
        newGithubIssue.setTitle(title);
        newGithubIssue.setDescription(description);
        newGithubIssue.setState(AbstractIssue.STATE_OPENED);
        newGithubIssue.setRepository(githubRepository);

        return newGithubIssue;
    }



}



