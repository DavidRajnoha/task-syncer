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
import com.redhat.tasksyncer.exceptions.ProjectNotFoundException;
import com.redhat.tasksyncer.exceptions.TaskSyncerException;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabProject;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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

    @RequestMapping(path = "/sink")
    public String sink(HttpServletRequest request) {

        return OK;
    }

    /**
     * This endpoint is intended to be triggered manually when creating new project and blocks until everything is done
     */
    @RequestMapping(
            path = "/project/{projectName}/sync/to/trello/{trelloUsername}/{boardName}/from/gitlab/{namespace}/{repoName}",
            method = RequestMethod.PATCH
    )
    public String syncTrello(@PathVariable String projectName,
                             @PathVariable String trelloUsername,
                             @PathVariable String boardName,
                             @PathVariable String namespace,
                             @PathVariable String repoName
    ) throws IOException, ProjectNotFoundException {
        Project project = projectRepository.findProjectByName(projectName)
                .orElse(new Project(projectName));
//                .orElseThrow(() -> new ProjectNotFoundException("no such project"));

        if(project.getId() == null) {
            projectRepository.save(project); //assert unique name constraint worked, edit: for postgresql worked

            GitlabAPI gl = GitlabAPI.connect(gitlabURL, gitlabAuthKey);
            GitlabProject glProject = gl.getProject(namespace, repoName);
            List<Issue> issues = gl.getIssues(glProject).stream()
                    .map(Issue::new)
                    .peek(i -> i.setProject(project))
                    .peek(i -> this.issueRepository.save(i))
                    .collect(Collectors.toList());

            Trello t = new TrelloImpl(trelloApplicationKey, trelloAccessToken, new RestTemplateHttpClient());
            Board b = t.createBoard(boardName);
            List<TList> lists = t.getBoardLists(b.getId());
            TList l = lists.get(0);

            for(Issue i : issues) {
                com.julienvey.trello.domain.Card card = new com.julienvey.trello.domain.Card();
                card.setName(i.getTitle());
                card.setDesc(i.getDescription());

                card = l.createCard(card);

                Card c = new Card(card);
                i.setCard(c);
                c.setIssue(i);

                cardRepository.save(c);  // must be this order otherwise fails
                issueRepository.save(i);
            }
            return OK;
        }
//
//        GitlabAPI gl = GitlabAPI.connect(gitlabURL, gitlabAuthKey);
//        GitlabProject glProject = gl.getProject(namespace, repoName);
//
//        List<Issue> issues = gl.getIssues(glProject).stream()
//                .map(Issue::new)
//                .peek(i -> i.setProject(project))
//                .collect(Collectors.toList());
//
//        for(int i = 0; i < issues.size(); i++) {
//            Issue a = issueRepository.findByRidAndType(issues.get(i).getRid(), issues.get(i).getType()).ifPresent(b -> issues.set(i, b));
//        }




//        issues.forEach(issue -> this.issueRepository.save(issue));

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

    /**
     * This endpoint is intended to be triggered manually when creating new project and blocks until everything is done
     */
    @RequestMapping(
            path = "/project/new/{projectName}/trello/{trelloUsername}/{boardName}/with/gitlab/{namespace}/{repoName}",
            method = RequestMethod.POST
    )
    public String newProject(@PathVariable String projectName,
                             @PathVariable String trelloUsername,
                             @PathVariable String boardName,
                             @PathVariable String namespace,
                             @PathVariable String repoName
    ) throws TaskSyncerException {
//        if(projectRepository.findProjectByName(projectName).isPresent()) {
//            throw new ProjectNotFoundException("project already exists");  // not paralel safe if unique name constraint does not work, for postgres it works correctly
//        }

        try {
            Project project = new Project(projectName);
            projectRepository.save(project);
        } catch (DataIntegrityViolationException e) {
            if(e.contains(ConstraintViolationException.class))
                throw new TaskSyncerException("project with name already exists", e);
            else
                throw e;
        }

        return OK;
    }

    @RequestMapping(path = "/project/{projectName}/endpoint/new/gitlab/{namespace}/{repoName}/credentials/{credentialsID}")
    public String addEndpointGitlab(@PathVariable String projectName,
                                    @PathVariable String namespace,
                                    @PathVariable String repoName,
                                    @PathVariable long credentialsID
    ) throws ProjectNotFoundException {
        Project project = projectRepository.findProjectByName(projectName).orElseThrow(() -> new ProjectNotFoundException("no such project"));



        return OK;
    }
}
