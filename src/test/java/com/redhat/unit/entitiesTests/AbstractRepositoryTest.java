package com.redhat.unit.entitiesTests;

import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.GithubRepository;
import com.redhat.tasksyncer.dao.entities.GitlabRepository;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


public class AbstractRepositoryTest {
    private String firstCredential = "login";
    private String secondCredential = "password";
    private String repoName = "repoName";
    private String repoNamespace = "repoNamespace";


    @Test
    public void whenNewInstanceOfTypeGITHUBisCalled_thenGitHubRepositoryIsCreated() throws RepositoryTypeNotSupportedException {
        AbstractRepository repository = AbstractRepository
                .newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace("github", firstCredential,
                        secondCredential, repoName, repoNamespace);

        assertThat(repository).isInstanceOf(GithubRepository.class);
    }

    @Test
    public void whenNewInstanceOfTypeGITLABisCalled_thenGitLabRepositoryIsCreated() throws RepositoryTypeNotSupportedException {
        AbstractRepository repository = AbstractRepository
                .newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace("gitlab", firstCredential,
                        secondCredential, repoName, repoNamespace);
        assertThat(repository).isInstanceOf(GitlabRepository.class);
    }

    @Test
    public void whenNewInstanceOfUnknownTypeIsCalled_thenRepositoryTypeNotSupportedExceptionIsThrwon() {
        assertThatThrownBy(() ->
                AbstractRepository.newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace("some_unkown_type", firstCredential,
                        secondCredential, repoName, repoNamespace)).isInstanceOf(RepositoryTypeNotSupportedException.class);

    }


    @Test
    public void whenNewInstanceOfIsCalled_thenAllFieldsArecorrectlySet() throws RepositoryTypeNotSupportedException {
        AbstractRepository repository = AbstractRepository
                .newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace("github", firstCredential,
                        secondCredential, repoName, repoNamespace);

        assertThat(repository.getFirstLoginCredential()).isEqualTo(firstCredential);
        assertThat(repository.getSecondLoginCredential()).isEqualTo(secondCredential);
        assertThat(repository.getRepositoryNamespace()).isEqualTo(repoNamespace);
        assertThat(repository.getRepositoryName()).isEqualTo(repoName);
    }


}
