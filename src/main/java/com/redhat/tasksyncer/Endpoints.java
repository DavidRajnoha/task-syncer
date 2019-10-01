package com.redhat.tasksyncer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.tasksyncer.dao.accessors.ProjectAccessor;
import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import com.redhat.tasksyncer.dao.repositories.*;
import com.redhat.tasksyncer.decoders.AbstractWebhookIssueDecoder;
import com.redhat.tasksyncer.decoders.GithubWebhookIssueDecoder;
import com.redhat.tasksyncer.decoders.GitlabWebhookIssueDecoder;

import com.redhat.tasksyncer.decoders.JiraWebhookIssueDecoder;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;
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
import java.util.List;
import java.util.Set;

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

    @Value("${gitlabWebhookURL}")
    private String gitlabWebhookURLString;

    @Value("${githubUsername}")
    private String githubUserName;

    @Value("${githubPassword}")
    private String githubPassword;

    @Value("${jiraUserName}")
    private String jiraUserName;

    @Value("${jiraPassword}")
    private String jiraPassword;

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


    /**
     *  Endpoint for processing webhooks
     * @param serviceName - name of the service you are trying to connect
     *                    Available values: "gitlab", "github, "jira"
     * @param projectName - name of the project in this app you want to have your webhooks send
     * */
    @RequestMapping(path = "/service/{serviceName}/project/{projectName}/hook",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST
    )
    public String hookEndpoint(@PathVariable String serviceName,
            @PathVariable String projectName,
                       HttpServletRequest request
    ) throws Exception {
        return processHook(projectName, request, serviceName);
    }

    //processing received webhook
    private String processHook(String projectName, HttpServletRequest request, String serviceType
    ) throws Exception {

        Project project = projectRepository.findProjectByName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project with name does not exist"));

        AbstractIssue newIssue = AbstractWebhookIssueDecoder.getInstance(serviceType).decode(request, project, repositoryRepository);

        ProjectAccessor projectAccessor = new ProjectAccessor(project, boardRepository, repositoryRepository, issueRepository, cardRepository, columnRepository, projectRepository, trelloApplicationKey, trelloAccessToken);
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

        ProjectAccessor projectAccessor = new ProjectAccessor(project, boardRepository, repositoryRepository, issueRepository, cardRepository, columnRepository, projectRepository, trelloApplicationKey, trelloAccessToken);
        projectAccessor.save();

        AbstractRepository repository = AbstractRepository.newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace(IssueType.GITLAB, gitlabURL, gitlabAuthKey, repoName, repoNamespace);

        projectAccessor.initialize(repository, TrelloCard.class.getName(), boardName);

        projectAccessor.save(); // todo: make it transactional

        return OK;
    }

    //TODO: fix creating multiplerepositories with same ne
    //TODO: Add more abstraction and merge with connectGithub
    //TODO: Create Gitlab webhook on connection
    @RequestMapping(path = "/project/{projectName}/connect/gitlab/{repoNamespace}/{repoName}",
            method = RequestMethod.PUT
    )
    public String connectGitlab(@PathVariable String projectName,
                                @PathVariable String repoName,
                                @PathVariable String repoNamespace) throws Exception {
        Project project = projectRepository.findProjectByName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project with name does not exist"));

        //Creates a projectAccessor and passes all components that has been autowired and values that has been defined here
        ProjectAccessor projectAccessor = new ProjectAccessor(project, boardRepository, repositoryRepository, issueRepository, cardRepository, columnRepository, projectRepository, trelloApplicationKey, trelloAccessToken);


        AbstractRepository repository = AbstractRepository.newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace(IssueType.GITLAB, gitlabURL, gitlabAuthKey, repoName, repoNamespace);
        //And also conducts synchronization of the gitlab issues with the local issueRepository and trello
        projectAccessor.addRepository(repository);

        return OK;
    }





    @RequestMapping(path = "/project/{projectName}/hook/gitlab/{repoNamespace}/{repoName}",
            method = RequestMethod.PUT
    )
    public String hookGitlab(@PathVariable String projectName,
                                @PathVariable String repoName,
                                @PathVariable String repoNamespace) throws Exception {
        Project project = projectRepository.findProjectByName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project with name does not exist"));

        //Creates a projectAccessor and passes all components that has been autowired and values that has been defined here
        ProjectAccessor projectAccessor = new ProjectAccessor(project, boardRepository, repositoryRepository, issueRepository, cardRepository, columnRepository, projectRepository, trelloApplicationKey, trelloAccessToken);


        AbstractRepository repository = AbstractRepository.newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace(IssueType.GITLAB, gitlabURL, gitlabAuthKey, repoName, repoNamespace);
        //And also conducts synchronization of the gitlab issues with the local issueRepository and trello

        projectAccessor.hookRepository(repository, gitlabWebhookURLString.replace("{projectName}", projectName));

        return OK;
    }



    @RequestMapping(path = "/project/{projectName}/connect/github/{repoName}",
            method = RequestMethod.PUT
    )
    public String connectGithub(@PathVariable String projectName,
                                @PathVariable String repoName) throws Exception {
    Project project = projectRepository.findProjectByName(projectName)
            .orElseThrow(() -> new IllegalArgumentException("Project with name does not exist"));

    //Creates a projectAccessor and passes all components that has been autowired and values that has been defined here
    ProjectAccessor projectAccessor = new ProjectAccessor(project, boardRepository, repositoryRepository, issueRepository, cardRepository, columnRepository, projectRepository, trelloApplicationKey, trelloAccessToken);

    AbstractRepository githubRepository = AbstractRepository.newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace(IssueType.GITHUB, githubUserName, githubPassword, repoName, githubUserName);

    //Creates a webhook to this app in the github repository based on the repoName PathVariable
    //TODO: assure that the webhook points to this app, now impossible due to ngrok
    //And also conducts synchronization of the github issues with the local issueRepository and trello
    projectAccessor.hookRepository(githubRepository, githubWebhookURLString);

    return OK;
    }


    //TODO: THIS WAS CTRL-Ced -> ABSTRACTIZE
    @RequestMapping(path = "/project/{projectName}/connect/jira/{repoNamespace}/{repoName}",
            method = RequestMethod.PUT
    )
    public String connectJira(@PathVariable String projectName,
                                @PathVariable String repoNamespace,
                                @PathVariable String repoName) throws Exception {
        Project project = projectRepository.findProjectByName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project with name does not exist"));

        //Creates a projectAccessor and passes all components that has been autowired and values that has been defined here
        ProjectAccessor projectAccessor = new ProjectAccessor(project, boardRepository, repositoryRepository, issueRepository, cardRepository, columnRepository, projectRepository, trelloApplicationKey, trelloAccessToken);

        AbstractRepository jiraRepository = AbstractRepository.newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace(IssueType.JIRA, jiraPassword, jiraUserName, repoName, repoNamespace);

        //Creates a webhook to this app in the github repository based on the repoName PathVariable
        //TODO: assure that the webhook points to this app, now impossible due to ngrok
        //And also conducts synchronization of the github issues with the local issueRepository and trello
        projectAccessor.addRepository(jiraRepository);

        return OK;
    }



    //GET REQUESTS

    @RequestMapping(path = "/project/{projectName}/all/{issueType}",
                    method = RequestMethod.GET
    )
    public String getIssuesByType(@PathVariable String projectName,
                                  @PathVariable IssueType issueType) throws JsonProcessingException {
        Set<AbstractIssue> issues = issueRepository.findByIssueType(issueType);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(issues);
    }

    @RequestMapping(path = "/project/{projectName}/all",
            method = RequestMethod.GET
    )
    public String getAllIssues(@PathVariable String projectName) throws JsonProcessingException {
        List<AbstractIssue> issues = (List<AbstractIssue>) issueRepository.findAll();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(issues);
    }

    @RequestMapping(path = "/project/{projectName}/repositories",
            method = RequestMethod.GET
    )
    public String getRepositoriesByProject(@PathVariable String projectName) throws JsonProcessingException {
        List<AbstractRepository> repositories = repositoryRepository.findByProject_Name(projectName);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(repositories);
    }

    @RequestMapping(path = "/project/{projectName}/{repositoryName}/{remoteIssueId}",
            method = RequestMethod.GET
    )
    public String getIssueByTypeAndRepositoryAndId(@PathVariable String projectName, @PathVariable String repositoryName,
                                                   @PathVariable String remoteIssueId) throws JsonProcessingException {
        List<AbstractIssue> issues = issueRepository
                .findByRepository_Project_nameAndRemoteIssueIdAndRepository_repositoryName(projectName, remoteIssueId, repositoryName);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(issues);
    }

}
