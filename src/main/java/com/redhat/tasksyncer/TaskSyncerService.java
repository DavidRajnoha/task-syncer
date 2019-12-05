package com.redhat.tasksyncer;


import com.redhat.tasksyncer.dao.accessors.ProjectAccessor;
import com.redhat.tasksyncer.dao.accessors.RepositoryAccessor;
import com.redhat.tasksyncer.dao.entities.*;
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
import java.util.*;

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
        Project project = doesProjectExist(projectName);

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
                                                Boolean trello, Boolean customMapping, List<String> columnNames,
                                                List<String> columnMappingKeys)
            throws RepositoryTypeNotSupportedException {
        if (projectRepository.findProjectByName(projectName).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Project with name " + projectName + "Already Exists");
        }

        // creates a new project
        Project project = new Project();
        project.setName(projectName);
        project.setColumnNames(columnNames);
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

        Map<String, String> columnMapping = null;

        if (customMapping) {
            try {
                columnMapping = getCustomColumnMapping(columnMappingKeys, columnNames);
            } catch (InvalidMappingException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }

        // adds repository to the project and syncs the remote issues with the local database
        try {
            projectAccessor.addRepository(repository, columnMapping);
        } catch (CannotConnectToRepositoryException e) {
            revertCreation(project);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't connect to the external service");
        } catch (InvalidMappingException e) {
            e.printStackTrace();
        }

        // saves the project
        projectAccessor.saveProject(project); // todo: make it transactional

        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

    private Map<String, String> getCustomColumnMapping(List<String> columnMappingKeys, List<String> columnNames) throws InvalidMappingException {
        Map<String, String> columnMapping = new LinkedHashMap<>();
        if (columnMappingKeys.size() <= columnNames.size()) {
            for (int i = 0; i < columnMappingKeys.size(); i++) {
                columnMapping.put(columnMappingKeys.get(i), columnNames.get(i));
            }
            return columnMapping;
        } else {
            throw new InvalidMappingException("The number of mapped parameters doesn't" +
                    " match the number of columns");
        }
    }

    public ResponseEntity<String> createProject(
            String projectName, String serviceType, String repoNamespace, String repoName,
            String boardName, String firstLoginCredential, String secondLoginCredential,
            Boolean trello) throws RepositoryTypeNotSupportedException {
        List<String> defaultColumnNames = new ArrayList<>();
        defaultColumnNames.add(AbstractColumn.TODO_DEFAULT);
        defaultColumnNames.add(AbstractColumn.DONE_DEFAULT);
        return createProject(projectName, serviceType, repoNamespace, repoName, boardName, firstLoginCredential,
                secondLoginCredential, trello, false, defaultColumnNames, null);
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


    public ResponseEntity<String> connectService(String projectName , String serviceName, String repoNamespace,
                                                 String repoName, String firstLoginCredential,
                                                 String secondLoginCredential, String hookOrConnect,
                                                 List<String> columnMappingKeys, List<String> customNames)
            throws RepositoryTypeNotSupportedException, CannotConnectToRepositoryException, SynchronizationFailedException {
        // Project with projectName must be already created
        Project project = doesProjectExist(projectName);

        //Initialize a projectAccessor - adds a project to the accessor and saves the project
        projectAccessor.saveProject(project);


        Map<String, String> columnMapping = new LinkedHashMap<>();
        if (columnMappingKeys !=null && customNames != null){
            try {
                // if not valid throws an exception
                areColumnNamesValid(customNames, project.getColumnNames());

                columnMapping = getCustomColumnMapping(columnMappingKeys, customNames);
            } catch (InvalidMappingException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }

        RepositoryAccessor repositoryAccessor = findRepositoryAccessor(serviceName);

        // Creates a repository of the serviceName type
        AbstractRepository repository = repositoryAccessor.createRepository(firstLoginCredential, secondLoginCredential, repoName, repoNamespace);

        // Adds the repository to the project and syncs the remoteIssues with local database
        try {
            repositoryAccessor = projectAccessor.addRepository(repository, columnMapping);
        } catch (InvalidMappingException e) {
            e.printStackTrace();
        }


        // Deciding if create hook or not based on the query parameters
        if (hookOrConnect.toLowerCase().equals("hook")) {
            try {
                // creates webhook pointing to local adress
                projectAccessor.hookRepository(repository,
                        githubWebhookURLString.replace("{projectName}", projectName), columnMapping);
            } catch (IOException | GitLabApiException e) {
                e.printStackTrace();
                repositoryAccessor.deleteRepository(repository);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create the webhook," +
                        " check if the webhook is not already created");
            } catch (InvalidMappingException e) {
                e.printStackTrace();
            }
        } else if (!hookOrConnect.toLowerCase().equals("connect")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid action \n Valid parameters are \"connect\" and \"hook\"");
        }

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    private void areColumnNamesValid(List<String> customNames, List<String> projectColumnNames) throws InvalidMappingException {
        for (String customName : customNames) {
            if (! projectColumnNames.contains(customName)){
                throw new InvalidMappingException("columns you wish to " +
                        "map to do not exist in this project");
            }
        }
    }

    public ResponseEntity<String> setColumnNames(String projectName, List<String> columnNames){
        Project project = doesProjectExist(projectName);
        projectAccessor.saveProject(project);
        projectAccessor.setColumnNames(columnNames);
        projectAccessor.save();

        return ResponseEntity.status(HttpStatus.OK).body("Column names set");
    }

    private Project doesProjectExist(String projectName) throws IllegalArgumentException{
        return projectRepository.findProjectByName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project with name " + projectName + " does not exist"));
    }

}
