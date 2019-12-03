package com.redhat.unit.accessorTests;

import com.redhat.tasksyncer.dao.accessors.GitlabRepositoryAccessor;
import com.redhat.tasksyncer.dao.accessors.RepositoryAccessor;
import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.CannotConnectToRepositoryException;
import com.redhat.tasksyncer.exceptions.InvalidMappingException;
import org.gitlab4j.api.GitLabApi;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertTrue;

public class GitlabRepositoryAccessorTest {

    private RepositoryAccessor gitlabRepositoryAccessor;
    private AbstractRepository gitlabRepository;

    @Mock
    private AbstractRepositoryRepository  repositoryRepository = Mockito.mock(AbstractRepositoryRepository.class);

    @Mock
    private GitLabApi mockGitlabApi = Mockito.mock(GitLabApi.class);

    @Mock
    private Project mockProject = Mockito.mock(Project.class);

    private List<String> columnNames = new ArrayList<>();

    private static final String TODO_STATE = "TODO";
    private static final String DONE_STATE = "DONE";


    @Before
    public void setUp() throws CannotConnectToRepositoryException, NoSuchFieldException {
        gitlabRepository = new GitlabRepository();
        gitlabRepositoryAccessor = new GitlabRepositoryAccessor(repositoryRepository);
        gitlabRepository.setProject(mockProject);

        columnNames.add(TODO_STATE);
        columnNames.add(DONE_STATE);

        FieldSetter.setField(gitlabRepositoryAccessor, gitlabRepositoryAccessor
                .getClass().getDeclaredField("gitlabApi"), mockGitlabApi);

        gitlabRepositoryAccessor.initializeRepository(gitlabRepository);

        Mockito.when(mockProject.getColumnNames()).thenReturn(columnNames);

    }

    @Test
    public void settingCorrectColumnMapping_setsColumnMapping() throws InvalidMappingException {
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put(AbstractIssue.STATE_OPENED, TODO_STATE);
        columnMapping.put(AbstractIssue.STATE_REOPENED, TODO_STATE);
        columnMapping.put(AbstractIssue.STATE_CLOSED, DONE_STATE);

        gitlabRepositoryAccessor.setColumnMapping(columnMapping);

        Map<String, String> foundMapping = gitlabRepositoryAccessor.getRepository().getColumnMapping();

        assertThat(foundMapping.size()).isEqualTo(3);
        assertTrue(foundMapping.containsValue(TODO_STATE));
        assertTrue(foundMapping.containsValue(DONE_STATE));
    }


    @Test
    public void settingColumnMappingOfIncorrectSize_throwsError(){
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put(AbstractIssue.STATE_OPENED, TODO_STATE);
        columnMapping.put(AbstractIssue.STATE_CLOSED, DONE_STATE);

        assertThatThrownBy(() -> gitlabRepositoryAccessor.setColumnMapping(columnMapping))
                .isInstanceOf(InvalidMappingException.class);

        }

    @Test
    public void settingColumnMappingWithUnkownKey_throwsError(){
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put(AbstractIssue.STATE_OPENED, TODO_STATE);
        columnMapping.put(AbstractIssue.STATE_CLOSED, DONE_STATE);
        columnMapping.put("IN_PROGRESS", TODO_STATE);

        assertThatThrownBy(() -> gitlabRepositoryAccessor.setColumnMapping(columnMapping))
                .isInstanceOf(InvalidMappingException.class);
    }


    @Test
    public void settingColumnMappingToUnknownColumn_throwsError(){
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put(AbstractIssue.STATE_OPENED, TODO_STATE);
        columnMapping.put(AbstractIssue.STATE_REOPENED, "REOPENED");
        columnMapping.put(AbstractIssue.STATE_CLOSED, DONE_STATE);

        assertThatThrownBy(() -> gitlabRepositoryAccessor.setColumnMapping(columnMapping))
                .isInstanceOf(InvalidMappingException.class);
    }

    @Test
    public void basicColumnMappingShouldBeSet(){
        AbstractRepository createdRepository = gitlabRepositoryAccessor
                .createRepository("usn", "pasw",
                        "rpn", "rpns");

        Map<String, String> foundMapping = createdRepository.getColumnMapping();

        assertTrue(foundMapping.containsValue(AbstractColumn.DONE_DEFAULT));
        assertTrue(foundMapping.containsValue(AbstractColumn.TODO_DEFAULT));
        assertTrue(foundMapping.containsKey(AbstractIssue.STATE_OPENED));
        assertTrue(foundMapping.containsKey(AbstractIssue.STATE_REOPENED));
        assertTrue(foundMapping.containsKey(AbstractIssue.STATE_CLOSED));
    }

}
