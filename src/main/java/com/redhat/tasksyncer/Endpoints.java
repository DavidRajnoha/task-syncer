package com.redhat.tasksyncer;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.RestTemplateHttpClient;
import com.redhat.tasksyncer.dao.CardRepository;
import com.redhat.tasksyncer.dao.IssueRepository;
import com.redhat.tasksyncer.dao.ProjectRepository;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.Card;
import com.redhat.tasksyncer.dao.entities.Issue;
import com.redhat.tasksyncer.dao.entities.Project;
import com.redhat.tasksyncer.decoders.GitlabWebhookIssueDecoder;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Filip Cap
 */
@RestController
@PropertySource("classpath:other.properties")
public class Endpoints {
    public static final String OK = "OK";

    @Value("${gitlabURL}")
    private String gitlabURL;

    @Value("${gitlabAuthKey}")
    private String gitlabAuthKey;

    @Value("${trello.appKey}")
    private String trelloApplicationKey;

    @Value("${trello.token}")
    private String trelloAccessToken;

    // todo not MT safe
    private GitLabApi gitlabApi;
    private Trello trelloApi;  // RestTemplateHttpClient should use builtin spring http client
    // end todo: not MT safe


    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AutowireCapableBeanFactory factory;

    @PostConstruct
    public void init() {
        gitlabApi = new GitLabApi(gitlabURL, Constants.TokenType.PRIVATE, gitlabAuthKey);
        trelloApi = new TrelloImpl(trelloApplicationKey, trelloAccessToken, new RestTemplateHttpClient());
    }


    private void createProjectAndInitBoard(Project project) throws Exception {
        project = projectRepository.save(project);
        Project finalProject = project;

        // todo: rework to remove type casting
        List<Issue> issues = project.getRepository().getIssues().stream().peek(i -> i.setRepository((AbstractRepository)finalProject.getRepository())).collect(Collectors.toList());

        // todo: make procedure for creating exact trello board
        project.createBoard();

        for(Issue i : issues) {
            Card c = project.getBoard().update(new Card(i));  // todo use generic converter

            c = cardRepository.save(c);  // must be this order otherwise fails
            i.setCard(c); // issue is owner of issue-card join

            issueRepository.save(i);
        }
    }

    private void updateBoard(Project project, HttpServletRequest request) throws GitLabApiException {
        GitlabWebhookIssueDecoder d = new GitlabWebhookIssueDecoder();
        Issue i = d.decode(request);

        Issue old = issueRepository.findByRidAndType(i.getRid(), i.getType())
                .orElse(i);

        if(old.getId() != null) {  // there exists such issue
            old.updateLocally(i);
            old.getCard().updateLocally(new Card(i));
            old = issueRepository.save(old);  // todo: it should also save card entity

            Board b = trelloApi.getBoard(project.getBoard().getBoardId());
            com.julienvey.trello.domain.Card trelloCard = new com.julienvey.trello.domain.Card();
            Card myCard = old.getCard();

            trelloCard.setId(myCard.getCuid());
            trelloCard.setDesc(myCard.getDescription());
            trelloCard.setName(myCard.getTitle());

            trelloApi.updateCard(trelloCard);

        } else {  // its new issue


            i.setRepository((AbstractRepository) project.getRepository());  // todo: rework to remove type casting

            com.julienvey.trello.domain.Card trelloCard = new com.julienvey.trello.domain.Card();
            trelloCard.setName(i.getTitle());
            trelloCard.setDesc(i.getDescription());

            List<TList> lists = trelloApi.getBoardLists(project.getBoard().getBoardId());
            TList list = lists.get(0);  // todo: add card to proper column according to state
            trelloCard = list.createCard(trelloCard);

            Card c = new Card(trelloCard);

            c = cardRepository.save(c);  // must be this order otherwise fails
            i.setCard(c); // issue is owner of issue-card join

            i = issueRepository.save(i);
        }
    }


    @RequestMapping(path = "/project/{projectName}/hook/from/gitlab/{repoNamespace}/{repoName}/to/trello/{boardName}",
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST
    )
    public String hook(@PathVariable String projectName,
                       @PathVariable String repoNamespace,
                       @PathVariable String repoName,
                       @PathVariable String boardName,
                       HttpServletRequest request
    ) throws GitLabApiException {

        Project project = projectRepository.findProjectByName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project with name does not exist"));

        updateBoard(project, request);
        return OK;
    }


    @RequestMapping(path = "/project/new/{projectName}/from/gitlab/{repoNamespace}/{repoName}/to/trello/{boardName}",
                    method = RequestMethod.PUT
    )
    public String createProject(@PathVariable String projectName,
                                @PathVariable String repoNamespace,
                                @PathVariable String repoName,
                                @PathVariable String boardName
    ) throws Exception {
        projectRepository.findProjectByName(projectName).ifPresent(p -> {
            throw new IllegalArgumentException("Project with name already exists");
        });

        Project project = factory.createBean(Project.class);

        project.initialize(factory, projectName, Issue.GITLAB_ISSUE, repoNamespace, repoName, Card.TRELLO_CARD, boardName);
        createProjectAndInitBoard(project);

        return OK;
    }

}
