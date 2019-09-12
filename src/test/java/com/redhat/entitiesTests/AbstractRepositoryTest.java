package com.redhat.entitiesTests;

import com.redhat.tasksyncer.Application;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.GithubRepository;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@RunWith(SpringRunner.class)
@ComponentScan("com.redhat.tasksyncer")
@SpringBootTest(classes = Application.class)
public class AbstractRepositoryTest {
    private String firstCredential = "login";
    private String secondCredential = "password";
    private String repoName = "repoName";
    private String repoNamespace = "repoNamespace";


    @Test
    public void whenNewInstanceOfTypeGITHUBisCalled_thenGitHubRepositoryIsCreated() throws RepositoryTypeNotSupportedException {
        AbstractRepository repository = AbstractRepository
                .newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace(IssueType.GITHUB, firstCredential,
                        secondCredential, repoName, repoNamespace);

        assertThat(repository).isInstanceOf(GithubRepository.class);
        assertThat(repository.getFirstLoginCredential()).isEqualTo(firstCredential);
        assertThat(repository.getSecondLoginCredential()).isEqualTo(secondCredential);
        assertThat(repository.getRepositoryNamespace()).isEqualTo(repoNamespace);
        assertThat(repository.getRepositoryName()).isEqualTo(repoName);
    }

    @Test
    public void whenNewInstanceOfTypeGITLABisCalled_thenGitLabRepositoryIsCreated() throws RepositoryTypeNotSupportedException {
        AbstractRepository repository = AbstractRepository
                .newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace(IssueType.GITHUB, firstCredential,
                        secondCredential, repoName, repoNamespace);
        assertThat(repository).isInstanceOf(GithubRepository.class);
    }

    @Test
    public void whenNewInstanceOfUnknownTypeIsCalled_thenRepositoryTypeNotSupportedExceptionIsThrwon() {
        assertThatThrownBy(() ->
                AbstractRepository.newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace(IssueType.TRELLO, firstCredential,
                        secondCredential, repoName, repoNamespace)).isInstanceOf(RepositoryTypeNotSupportedException.class);

    }


    @Test
    public void whenNewInstanceOfIsCalled_thenAllFieldsArecorrectlySet() throws RepositoryTypeNotSupportedException {
        AbstractRepository repository = AbstractRepository
                .newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace(IssueType.GITHUB, firstCredential,
                        secondCredential, repoName, repoNamespace);

        assertThat(repository.getFirstLoginCredential()).isEqualTo(firstCredential);
        assertThat(repository.getSecondLoginCredential()).isEqualTo(secondCredential);
        assertThat(repository.getRepositoryNamespace()).isEqualTo(repoNamespace);
        assertThat(repository.getRepositoryName()).isEqualTo(repoName);
    }


}
