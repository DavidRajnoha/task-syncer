package com.redhat.unit.ServiceTests;

import com.redhat.tasksyncer.TaskSyncerService;
import com.redhat.tasksyncer.dao.accessors.JiraRepositoryAccessor;
import com.redhat.tasksyncer.dao.accessors.ProjectAccessor;
import com.redhat.tasksyncer.dao.accessors.RepositoryAccessor;
import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.ProjectRepository;
import com.redhat.tasksyncer.decoders.AbstractWebhookIssueDecoder;
import com.redhat.tasksyncer.decoders.JiraWebhookIssueDecoder;
import com.redhat.tasksyncer.exceptions.*;
import org.gitlab4j.api.GitLabApiException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@RunWith(MockitoJUnitRunner.class)
public class TaskSyncerTests {

    private TaskSyncerService service;

    @Mock
    private ProjectAccessor projectAccessor;

    private  Map<String, AbstractWebhookIssueDecoder> webhookIssueDecoderMap = new HashMap<>();
    private Map<String, RepositoryAccessor> repositoryAccessors = new HashMap<>();


    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private RepositoryAccessor abstractRepositoryAccessor = Mockito.mock(JiraRepositoryAccessor.class);

    @Mock
    private AbstractWebhookIssueDecoder abstractWebhookIssueDecoder = Mockito.mock(JiraWebhookIssueDecoder.class);

    @Mock
    private JiraRepositoryAccessor jiraRepositoryAccessor;


    MockHttpServletRequest servletRequest;

    String projectName = "projectName";
    String issueTitle = "issueTitle";
    Project project = new Project();
    AbstractIssue issue = new JiraIssue();
    AbstractRepository jiraRepository = new JiraRepository();
    private String fst = "firstLoginCredential";
    private String snd = "secondLoginCredential";
    private String repoName = "repoName";
    private String repoNmsp = "repoNamespace";


    @Before
    public void setup() throws TrelloCalllbackNotAboutCardException, InvalidWebhookCallbackException, RepositoryTypeNotSupportedException, CannotConnectToRepositoryException {
        issue.setTitle(issueTitle);

        servletRequest = new MockHttpServletRequest();

        Mockito.when(abstractWebhookIssueDecoder.decode(servletRequest, project)).thenReturn(issue);
        Mockito.when(projectRepository.findProjectByName(projectName)).thenReturn(Optional.of(project));
        Mockito.when(jiraRepositoryAccessor.createRepository(fst, snd, repoName, repoNmsp))
                .thenReturn(jiraRepository);

        jiraRepository.setRepositoryName(repoName);
        jiraRepository.setRepositoryNamespace(repoNmsp);

        project.setName(projectName);

        webhookIssueDecoderMap.put("jiraWebhookIssueDecoder", abstractWebhookIssueDecoder);

        repositoryAccessors.put("jiraRepositoryAccessor", jiraRepositoryAccessor);

        service = new TaskSyncerService(projectRepository, repositoryAccessors, projectAccessor, webhookIssueDecoderMap);
        setField(service, "githubWebhookURLString", "url/projectName/url");

        Mockito.when(projectAccessor.addRepository(jiraRepository)).thenReturn(jiraRepositoryAccessor);

    }

    @Test
    public void processingWebhookRequestSyncsIssue() throws TrelloCalllbackNotAboutCardException, InvalidWebhookCallbackException, RepositoryTypeNotSupportedException {
        // Set up
        ArgumentCaptor<AbstractIssue> argumentCaptor = ArgumentCaptor.forClass(AbstractIssue.class);



        // Test
        service.processHook(projectName, servletRequest, "Jira");


        // Assertion
        Mockito.verify(projectAccessor, Mockito.times(1)).syncIssue(argumentCaptor.capture());

        AbstractIssue foundIssue = argumentCaptor.getValue();

        assertThat(foundIssue.getClass().getSimpleName()).isEqualTo("JiraIssue");
        assertThat(foundIssue.getTitle()).isEqualTo(issueTitle);
    }


    @Test(expected = RepositoryTypeNotSupportedException.class)
    public void processingWebhookUnsupportedRepository() throws TrelloCalllbackNotAboutCardException, InvalidWebhookCallbackException, RepositoryTypeNotSupportedException {
        service.processHook(projectName, servletRequest, "UnsupportedServiceType");
    }

    @Test
    public void createProject_ShouldCreateCorrectProject() throws RepositoryTypeNotSupportedException, CannotConnectToRepositoryException {

        ArgumentCaptor<AbstractRepository> argumentCaptor = ArgumentCaptor.forClass(AbstractRepository.class);
        ArgumentCaptor<Project> projectArgumentCaptor = ArgumentCaptor.forClass(Project.class);


        service.createProject("new Project", "jira", repoNmsp, repoName, "boardName",
                fst, snd, false);


        Mockito.verify(projectAccessor, Mockito.times(1)).addRepository(argumentCaptor.capture());
        Mockito.verify(projectAccessor, Mockito.times(2)).saveProject(projectArgumentCaptor.capture());


        AbstractRepository savedRepository = argumentCaptor.getValue();
        Project foundProject = projectArgumentCaptor.getValue();

        assertThat(foundProject.getName()).isEqualTo("new Project");
    }


    @Test(expected = RepositoryTypeNotSupportedException.class)
    public void createProjectUnsupportedService_throwsException() throws RepositoryTypeNotSupportedException {
        service.createProject("random name", "UnsupportedServiceType", repoNmsp,
                repoName,"boardName", fst, snd, false);
    }


    @Test
    public void connectJiraService_ConnectsService() throws SynchronizationFailedException, CannotConnectToRepositoryException, RepositoryTypeNotSupportedException {
        ArgumentCaptor<AbstractRepository> argumentCaptor = ArgumentCaptor.forClass(AbstractRepository.class);
        ArgumentCaptor<Project> projectArgumentCaptor = ArgumentCaptor.forClass(Project.class);

        service.connectService(projectName, "jira", repoNmsp, repoName,
                fst, snd, "connect");


        Mockito.verify(projectAccessor, Mockito.times(1)).addRepository(argumentCaptor.capture());
        Mockito.verify(projectAccessor, Mockito.times(1)).saveProject(projectArgumentCaptor.capture());

        AbstractRepository foundRepository = argumentCaptor.getValue();
        Project foundProject = projectArgumentCaptor.getValue();

        assertThat(foundRepository.getRepositoryName()).isEqualTo(repoName);
        assertThat(foundProject.getName()).isEqualTo(projectName);
    }


    @Test
    public void hookJiraServiceInvokesTheCallForHook() throws SynchronizationFailedException,
            CannotConnectToRepositoryException, RepositoryTypeNotSupportedException, IOException, GitLabApiException {
        ArgumentCaptor<AbstractRepository> argumentCaptor = ArgumentCaptor.forClass(AbstractRepository.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        service.connectService(projectName, "jira", repoNmsp, repoName, fst, snd, "hook");

        Mockito.verify(projectAccessor, Mockito.times(1)).hookRepository(argumentCaptor.capture(),
                stringArgumentCaptor.capture());
    }

    @Test
    public void neitherHookNorConnectService_thenErrorHttpCodeIsReturned() throws SynchronizationFailedException, CannotConnectToRepositoryException, RepositoryTypeNotSupportedException {
        ResponseEntity responseEntity = service.connectService(projectName, "jira", repoNmsp, repoName, fst,
                snd, "neitherHookOrConnect");
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }
}
