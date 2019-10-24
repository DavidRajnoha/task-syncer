package com.redhat.unit.accessorTests;

import com.redhat.tasksyncer.dao.accessors.ProjectAccessor;
import com.redhat.tasksyncer.dao.accessors.ProjectAccessorImpl;
import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class ProjectAccessorTests {


    @InjectMocks
    private AbstractIssueRepository issueRepository = Mockito.mock(AbstractIssueRepository.class);
    private AbstractRepository abstractRepository;
    private ProjectAccessor projectAccessor;

    private String oldDescription = "Cool Issue";
    private String updatedDescription = "Much cooler issue";
    private String assignee = "Assignee";




    @Before
    public void setup(){
        abstractRepository = new GitlabRepository();
        abstractRepository.setRepositoryName("repoName");



        projectAccessor = new ProjectAccessorImpl();


    }

    @Test
    public void whenExistingIssueIsUpdated_thenChangedFieldsAreUpdated(){
        AbstractIssue oldIssue = getExistingIssue("1");
        AbstractIssue updatedIssue = getUpdatedIssue(oldIssue);

        AbstractIssue foundIssue = projectAccessor.update(updatedIssue);

        // updated fields
        assertThat(foundIssue.getTitle()).isEqualTo(updatedIssue.getTitle());

        // field that is null in updated issue
        assertThat(foundIssue.getAssignee()).isEqualTo(oldIssue.getAssignee());
    }

    @Test
    public void whenNewIssueIsUpdated_thenNewIssueIsReturned(){
        AbstractIssue newIssue = getNewGitlabIssue("2");

        AbstractIssue foundIssue = projectAccessor.update(newIssue);

        assertThat(foundIssue.getTitle()).isEqualTo(newIssue.getTitle());
        assertThat(foundIssue.getDescription()).isEqualTo(newIssue.getDescription());
    }

    @Test
    public void whenExistingIssueWithExisitingSubIssueIsUpdated_thenChangedFieldsAreUpdated(){
        // Mocking the find method in repository so the issues will look like old issues
        AbstractIssue parentIssue = getExistingIssue("3");
        AbstractIssue subIssue = getExistingSubIssue("4");
        parentIssue.addChildIssue(subIssue);
        AbstractIssue parentUpdatedIssue = getUpdatedIssue(parentIssue);


        AbstractIssue foundIssue = projectAccessor.update(parentUpdatedIssue);

        // updated fields
        assertThat(foundIssue.getTitle()).isEqualTo(parentUpdatedIssue.getTitle());
        assertThat(foundIssue.getDescription()).isEqualTo(parentUpdatedIssue.getDescription());

        // subIssue is found
        assertTrue(foundIssue.getChildIssues().containsValue(subIssue));


        // field that is null in updated issue
        assertThat(foundIssue.getAssignee()).isEqualTo(parentIssue.getAssignee());
        assertThat(foundIssue.getChildIssues().size()).isEqualTo(1);
    }

    @Test
    public void whenExistingIssueWithNewSubIssueIsUpdated_thenAreTheseIssuesCorrectlySaved(){
        AbstractIssue parentIssue = getExistingIssue("5");
        AbstractIssue parentUpdatedIssue = getUpdatedIssue(parentIssue);
        AbstractIssue subIssue = getNewSubIssue("6");
        parentUpdatedIssue.addChildIssue(subIssue);



        AbstractIssue foundIssue = projectAccessor.update(parentUpdatedIssue);

        assertThat(foundIssue.getTitle()).isEqualTo(parentUpdatedIssue.getTitle());
        assertThat(foundIssue.getDescription()).isEqualTo(parentUpdatedIssue.getDescription());

        // subIssue is found
        assertThat(foundIssue.getChildIssues().get(subIssue.getRemoteIssueId()).getRemoteIssueId()).isEqualTo(subIssue.getRemoteIssueId());


        // field that is null in updated issue
        assertThat(foundIssue.getAssignee()).isEqualTo(parentUpdatedIssue.getAssignee());
        assertThat(foundIssue.getChildIssues().size()).isEqualTo(1);
    }

    @Test
    public void whenNewIssueWithNewSubIssueIsUpdated_thenAreTheseIssuesCorrectlyUpdated(){
        AbstractIssue newIssue = getNewGitlabIssue("7");
        AbstractIssue newSubIssue = getNewSubIssue("8");
        newIssue.addChildIssue(newSubIssue);


        AbstractIssue foundIssue = projectAccessor.update(newIssue);

        // updated fields
        assertThat(foundIssue.getTitle()).isEqualTo(newIssue.getTitle());
        assertThat(foundIssue.getDescription()).isEqualTo(newSubIssue.getDescription());

        // subIssue is found
        assertTrue(foundIssue.getChildIssues().containsValue(newSubIssue));


        // field that is null in updated issue
        assertThat(foundIssue.getAssignee()).isEqualTo(newIssue.getAssignee());
        assertThat(foundIssue.getChildIssues().size()).isEqualTo(1);
    }


    @Test
    public void whenNewIssueWithExistingSubIssueIsUpdated_thenAreTheseIssuesCorrectlyUpdated(){
        AbstractIssue existingSubIssue = getExistingIssue("10");
        AbstractIssue updatedSubIssue = getUpdatedIssue(existingSubIssue);

        AbstractIssue newIssue = getNewGitlabIssue("9");
        newIssue.addChildIssue(updatedSubIssue);


        AbstractIssue foundIssue = projectAccessor.update(newIssue);

        // updated fields
        assertThat(foundIssue.getTitle()).isEqualTo(newIssue.getTitle());
        assertThat(foundIssue.getDescription()).isEqualTo(newIssue.getDescription());

        // subIssue is found and is based on the old issue
        assertThat(foundIssue.getChildIssues().get(updatedSubIssue.getRemoteIssueId()).getId())
                .isEqualTo(existingSubIssue.getId());

        // subIssueIsUpdated
        assertThat(foundIssue.getChildIssues().get(updatedSubIssue.getRemoteIssueId()).getTitle())
                .isEqualTo(updatedSubIssue.getTitle());

    }

    @Test
    public void updateIssueWithSubtaskWithoutProvidingTheSubtask(){
        AbstractIssue oldIssueWithSubtask = getExistingIssue("11");
        AbstractIssue updatedIssue = getUpdatedIssue(oldIssueWithSubtask);
        AbstractIssue existingSubtask = getExistingSubIssue("12");
        oldIssueWithSubtask.addChildIssue(existingSubtask);

        AbstractIssue foundIssue  = projectAccessor.update(updatedIssue);

        assertThat(foundIssue.getChildIssues().size()).isEqualTo(1);
    }

    @Test
    public void updateIssueWithManySubtasksWhileProvideingJustOne(){
        AbstractIssue existingParentIssue = getExistingIssue("13");
        AbstractIssue updatedParentIssue = getUpdatedIssue(existingParentIssue);
        AbstractIssue subIssueOne = getExistingSubIssue("14");
        AbstractIssue subIssueTwo = getExistingSubIssue("15");
        AbstractIssue updatedSubIssue = getUpdatedIssue(subIssueOne);

        existingParentIssue.addChildIssue(subIssueOne);
        existingParentIssue.addChildIssue(subIssueTwo);
        updatedParentIssue.addChildIssue(updatedSubIssue);

        AbstractIssue foundIssue = projectAccessor.update(updatedParentIssue);

        assertThat(foundIssue.getChildIssues().size()).isEqualTo(2);
    }
    
    @Test
    public void whenUpdatingSubtaskWrappedInEmptyIssue_thenIsTheSubtaskUpdatedAndTheIssueInfoIsRetained(){
        AbstractIssue existingParentIssue = getExistingIssue("16");
        AbstractIssue exisitngSubIssue = getExistingSubIssue("17");


        AbstractIssue updatedSubIssue = getUpdatedIssue(exisitngSubIssue);
        AbstractIssue containerIssue = getParentContainerIssueIssue(existingParentIssue);

        existingParentIssue.addChildIssue(exisitngSubIssue);
        containerIssue.addChildIssue(updatedSubIssue);

        AbstractIssue foundIssue = projectAccessor.update(containerIssue);

        assertThat(foundIssue.getTitle()).isEqualTo(existingParentIssue.getTitle());
        assertThat(foundIssue.getChildIssues().get(updatedSubIssue.getRemoteIssueId()).getTitle())
                .isEqualTo(updatedSubIssue.getTitle());
    }


    @Test
    public void whenUpdatingNewIssueWithMoreThenOneSubtasks_thenNoConcurrentModificationIsThrown(){
        AbstractIssue parentIssueWithManySubtasks = getNewGitlabIssue("18");
        AbstractIssue subIssueOne = getNewSubIssue("19");
        AbstractIssue subIssueTwo = getNewSubIssue("20");
        parentIssueWithManySubtasks.addChildIssue(subIssueOne);
        parentIssueWithManySubtasks.addChildIssue(subIssueTwo);


        AbstractIssue foundIssue = projectAccessor.update(parentIssueWithManySubtasks);

        assertThat(foundIssue.getChildIssues().get(subIssueOne.getRemoteIssueId())).isEqualTo(subIssueOne);
    }




    private void mockGettingIssueFromRepository(AbstractIssue issueToFind){
        Mockito.when(issueRepository.findByRemoteIssueIdAndRepository_repositoryName(issueToFind.getRemoteIssueId(),
                issueToFind.getRepository().getRepositoryName())).thenReturn(Optional.of(issueToFind));
    }

    private AbstractIssue getPreFilledGitlabIssue(String remoteIssueId, Long id, String title){
        return getGitlabIssue(remoteIssueId, title, id, abstractRepository, assignee, oldDescription);
    }

    private AbstractIssue getNewGitlabIssue(String remoteIssueId){
        return getPreFilledGitlabIssue(remoteIssueId, null, "new Issue");
    }

    private AbstractIssue getSubIssue(String remoteIssueId, Long id, String title){
        return getPreFilledGitlabIssue(remoteIssueId, id, title);
    }

    private AbstractIssue getNewSubIssue(String remoteIssueId){
        return getSubIssue(remoteIssueId, null, "new subIssue");
    }

    private AbstractIssue getExistingSubIssue(String remoteIssueId){
        AbstractIssue existingSubIssue = getSubIssue(remoteIssueId, 1L, "existing subIssue");
        mockGettingIssueFromRepository(existingSubIssue);
        return existingSubIssue;
    }

    private AbstractIssue getUpdatedIssue(AbstractIssue oldIssue){
        return getPreFilledGitlabIssue(oldIssue.getRemoteIssueId(), null, "updeted supercool title");
    }

    private AbstractIssue getExistingIssue(String remoteIssueId){
        AbstractIssue existingIssue = getPreFilledGitlabIssue(remoteIssueId, 1L, "existing Issue");
        mockGettingIssueFromRepository(existingIssue);
        return existingIssue;
    }

    private AbstractIssue getParentContainerIssueIssue(AbstractIssue oldIssue){
        return getGitlabIssue(oldIssue.getRemoteIssueId(), null, null,
                oldIssue.getRepository(), null, null);
    }

    private AbstractIssue getGitlabIssue(String remoteIssueId, String title, Long id, AbstractRepository abstractRepository,
                                         String assignee, String oldDescription){
        GitlabIssue gitlabIssue = new GitlabIssue();
        gitlabIssue.setRemoteIssueId(remoteIssueId);
        gitlabIssue.setRepository(abstractRepository);
        gitlabIssue.setTitle(title);
        gitlabIssue.setId(id);
        gitlabIssue.setAssignee(assignee);
        gitlabIssue.setDescription(oldDescription);
        return gitlabIssue;
    }


}
