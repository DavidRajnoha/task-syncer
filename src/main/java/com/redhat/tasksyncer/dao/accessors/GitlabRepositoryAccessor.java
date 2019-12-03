package com.redhat.tasksyncer.dao.accessors;

import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.InvalidMappingException;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.ProjectHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Filip Cap
 */
@Component
public class GitlabRepositoryAccessor extends RepositoryAccessor {


    private GitLabApi gitlabApi;

    @Autowired
    public GitlabRepositoryAccessor(AbstractRepositoryRepository repositoryRepository) {
        this.repositoryRepository = repositoryRepository;
    }

    @Override
    public void createWebhook(@NotNull String webhook) throws  GitLabApiException {
        ProjectHook projectHook = new ProjectHook();
        projectHook.setIssuesEvents(true);
        webhook = webhook.replace("{projectName}", repository.getProject().getName());
        gitlabApi.getProjectApi().addHook(repository.getRepositoryNamespace() + "%2F" + repository.getRepositoryName(), webhook, projectHook, false, gitlabApi.getSecretToken());
    }

    @Override
    public AbstractRepository createRepositoryOfType() {
        AbstractRepository gitlabRepository = new GitlabRepository();
        Map<String, String> defaultColumnMapping = new HashMap<>();

        defaultColumnMapping.put(AbstractIssue.STATE_OPENED, AbstractColumn.TODO_DEFAULT);
        defaultColumnMapping.put(AbstractIssue.STATE_REOPENED, AbstractColumn.TODO_DEFAULT);
        defaultColumnMapping.put(AbstractIssue.STATE_CLOSED, AbstractColumn.DONE_DEFAULT);

        gitlabRepository.setColumnMapping(defaultColumnMapping);

        return gitlabRepository;
    }


    @Override
    public Map<String, String> isMappingValid(Map<String, String> mapping) throws InvalidMappingException {
        if (mapping.size() != 3){
            throw new InvalidMappingException("The mapping for gitlab must contain exactly three entries");
        }

//        Boolean isValid = true;
//        mapping.keySet().forEach(key -> {
//            if (!key.equals(AbstractIssue.STATE_OPENED) && !key.equals(AbstractIssue.STATE_CLOSED) &&
//            !key.equals(AbstractIssue.STATE_REOPENED)) {
//                isValid
//            }
//            }
//        });

        for (String key : mapping.keySet()) {
            if (!key.equals(AbstractIssue.STATE_OPENED) && !key.equals(AbstractIssue.STATE_CLOSED) &&
                    !key.equals(AbstractIssue.STATE_REOPENED)) {
                throw new InvalidMappingException("The mapping contains unknown key: " + key);
            }
        }

        List<String> columnNames = repository.getProject().getColumnNames();
        for (String value : mapping.values()) {
            if (!columnNames.contains(value)) {
                throw new InvalidMappingException("The mapping contains non existing column: "
                        + value);
            }
        }

        return mapping;
    }


    @Override
    public void connectToRepository() {
        this.gitlabApi = getConnection(repository.getFirstLoginCredential(), Constants.TokenType.PRIVATE, repository.getSecondLoginCredential());
    }

    private GitLabApi getConnection(String firstLoginCredential, Constants.TokenType aPrivate, String secondLoginCredential) {
        return new GitLabApi(firstLoginCredential, aPrivate, secondLoginCredential);

    }

    @Override
    public List<AbstractIssue> downloadAllIssues() throws GitLabApiException {
        Project glProject = gitlabApi.getProjectApi()
                .getProject(this.repository.getRepositoryNamespace(), repository.getRepositoryName());

        Stream<Issue> issuesStream = gitlabApi.getIssuesApi()
                .getIssues(glProject,100)
                .stream();  // have to use pagination, we want all pages not just the first one

        // converts each issue
        return issuesStream
                .map(input -> GitlabIssue.ObjectToGitlabIssueConverter
                        .convert(input, repository.getColumnMapping()))
                .collect(Collectors.toList());
    }

}
