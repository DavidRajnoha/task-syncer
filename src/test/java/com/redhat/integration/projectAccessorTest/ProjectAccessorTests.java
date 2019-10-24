package com.redhat.integration.projectAccessorTest;

import com.redhat.tasksyncer.dao.accessors.ProjectAccessor;
import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.*;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

//@RunWith(SpringRunner.class)
//@ComponentScan("com.redhat.tasksyncer")
//@SpringBootTest(classes = Application.class)
//@DataJpaTest
@RunWith(MockitoJUnitRunner.class)
public class ProjectAccessorTests {

    @Value("${gitlabURL}")
    private String gitlabURL;

    @Value("${gitlabAuthKey}")
    private String gitlabAuthKey;

    @Value("${trello.appKey}")
    private String trelloApplicationKey;

    @Value("${trello.token}")
    private String trelloAccessToken;

    @Value("${githubWebhookURL}")
    private String githubWebhookURLString;

    @Value("${githubUsername}")
    private String githubUserName;

    @Value("${githubPassword}")
    private String githubPassword;

    @Autowired
    private AbstractBoardRepository boardRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AbstractRepositoryRepository repositoryRepository;

    @Autowired
    private AbstractCardRepository cardRepository;

    @Autowired
    private AbstractColumnRepository columnRepository;

    private AbstractRepository githubRepository;
    private Project project;
    private ProjectAccessor projectAccessor;
    private AbstractBoard trelloBoard;

    @InjectMocks
    private AbstractIssueRepository issueRepository = Mockito.mock(AbstractIssueRepository.class);


    private String remoteIssueId = "1";
    private String title = "Github Issue";
    private String description = "Description";
    private String ghRepositoryName = "GithubRepository";
    private String updatedDescription = "UPDATED!!!";


    //REALY stuppid test because it connects to Trello and it is not the thing it should be testing!!!!!!
    @Before
    public void setup() throws RepositoryTypeNotSupportedException {
        project = new Project();

        githubRepository = AbstractRepository.newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace("github", "userName", "passwd", ghRepositoryName, "");
       // repositoryRepository.save(githubRepository);

    }

    @Test
    public void contextLoads(){
    }

 /* Stubbing with mockito on spyProjectAccessorIsNotWorking, see if it will work on projectAccessorImpl

    @Test
    public void whenProjectAccessorUpdatesNewGithubIssue_thenNewGithubIssueIsSaved(){
        AbstractIssue newGithubIssue = getNewGithubIssue();

        Mockito.when(issueRepository.findByRemoteIssueIdAndRepository_repositoryName(newGithubIssue.getRemoteIssueId(),
                newGithubIssue.getRepository().getRepositoryName())).thenReturn(Optional.of(newGithubIssue));

        ArgumentCaptor<AbstractIssue> argument = ArgumentCaptor.forClass(AbstractIssue.class);


        projectAccessor = new ProjectAccessor(project, boardRepository, repositoryRepository, issueRepository, cardRepository, columnRepository, projectRepository, trelloApplicationKey, trelloAccessToken);
        ProjectAccessor spyProjectAccessor = Mockito.spy(projectAccessor);

        Mockito.doReturn(newGithubIssue).when(spyProjectAccessor).setCard(newGithubIssue);
        Mockito.doReturn(newGithubIssue).when(spyProjectAccessor).updateCard(newGithubIssue);


        spyProjectAccessor.syncIssue(newGithubIssue);


        AbstractIssue foundIssue = argument.getValue();
        assertThat(foundIssue).isNotNull();
        assertThat(foundIssue.getTitle()).isEqualTo(title);

        Mockito.verify(issueRepository).save(argument.capture());

    }*/


//    //Test works in issolation, problem with board Creation
//    @Test
//    public void whenProjectAccessorUpdatesExistingGithubIssue_thenIsTheIssueReallyUpdated() throws Exception {
//        projectAccessor = new ProjectAccessor(project, boardRepository, repositoryRepository, issueRepository, cardRepository, columnRepository, projectRepository, trelloApplicationKey, trelloAccessToken);
//
//
//        AbstractIssue newGithubIssue = getNewGithubIssue();
//
//        //Setup
//        projectAccessor.syncIssue(newGithubIssue);
//
//        //Test
//        newGithubIssue.setDescription(updatedDescription);
//        projectAccessor.syncIssue(newGithubIssue);
//
//
//        AbstractIssue foundIssue = issueRepository.findByRemoteIssueIdAndRepository_repositoryName(remoteIssueId, ghRepositoryName).orElseThrow(() -> new Exception("No Issue found at second try"));
//
//        //Assertion
//        assertThat(foundIssue.getDescription()).isEqualTo(updatedDescription);
//
//        //Cleanup
//        issueRepository.delete(foundIssue);
//
//    }



    private AbstractIssue getNewGithubIssue(){
        AbstractIssue newGithubIssue = new GithubIssue();
        newGithubIssue.setRemoteIssueId(remoteIssueId);
        newGithubIssue.setTitle(title);
        newGithubIssue.setDescription(description);
        newGithubIssue.setState(AbstractIssue.STATE_OPENED);
        newGithubIssue.setRepository(githubRepository);

        return newGithubIssue;
    }



}

