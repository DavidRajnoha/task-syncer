package com.redhat.tasksyncer;


import com.redhat.tasksyncer.dao.accessors.ProjectAccessor;
import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.GitlabRepository;
import com.redhat.tasksyncer.dao.entities.Project;
import com.redhat.tasksyncer.dao.entities.TrelloCard;
import com.redhat.tasksyncer.dao.repositories.*;
import com.redhat.tasksyncer.decoders.GitlabWebhookIssueDecoder;

import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Filip Cap
 */
@RestController
@PropertySource("classpath:other.properties")
@ComponentScan(basePackages = "com.redhat.tasksyncer.dao.entities")
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
    private AbstractBoardRepository boardRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AbstractRepositoryRepository repositoryRepository;

    @Autowired
    private AbstractIssueRepository issueRepository;

    @Autowired
    private AbstractCardRepository cardRepository;



    @RequestMapping(path = "/project/{projectName}/hook",
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST
    )
    public String hook(@PathVariable String projectName,
                       HttpServletRequest request
    ) throws GitLabApiException {

        Project project = projectRepository.findProjectByName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project with name does not exist"));

        // todo: determine which decoder to use
        AbstractIssue newIssue = new GitlabWebhookIssueDecoder().decode(request);

        ProjectAccessor projectAccessor = new ProjectAccessor(project, boardRepository, repositoryRepository, issueRepository, cardRepository, projectRepository, trelloApplicationKey, trelloAccessToken, gitlabURL, gitlabAuthKey);
        projectAccessor.update(newIssue);

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

        Project project = new Project();
        project.setName(projectName);

        ProjectAccessor projectAccessor = new ProjectAccessor(project, boardRepository, repositoryRepository, issueRepository, cardRepository, projectRepository, trelloApplicationKey, trelloAccessToken, gitlabURL, gitlabAuthKey);
        projectAccessor.initialize(GitlabRepository.class.getName(), repoNamespace, repoName, TrelloCard.class.getName(), boardName);
        projectAccessor.doInitialSync();

        projectAccessor.save(); // todo: make it transactional

        return OK;
    }

}
