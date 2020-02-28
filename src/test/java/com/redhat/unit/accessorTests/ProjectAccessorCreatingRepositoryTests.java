//package com.redhat.unit.accessorTests;
//
//
//import com.redhat.tasksyncer.dao.accessors.project.ProjectAccessor;
//import com.redhat.tasksyncer.dao.accessors.project.ProjectAccessorImpl;
//import com.redhat.tasksyncer.dao.accessors.repository.GitlabRepositoryAccessor;
//import com.redhat.tasksyncer.dao.accessors.repository.RepositoryAccessor;
//import com.redhat.tasksyncer.dao.accessors.trello.BoardAccessor;
//import com.redhat.tasksyncer.dao.entities.projects.Project;
//import com.redhat.tasksyncer.dao.entities.repositories.AbstractRepository;
//import com.redhat.tasksyncer.dao.entities.repositories.GitlabRepository;
//import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
//import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
//import com.redhat.tasksyncer.dao.repositories.ProjectRepository;
//import org.junit.Before;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.mockito.internal.util.reflection.FieldSetter;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class ProjectAccessorCreatingRepositoryTests {
//
//    private ProjectAccessor projectAccessorUnderTest;
//
//    private Project project = new Project();
//
//    @Mock
//    private AbstractIssueRepository issueRepository;
//    @Mock
//    private ProjectRepository projectRepository;
//    @Mock
//    private AbstractRepositoryRepository repositoryRepository;
//    @Mock
//    private BoardAccessor boardAccessor;
//    @Mock
//    private Map<String, RepositoryAccessor> repositoryAccessors;
//
//
//    private RepositoryAccessor gitlabRepositoryAccessor = Mockito.mock(GitlabRepositoryAccessor.class);
//    private AbstractRepository gitlabRepository = new GitlabRepository();
//
//    private AbstractRepository unsupportedRepository = new UnsupportedRepository();
//
//    private String glRepoName = "glRepoName";
//    private String projectName = "ProjectName";
//
//    private Map<String, String> columnMapping = new HashMap<>();
//
//    @Before
//    public void setup() throws NoSuchFieldException {
//        MockitoAnnotations.initMocks(this);
//
//        projectAccessorUnderTest = new ProjectAccessorImpl(projectRepository);
//
//        gitlabRepository.setRepositoryName(glRepoName);
//        project.setName(projectName);
//
//
//        FieldSetter.setField(projectAccessorUnderTest, projectAccessorUnderTest.getClass()
//                .getDeclaredField("project"), project);
//
//        Mockito.when(repositoryAccessors.get("gitlabRepositoryAccessor")).thenReturn(gitlabRepositoryAccessor);
//        Mockito.when(gitlabRepositoryAccessor.createItself()).thenReturn(gitlabRepository);
//
//        columnMapping.put("key", "custom_name");
//
//    }
//
////    @Test
////    public void createRepositoryAccessor_createsAccessor() throws RepositoryTypeNotSupportedException, CannotConnectToRepositoryException {
////        ArgumentCaptor<AbstractRepository> repositoryArgumentCaptor = ArgumentCaptor.forClass(AbstractRepository.class);
////
////        // test
////        RepositoryAccessor createdRepositoryAccessor = projectAccessorUnderTest
////                .createRepositoryAccessor(gitlabRepository);
////
////        // verification
////
////        Mockito.verify(repositoryRepository, Mockito.times(1))
////                .save(repositoryArgumentCaptor.capture());
////        AbstractRepository savedRepository = repositoryArgumentCaptor.getValue();
////        assertThat(savedRepository).isEqualTo(gitlabRepository);
////
////        assertThat(createdRepositoryAccessor).isEqualTo(gitlabRepositoryAccessor);
////    }
//
////    @Test
////    public void createRepositoryAccessorForInvalidClass_throwsError(){
////        //test
////        assertThatThrownBy(() -> projectAccessorUnderTest.createRepositoryAccessor(unsupportedRepository))
////                .isInstanceOf(RepositoryTypeNotSupportedException.class);
////    }
//
////    @Test
////    public void addingRepositoruWithNullColumnMapping_doesntAddColmnMapping() throws InvalidMappingException, RepositoryTypeNotSupportedException, CannotConnectToRepositoryException {
////        // Test
////        projectAccessorUnderTest.addRepository(gitlabRepository, null);
////
////        // Verification
////        Mockito.verify(gitlabRepositoryAccessor, Mockito.times(0)).setColumnMapping(any());
////    }
//
////    @Test
////    @SuppressWarnings("unchecked")
////    public void addingRepositoryWithCollumnMapping_setsColumnMapping() throws InvalidMappingException, CannotConnectToRepositoryException, RepositoryTypeNotSupportedException {
////        ArgumentCaptor<Map<String, String>> argumentCaptor = ArgumentCaptor.forClass(Map.class);
////
////        //Test
////        projectAccessorUnderTest.addRepository(gitlabRepository,columnMapping);
////
////        //Verification
////        Mockito.verify(gitlabRepositoryAccessor, Mockito.times(1))
////                .setColumnMapping(argumentCaptor.capture());
////        assertThat(argumentCaptor.getValue().get("key")).isEqualTo("custom_name");
////    }
//
//    private static class UnsupportedRepository extends AbstractRepository{
//
//    }
//}
