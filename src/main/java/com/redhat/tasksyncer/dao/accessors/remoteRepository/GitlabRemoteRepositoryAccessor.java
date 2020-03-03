package com.redhat.tasksyncer.dao.accessors.remoteRepository;

import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.issues.GitlabIssue;
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
public class GitlabRemoteRepositoryAccessor extends RemoteRepositoryAccessor {


    private GitLabApi gitlabApi;

    @Autowired
    public GitlabRemoteRepositoryAccessor() {
    }


    @Override
    public void createWebhook(@NotNull String webhook) throws  GitLabApiException {
        ProjectHook projectHook = new ProjectHook();
        projectHook.setIssuesEvents(true);
        webhook = webhook.replace("{projectName}", repository.getProject().getName());
        gitlabApi.getProjectApi().addHook(repository.getRepositoryNamespace() + "%2F" + repository.getRepositoryName(), webhook, projectHook, false, gitlabApi.getSecretToken());
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
                .peek(issue -> issue.setRepository(repository))
                .collect(Collectors.toList());
    }

}
