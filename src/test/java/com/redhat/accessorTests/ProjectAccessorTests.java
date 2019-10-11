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
    public void whenNewIssueIsCreated_thenNewIssueIsReturned(){
        AbstractIssue foundIssue = projectAccessor.update(newIssue);

        assertThat(foundIssue.getTitle()).isEqualTo(newIssue.getTitle());
        assertThat(foundIssue.getDescription()).isEqualTo(newIssue.getDescription());
    }

}
