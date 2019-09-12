package com.redhat.entitiesTests;

import com.redhat.tasksyncer.Application;
import com.redhat.tasksyncer.dao.accessors.GithubRepositoryAccessor;
import com.redhat.tasksyncer.dao.accessors.GitlabRepositoryAccessor;
import com.redhat.tasksyncer.dao.accessors.RepositoryAccessor;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.GithubRepository;
import com.redhat.tasksyncer.dao.entities.GitlabRepository;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import static org.mockito.Mockito.when;



@RunWith(SpringRunner.class)
@ComponentScan("com.redhat.tasksyncer")
@SpringBootTest(classes = Application.class)
public class RepositoryAccessorTest {

    @Autowired
    AbstractRepositoryRepository repositoryRepository;
    @Autowired
    AbstractIssueRepository issueRepository;

    AbstractRepository githubRepository;
    AbstractRepository gitlabRepository;
    AbstractRepository unsuportedRepository;

    RepositoryAccessor repositoryAccessor;

    private String firstCredential = "login";
    private String secondCredential = "password";
    private String repoName = "repoName";
    private String repoNamespace = "repoNamespace";



    @Before
    public void setUp() throws RepositoryTypeNotSupportedException, IOException {
        githubRepository = AbstractRepository.newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace(IssueType.GITHUB,
                firstCredential, secondCredential, repoName, repoNamespace);

        gitlabRepository = AbstractRepository.newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace(IssueType.GITLAB,
                firstCredential, secondCredential, repoName, repoNamespace);

        unsuportedRepository = new UnsuportedRepository();

    }

    @Test
    public void whenPassingGitHubRepository_thenGitHubRepositoryAccessorShouldBeCreated() throws IOException, RepositoryTypeNotSupportedException {
        repositoryAccessor = RepositoryAccessor.getInstance(githubRepository, repositoryRepository, issueRepository);
        assertThat(repositoryAccessor).isInstanceOf(GithubRepositoryAccessor.class);
    }

    @Test
    public void whenPassingGitlabRepository_thenGitlabRepositoryAccessorShouldBeCreated() throws IOException, RepositoryTypeNotSupportedException {
        repositoryAccessor = RepositoryAccessor.getInstance(gitlabRepository, repositoryRepository, issueRepository);
        assertThat(repositoryAccessor).isInstanceOf(GitlabRepositoryAccessor.class);
    }

    @Test
    public void whenPassingUnsupportedRepository_thenExceptionIsThrown(){
        assertThatThrownBy(() ->
                RepositoryAccessor.getInstance(unsuportedRepository, repositoryRepository, issueRepository))
                .isInstanceOf(RepositoryTypeNotSupportedException.class);
    }
}

class UnsuportedRepository extends AbstractRepository{
    UnsuportedRepository(){
        super();
    }
}
