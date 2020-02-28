//package com.redhat.integration.networkConnectionTests;
//
//import com.redhat.tasksyncer.dao.accessors.repository.RepositoryAccessor;
//import com.redhat.tasksyncer.dao.accessors.repository.TrelloRepositoryAccessor;
//import com.redhat.tasksyncer.dao.entities.repositories.AbstractRepository;
//import com.redhat.tasksyncer.dao.entities.repositories.TrelloRepository;
//import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
//import org.gitlab4j.api.GitLabApiException;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//
//import java.io.IOException;
//
//public class TrelloWebhookCreationTests {
//
//    AbstractRepository repository;
//    RepositoryAccessor accessor;
//
//    @InjectMocks
//    AbstractRepositoryRepository repositoryRepository;
//
//
//    @Before
//    public void setup(){
//        repository = new TrelloRepository();
//        repository.setRepositoryName("5d5bdb0df1081112b3e2c2a3");
//        repository.setFirstLoginCredential("9942cba7d6c0f1148edb1b711a79b79c");
//        repository.setSecondLoginCredential("3d9b6ad63c66b9c509773b9a34fa4b275cc167ea5ff9262ba744c6ebd42bffb5");
//
//        accessor = new TrelloRepositoryAccessor(repositoryRepository);
//    }
//
//    // The endpoint where we want to create our webhook must be valid and responding to POST requests
//    @Test
//    public void whenCreatingWebhook_thenIsWebhookCreated() throws IOException, GitLabApiException {
//        accessor.createWebhook("https://hookb.in/wNenYwLl7qC0w0V3xlkE");
//    }
//}
