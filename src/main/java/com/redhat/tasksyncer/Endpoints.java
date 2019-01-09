package com.redhat.tasksyncer;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.RestTemplateHttpClient;
import com.redhat.tasksyncer.dao.CardRepository;
import com.redhat.tasksyncer.dao.IssueRepository;
import com.redhat.tasksyncer.dao.ProjectRepository;
import com.redhat.tasksyncer.dao.entities.Card;
import com.redhat.tasksyncer.dao.entities.Issue;
import com.redhat.tasksyncer.dao.entities.Project;
import com.redhat.tasksyncer.decoders.GitlabWebhookIssueDecoder;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PostConstruct
    public void init() {
        gitlabApi = new GitLabApi(gitlabURL, Constants.TokenType.PRIVATE, gitlabAuthKey);
        trelloApi = new TrelloImpl(trelloApplicationKey, trelloAccessToken, new RestTemplateHttpClient());
    }

    @RequestMapping(path = "/project/{projectName}/hook/from/gitlab/{namespace}/{repoName}/to/trello/{boardName}",
                    consumes = MediaType.APPLICATION_JSON_VALUE)
    public String hook(@PathVariable String projectName,
                       @PathVariable String namespace,
                       @PathVariable String repoName,
                       @PathVariable String boardName,
                       HttpServletRequest request
    ) throws GitLabApiException {
        Project project = projectRepository.findProjectByName(projectName)
                .orElse(new Project(projectName));

        if(project.getId() == null) {  // is it new project?
            // ignore request data, because we are downloading all issues

            project = projectRepository.save(project);  //todo ensure nothing is being done with project variable

            org.gitlab4j.api.models.Project glProject = gitlabApi.getProjectApi().getProject(namespace, repoName);

            Stream<org.gitlab4j.api.models.Issue> issuesStream = gitlabApi.getIssuesApi().getIssues(glProject,100).stream();  // have to use pagination, we want all pages not just the first one

            Project finalProject = project;
            List<Issue> issues = issuesStream
                    .map(Issue::new)
                    .peek(i -> i.setProject(finalProject))
                    .collect(Collectors.toList());

            // todo: make procedure for creating exact trello board
            Board b = trelloApi.createBoard(boardName);
            project.setBoardId(b.getId());

            List<TList> lists = trelloApi.getBoardLists(b.getId());
            TList list = lists.get(0);  // todo: add card to proper column according to state

            for(Issue i : issues) {
                com.julienvey.trello.domain.Card trelloCard = new com.julienvey.trello.domain.Card();
                trelloCard.setName(i.getTitle());
                trelloCard.setDesc(i.getDescription());

                trelloCard = list.createCard(trelloCard);

                Card c = new Card(trelloCard);
                c = cardRepository.save(c);  // must be this order otherwise fails
                i.setCard(c); // issue is owner of issue-card join

                issueRepository.save(i);
            }



        } else { // not new project
            GitlabWebhookIssueDecoder d = new GitlabWebhookIssueDecoder();
            Issue i = d.decode(request);

            Issue old = issueRepository.findByRidAndType(i.getRid(), i.getType())
                    .orElse(i);

            if(old.getId() != null) {  // there exists such issue
                old.updateLocally(i);
                old.getCard().updateLocally(new Card(i));
                old = issueRepository.save(old);  // todo: it should also save card entity

                Board b = trelloApi.getBoard(project.getBoardId());
                com.julienvey.trello.domain.Card trelloCard = new com.julienvey.trello.domain.Card();
                Card myCard = old.getCard();

                trelloCard.setId(myCard.getCuid());
                trelloCard.setDesc(myCard.getDescription());
                trelloCard.setName(myCard.getTitle());

                trelloApi.updateCard(trelloCard);

            } else {  // its new issue


                i.setProject(project);

                com.julienvey.trello.domain.Card trelloCard = new com.julienvey.trello.domain.Card();
                trelloCard.setName(i.getTitle());
                trelloCard.setDesc(i.getDescription());

                List<TList> lists = trelloApi.getBoardLists(project.getBoardId());
                TList list = lists.get(0);  // todo: add card to proper column according to state
                trelloCard = list.createCard(trelloCard);

                Card c = new Card(trelloCard);

                c = cardRepository.save(c);  // must be this order otherwise fails
                i.setCard(c); // issue is owner of issue-card join

                i = issueRepository.save(i);


            }
        }
        return OK;
    }

}
