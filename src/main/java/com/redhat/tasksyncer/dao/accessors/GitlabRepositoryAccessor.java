package com.redhat.tasksyncer.dao.accessors;

import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.GitlabIssue;
import com.redhat.tasksyncer.dao.entities.GitlabRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.ProjectHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.validation.constraints.NotNull;
import java.util.List;
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
        return new GitlabRepository();
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
                .map(GitlabIssue.ObjectToGitlabIssueConverter::convert)
                .collect(Collectors.toList());
    }

}
