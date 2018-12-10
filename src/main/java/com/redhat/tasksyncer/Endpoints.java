package com.redhat.tasksyncer;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.RestTemplateHttpClient;
import com.redhat.tasksyncer.dao.CardRepository;
import com.redhat.tasksyncer.dao.IssueRepository;
import com.redhat.tasksyncer.dao.ProjectRepository;
import com.redhat.tasksyncer.dao.entities.Issue;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ProjectRepository projectRepository;


    /**
     * This endpoint is intended to be triggered manually when creating new project and blocks until everything is done
     */
    @RequestMapping(
            path = "/project/{projectName}/sync/trello/{trelloUsername}/{boardName}/with/gitlab/{namespace}/{repoName}",
            method = RequestMethod.PATCH
    )
    public String syncTrello(@PathVariable String projectName,
                             @PathVariable String trelloUsername,
                             @PathVariable String boardName,
                             @PathVariable String namespace,
                             @PathVariable String repoName
    ) throws IOException {
        GitlabAPI gl = GitlabAPI.connect(gitlabURL, gitlabAuthKey);
        GitlabProject project = gl.getProject(namespace, repoName);
        List<Issue> issues = gl.getIssues(project).stream().map(Issue::new).collect(Collectors.toList());

        issues.forEach(issue -> this.issueRepository.save(issue));

//        Trello t = new TrelloImpl(trelloApplicationKey, trelloAccessToken, new RestTemplateHttpClient());
//        Board b = t.createBoard("blah");
//
//        List<TList> lists = t.getBoardLists(b.getId());
//        for (Issue i : issues) {
//            TList l = lists.get(0);
//            Card c = new Card();
//            c.setName(i.getTitle());
//            c.setDesc(i.getDescription());
//            c = l.createCard(c);
//        }

        return OK;
    }
}
