package com.redhat.tasksyncer;


import com.redhat.tasksyncer.dao.accessors.ProjectAccessor;
import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.GitlabRepository;
import com.redhat.tasksyncer.dao.entities.Project;
import com.redhat.tasksyncer.dao.entities.TrelloCard;
import com.redhat.tasksyncer.dao.repositories.*;
import com.redhat.tasksyncer.decoders.GithubWebhookIssueDecoder;
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

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

    public Endpoints() {
    }


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

        ProjectAccessor projectAccessor = new ProjectAccessor(project, boardRepository, repositoryRepository, issueRepository, cardRepository, columnRepository, projectRepository, trelloApplicationKey, trelloAccessToken, gitlabURL, gitlabAuthKey,
                githubUserName, githubPassword);
        projectAccessor.update(newIssue);

        return OK;
    }


    //TODO: Move gitHubHook into the hook and determine which decoder to use
    @RequestMapping(path = "/github/project/{projectName}/hook",
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST
    )
    public String gitHubHook(@PathVariable String projectName,
                             HttpServletRequest request
    ) throws IOException {
        Project project = projectRepository.findProjectByName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project with name does not exist"));

        AbstractIssue newIssue = new GithubWebhookIssueDecoder().decode(request);
        ProjectAccessor projectAccessor = new ProjectAccessor(project, boardRepository, repositoryRepository, issueRepository, cardRepository, columnRepository, projectRepository, trelloApplicationKey, trelloAccessToken, gitlabURL, gitlabAuthKey,
                githubUserName, githubPassword);
        projectAccessor.update(newIssue);

        System.out.println("GitHubEventChanged");
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

        ProjectAccessor projectAccessor = new ProjectAccessor(project, boardRepository, repositoryRepository, issueRepository, cardRepository, columnRepository, projectRepository, trelloApplicationKey, trelloAccessToken, gitlabURL, gitlabAuthKey,
                githubUserName, githubPassword);
        projectAccessor.initialize(GitlabRepository.class.getName(), repoNamespace, repoName, TrelloCard.class.getName(), boardName);
        projectAccessor.doInitialSync();

        projectAccessor.save(); // todo: make it transactional

        return OK;
    }

    @RequestMapping(path = "/project/{projectName}/connect/github/{repoName}",
            method = RequestMethod.PUT
    )
    public String connectGithub(@PathVariable String projectName,
                                @PathVariable String repoName) throws IOException {
    Project project = projectRepository.findProjectByName(projectName)
            .orElseThrow(() -> new IllegalArgumentException("Project with name does not exist"));

        ProjectAccessor projectAccessor = new ProjectAccessor(project, boardRepository, repositoryRepository, issueRepository, cardRepository, columnRepository, projectRepository, trelloApplicationKey, trelloAccessToken, gitlabURL, gitlabAuthKey,
                githubUserName, githubPassword);

        URL githubWebhookURL = new URL(githubWebhookURLString);
        projectAccessor.connectGithub(githubWebhookURL,githubUserName + "/" + repoName);

    return OK;
    }

}
