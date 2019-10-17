package com.redhat.accessorTests;

import com.redhat.tasksyncer.dao.accessors.ProjectAccessor;
import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static junit.framework.TestCase.assertTrue;



import java.util.Optional;


@RunWith(MockitoJUnitRunner.class)
public class ProjectAccessorTests {


    @InjectMocks
    private AbstractIssueRepository issueRepository = Mockito.mock(AbstractIssueRepository.class);

    private Optional<AbstractIssue> abstractIssueOptional;
    private AbstractIssue abstractIssue;
    private AbstractRepository abstractRepository;

    private ProjectAccessor projectAccessor;


    private AbstractIssue updatedIssue;
    private AbstractIssue newIssue;

    private AbstractIssue parentIssue;
    private AbstractIssue parentUpdatedIssue;
    private AbstractIssue subIssue;
    private AbstractIssue subUpdatedIssue;


    private String oldDescription = "Cool Issue";
    private String updatedDescription = "Much cooler issue";
    private String assignee = "Assignee";


    @Before
    public void setup(){
        abstractRepository = new GitlabRepository();
        abstractRepository.setRepositoryName("repoName");

        abstractIssue = new GitlabIssue();
        abstractIssue.setRemoteIssueId("1");
        abstractIssue.setRepository(abstractRepository);
        abstractIssue.setTitle("old title");
        abstractIssue.setId(1L);
        abstractIssue.setAssignee(assignee);
        abstractIssue.setDescription(oldDescription);

        Mockito.when(issueRepository.findByRemoteIssueIdAndRepository_repositoryName(abstractIssue.getRemoteIssueId(),
                abstractIssue.getRepository().getRepositoryName())).thenReturn(Optional.of(abstractIssue));

        projectAccessor = new ProjectAccessor(null, null, null,
                issueRepository, null, null, null, null,
                null);

        updatedIssue = new GitlabIssue();
        updatedIssue.setRepository(abstractRepository);
        updatedIssue.setRemoteIssueId("1");
        updatedIssue.setTitle("Updated title");
        updatedIssue.setDescription(updatedDescription);
        updatedIssue.setAssignee(null);

        newIssue = new GitlabIssue();
        newIssue.setRemoteIssueId("2");
        newIssue.setTitle("title");
        newIssue.setDescription("description");
        newIssue.setRepository(abstractRepository);


        subIssue = new GitlabIssue();
        subIssue.setRemoteIssueId("3");
        subIssue.setTitle("title");
        subIssue.setId(2L);
        subIssue.setDescription("description");
        subIssue.setRepository(abstractRepository);

        subUpdatedIssue = new GitlabIssue();
        subUpdatedIssue.setRemoteIssueId("3");
        subUpdatedIssue.setTitle("title");
        subUpdatedIssue.setDescription("CoolerDescripotion");
        subUpdatedIssue.setRepository(abstractRepository);

        parentIssue = new GitlabIssue();
        parentIssue.setRemoteIssueId("4");
        parentIssue.setRepository(abstractRepository);
        parentIssue.setTitle("old title");
        parentIssue.setId(1L);
        parentIssue.setAssignee(assignee);
        parentIssue.setDescription(oldDescription);
        parentIssue.addChildIssue(subIssue);

        parentUpdatedIssue = new GitlabIssue();
        parentUpdatedIssue.setRepository(abstractRepository);
        parentUpdatedIssue.setRemoteIssueId("4");
        parentUpdatedIssue.setTitle("Updated title");
        parentUpdatedIssue.setDescription(updatedDescription);
        parentUpdatedIssue.setAssignee(null);
        parentUpdatedIssue.addChildIssue(subUpdatedIssue);

    }

    @Test
    public void whenExistingIssueIsUpdated_thenChangedFieldsAreUpdated(){
        AbstractIssue foundIssue = projectAccessor.update(updatedIssue);

        // updated fields
        assertThat(foundIssue.getTitle()).isEqualTo(updatedIssue.getTitle());
        assertThat(foundIssue.getDescription()).isEqualTo(updatedIssue.getDescription());

        // field that is null in updated issue
        assertThat(foundIssue.getAssignee()).isEqualTo(abstractIssue.getAssignee());
    }

    @Test
    public void whenNewIssueIsUpdated_thenNewIssueIsReturned(){
        AbstractIssue foundIssue = projectAccessor.update(newIssue);

        assertThat(foundIssue.getTitle()).isEqualTo(newIssue.getTitle());
        assertThat(foundIssue.getDescription()).isEqualTo(newIssue.getDescription());
    }

    @Test
    public void whenExistingIssueWithExisitingSubIssueIsUpdated_thenChangedFieldsAreUpdated(){
        // Mocking the find method in repository so the issues will look like old issues
        Mockito.when(issueRepository.findByRemoteIssueIdAndRepository_repositoryName(parentIssue.getRemoteIssueId(),
                parentIssue.getRepository().getRepositoryName())).thenReturn(Optional.of(parentIssue));

        Mockito.when(issueRepository.findByRemoteIssueIdAndRepository_repositoryName(subIssue.getRemoteIssueId(),
                subIssue.getRepository().getRepositoryName())).thenReturn(Optional.of(subIssue));

        AbstractIssue foundIssue = projectAccessor.update(parentUpdatedIssue);

        // updated fields
        assertThat(foundIssue.getTitle()).isEqualTo(parentUpdatedIssue.getTitle());
        assertThat(foundIssue.getDescription()).isEqualTo(parentUpdatedIssue.getDescription());

        // subIssue is found
        assertTrue(foundIssue.getChildIssues().contains(subIssue));


        // field that is null in updated issue
        assertThat(foundIssue.getAssignee()).isEqualTo(parentIssue.getAssignee());
        assertThat(foundIssue.getChildIssues().size()).isEqualTo(1);
    }

    @Test
    public void whenNewIssueWithNewSubIssueIsUpdated_thenAreTheseIssuesCorrectlySaved(){
        AbstractIssue foundIssue = projectAccessor.update(parentUpdatedIssue);

        assertThat(foundIssue.getTitle()).isEqualTo(parentUpdatedIssue.getTitle());
        assertThat(foundIssue.getDescription()).isEqualTo(parentUpdatedIssue.getDescription());

        // subIssue is found
        assertTrue(foundIssue.getChildIssues().contains(subUpdatedIssue));


        // field that is null in updated issue
        assertThat(foundIssue.getAssignee()).isEqualTo(parentUpdatedIssue.getAssignee());
        assertThat(foundIssue.getChildIssues().size()).isEqualTo(1);
    }

    @Test
    public void whenExistingIssueWithNewSubIssueIsUpdated_thenAreTheseIssuesCorrectlyUpdated(){
        // Mocking the find method in repository so the issues will look like old issues
        Mockito.when(issueRepository.findByRemoteIssueIdAndRepository_repositoryName(parentIssue.getRemoteIssueId(),
                parentIssue.getRepository().getRepositoryName())).thenReturn(Optional.of(parentIssue));

        AbstractIssue foundIssue = projectAccessor.update(parentUpdatedIssue);

        // updated fields
        assertThat(foundIssue.getTitle()).isEqualTo(parentUpdatedIssue.getTitle());
        assertThat(foundIssue.getDescription()).isEqualTo(parentUpdatedIssue.getDescription());

        // subIssue is found
        assertTrue(foundIssue.getChildIssues().contains(subUpdatedIssue));


        // field that is null in updated issue
        assertThat(foundIssue.getAssignee()).isEqualTo(parentIssue.getAssignee());
        assertThat(foundIssue.getChildIssues().size()).isEqualTo(1);
    }


    @Test
    public void whenNewIssueWithExistingSubIssueIsUpdated_thenAreTheseIssuesCorrectlyUpdated(){
        // Mocking the find method in repository so the issues will look like old issues
        Mockito.when(issueRepository.findByRemoteIssueIdAndRepository_repositoryName(parentIssue.getRemoteIssueId(),
                parentIssue.getRepository().getRepositoryName())).thenReturn(Optional.of(parentIssue));

        Mockito.when(issueRepository.findByRemoteIssueIdAndRepository_repositoryName(subIssue.getRemoteIssueId(),
                subIssue.getRepository().getRepositoryName())).thenReturn(Optional.of(subIssue));

        AbstractIssue foundIssue = projectAccessor.update(parentUpdatedIssue);

        // updated fields
        assertThat(foundIssue.getTitle()).isEqualTo(parentUpdatedIssue.getTitle());
        assertThat(foundIssue.getDescription()).isEqualTo(parentUpdatedIssue.getDescription());

        // subIssue is found
        assertTrue(foundIssue.getChildIssues().contains(subIssue));
    }


}
