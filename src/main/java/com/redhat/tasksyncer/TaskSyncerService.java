package com.redhat.tasksyncer;


import com.redhat.tasksyncer.dao.accessors.ProjectAccessor;
import com.redhat.tasksyncer.dao.accessors.RepositoryAccessor;
import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.Project;
import com.redhat.tasksyncer.dao.entities.TrelloCard;
import com.redhat.tasksyncer.dao.repositories.ProjectRepository;
import com.redhat.tasksyncer.decoders.AbstractWebhookIssueDecoder;
import com.redhat.tasksyncer.exceptions.*;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Service
public class TaskSyncerService {
    @Value("${trello.appKey}")
    private String trelloApplicationKey;

    @Value("${trello.token}")
    private String trelloAccessToken;


    @Value("${githubWebhookURL}")
    private String githubWebhookURLString;

    @Value("${gitlabWebhookURL}")
    private String gitlabWebhookURLString;


    private final Map<String, RepositoryAccessor> repositoryAccessors;
    private final ProjectAccessor projectAccessor;
    private final Map<String, AbstractWebhookIssueDecoder> webhookIssueDecoderMap;
    private ProjectRepository projectRepository;

    @Autowired
    public TaskSyncerService(ProjectRepository projectRepository, Map<String, RepositoryAccessor> repositoryAccessors,
                             ProjectAccessor projectAccessor,
                Map<String, AbstractWebhookIssueDecoder> webhookIssueDecoderMap) {
            this.repositoryAccessors = repositoryAccessors;
            this.projectAccessor = projectAccessor;
            this.webhookIssueDecoderMap = webhookIssueDecoderMap;
            this.projectRepository = projectRepository;
        }


    public void processHook(String projectName, HttpServletRequest request, String serviceType
    ) throws RepositoryTypeNotSupportedException, TrelloCalllbackNotAboutCardException, InvalidWebhookCallbackException {
        // if project the hook is pointed at does not exists, throws error
        // TODO: Swap error for HTTP response
        Project project = projectRepository.findProjectByName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project with name does not exist"));

        AbstractWebhookIssueDecoder webhookIssueDecoder;
        webhookIssueDecoder = findWebhookIssueDecoder(serviceType);

        // converts webhook request to the abstractIssue
        AbstractIssue newIssue = webhookIssueDecoder.decode(request, project);

        // syncs the updated issue with the local database
        projectAccessor.saveProject(project);
        projectAccessor.syncIssue(newIssue);

    }


    private AbstractWebhookIssueDecoder findWebhookIssueDecoder(String serviceName) throws RepositoryTypeNotSupportedException {
        String serviceType = serviceName.toLowerCase().concat("WebhookIssueDecoder");
        AbstractWebhookIssueDecoder webhookIssueDecoder = webhookIssueDecoderMap.get(serviceType);
        if (webhookIssueDecoder == null){
            throw new RepositoryTypeNotSupportedException("Service type: " + serviceType + " not supported for processing" +
                    "webhooks ");
        }
        return webhookIssueDecoder;
    }


    public ResponseEntity<String> createProject(String projectName, String serviceType, String repoNamespace, String repoName,
                                                String boardName, String firstLoginCredential, String secondLoginCredential,
                                                Boolean trello) throws RepositoryTypeNotSupportedException {
        if (projectRepository.findProjectByName(projectName).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Project with name " + projectName + "Already Exists");
        }

        // creates a new project
        Project project = new Project();
        project.setName(projectName);
        projectAccessor.saveProject(project);


        // Creates repository accessor
        RepositoryAccessor repositoryAccessor = findRepositoryAccessor(serviceType);

        // Creates repository
        AbstractRepository repository = repositoryAccessor.createRepository(
                firstLoginCredential, secondLoginCredential, repoName, repoNamespace);


        // based on the trello parameter decides if create trello board or not
        if (trello) {
            try {
                projectAccessor.initialize(TrelloCard.class.getName(), boardName);
            } catch (HttpClientErrorException e){
                projectAccessor.deleteProject(project);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authentication with Trello failed");
            }
        }

        // adds repository to the project and syncs the remote issues with the local database
        try {
            projectAccessor.addRepository(repository);
        } catch (CannotConnectToRepositoryException e) {
            revertCreation(project);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't connect to the external service");
        }

        // saves the project
        projectAccessor.saveProject(project); // todo: make it transactional

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("OK");
    }

    private RepositoryAccessor findRepositoryAccessor(String serviceName) throws RepositoryTypeNotSupportedException {
        String serviceType = serviceName.toLowerCase().concat("RepositoryAccessor");
        RepositoryAccessor repositoryAccessor = repositoryAccessors.get(serviceType);
        if (repositoryAccessor == null){
            throw new RepositoryTypeNotSupportedException("Repo type: " + serviceType + " not supported");
        }

        return repositoryAccessor;
    }

    /**
     * deletes board from trello and project from tasksyncer, should clean changes in case of an error during creation of
     * the project
     * TODO: assert functionality and test thoroughly
     * */
    public void revertCreation(Project project){
        try {
            projectAccessor.deleteBoard(trelloApplicationKey, trelloAccessToken);
        } catch (CannotConnectToRepositoryException e) {
            e.printStackTrace();
        }
        projectAccessor.deleteProject(project);
    }


    public ResponseEntity<String> connectService(String projectName, String serviceName, String repoNamespace,
                                                 String repoName, String firstLoginCredential,
                                                 String secondLoginCredential, String hookOrConnect) throws RepositoryTypeNotSupportedException, CannotConnectToRepositoryException, SynchronizationFailedException {
        // Project with projectName must be already created
        Project project = projectRepository.findProjectByName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project with name does not exist"));

        //Initialize a projectAccessor - adds a project to the accessor and saves the project
        projectAccessor.saveProject(project);

        RepositoryAccessor repositoryAccessor = findRepositoryAccessor(serviceName);

        // Creates a repository of the serviceName type
        AbstractRepository repository = repositoryAccessor.createRepository(firstLoginCredential, secondLoginCredential, repoName, repoNamespace);

        // Adds the repository to the project and syncs the remoteIssues with local database
        repositoryAccessor = projectAccessor.addRepository(repository);



        // Deciding if create hook or not based on the query parameters
        if (hookOrConnect.toLowerCase().equals("hook")) {
            try {
                // creates webhook pointing to local adress
                projectAccessor.hookRepository(repository, githubWebhookURLString.replace("{projectName}", projectName));
            } catch (IOException | GitLabApiException e) {
                e.printStackTrace();
                repositoryAccessor.deleteRepository(repository);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create the webhook," +
                        " check if the webhook is not already created");
            }
        } else if (!hookOrConnect.toLowerCase().equals("connect")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid action \n Valid parameters are \"connect\" and \"hook\"");
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("");
    }

}
