package com.redhat.integration.projectAccessorTest;

import com.redhat.tasksyncer.Application;
import com.redhat.tasksyncer.dao.accessors.ProjectAccessorImpl;
import com.redhat.tasksyncer.dao.accessors.TrelloBoardAccessor;
import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.*;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@RunWith(SpringRunner.class)
@ComponentScan("com.redhat.tasksyncer")
@SpringBootTest(classes = Application.class)
@DataJpaTest
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
    private AbstractIssueRepository issueRepository;

    @Autowired
    private AbstractCardRepository cardRepository;

    @Autowired
    private AbstractColumnRepository columnRepository;

    private AbstractRepository githubRepository;
    private Project project;
    private ProjectAccessorImpl projectAccessor;
    private AbstractBoard trelloBoard;


    private String remoteIssueId = "1";
    private String title = "Github Issue";
    private String description = "Description";
    private String ghRepositoryName = "GithubRepository";
    private String updatedDescription = "UPDATED!!!";


    //REALY stuppid test because it connects to Trello and it is not the thing it should be testing!!!!!!
    @Before
    public void setup() throws RepositoryTypeNotSupportedException {
        project = new Project();

        TrelloBoard board = new TrelloBoard();
        board.setId(1L);
        board.setBoardName("Stupid Integration testing");

        TrelloBoardAccessor trelloBoardAccessor = new TrelloBoardAccessor(board, trelloApplicationKey, trelloAccessToken, boardRepository,
                cardRepository, columnRepository);

        trelloBoardAccessor.createItself();

        project.setBoard(board);


        githubRepository = AbstractRepository.newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace("github", "userName", "passwd", ghRepositoryName, "");
        repositoryRepository.save(githubRepository);

    }

    @Test
    public void contextLoads(){
    }

    @Test
    public void whenProjectAccessorUpdatesNewGithubIssue_thenNewGithubIssueIsSaved(){
        projectAccessor = new ProjectAccessorImpl(project, trelloApplicationKey, trelloAccessToken);

        AbstractIssue newGithubIssue = getNewGithubIssue();
        projectAccessor.syncIssue(newGithubIssue);
        AbstractIssue foundIssue = issueRepository.findOneByRemoteIssueId(remoteIssueId);
        assertThat(foundIssue).isNotNull();
        assertThat(foundIssue.getTitle()).isEqualTo(title);


        //Cleanup
        issueRepository.delete(foundIssue);
    }


    //Test works in issolation, problem with board Creation
    @Test
    public void whenProjectAccessorUpdatesExistingGithubIssue_thenIsTheIssueReallyUpdated() throws Exception {
        projectAccessor = new ProjectAccessorImpl(project, trelloApplicationKey, trelloAccessToken);


        AbstractIssue newGithubIssue = getNewGithubIssue();

        //Setup
        projectAccessor.syncIssue(newGithubIssue);

        //Test
        newGithubIssue.setDescription(updatedDescription);
        projectAccessor.syncIssue(newGithubIssue);


        AbstractIssue foundIssue = issueRepository.findByRemoteIssueIdAndRepository_repositoryName(remoteIssueId, ghRepositoryName).orElseThrow(() -> new Exception("No Issue found at second try"));

        //Assertion
        assertThat(foundIssue.getDescription()).isEqualTo(updatedDescription);

        //Cleanup
        issueRepository.delete(foundIssue);

    }



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

