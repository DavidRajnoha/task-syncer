package com.redhat.unit.accessorTests;

import com.redhat.tasksyncer.dao.accessors.repository.GitlabRepositoryAccessor;
import com.redhat.tasksyncer.dao.accessors.repository.RepositoryAccessor;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.projects.Project;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.InvalidMappingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GitlabRepositoryAccessorTest {


    @Mock
    private AbstractRepositoryRepository repositoryRepository;

    @InjectMocks
    private RepositoryAccessor gitlabRepositoryAccessor = new GitlabRepositoryAccessor(repositoryRepository);


    private Project project = new Project();
    private List<String> columnNames = new ArrayList<>();

    private static final String TODO_STATE = "TODO";
    private static final String DONE_STATE = "DONE";


    @Before
    public void setUp(){
        columnNames.add(TODO_STATE);
        columnNames.add(DONE_STATE);
        project.setColumnNames(columnNames);

    }

    @Test
    public void settingCorrectColumnMapping_setsColumnMapping() throws InvalidMappingException {
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put(AbstractIssue.STATE_OPENED, TODO_STATE);
        columnMapping.put(AbstractIssue.STATE_REOPENED, TODO_STATE);
        columnMapping.put(AbstractIssue.STATE_CLOSED, DONE_STATE);


        Map<String, String> foundMapping =  gitlabRepositoryAccessor.isMappingValid(columnNames, columnMapping);

        assertThat(foundMapping.size()).isEqualTo(3);
        assertTrue(foundMapping.containsValue(TODO_STATE));
        assertTrue(foundMapping.containsValue(DONE_STATE));
    }


    @Test
    public void settingColumnMappingOfIncorrectSize_throwsError(){
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put(AbstractIssue.STATE_OPENED, TODO_STATE);
        columnMapping.put(AbstractIssue.STATE_CLOSED, DONE_STATE);

        assertThatThrownBy(() -> gitlabRepositoryAccessor.isMappingValid(columnNames, columnMapping))
                .isInstanceOf(InvalidMappingException.class);

        }

    @Test
    public void settingColumnMappingWithUnkownKey_throwsError(){
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put(AbstractIssue.STATE_OPENED, TODO_STATE);
        columnMapping.put(AbstractIssue.STATE_CLOSED, DONE_STATE);
        columnMapping.put("IN_PROGRESS", TODO_STATE);

        assertThatThrownBy(() -> gitlabRepositoryAccessor.isMappingValid(columnNames, columnMapping))
                .isInstanceOf(InvalidMappingException.class);
    }


    @Test
    public void settingColumnMappingToUnknownColumn_throwsError(){
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put(AbstractIssue.STATE_OPENED, TODO_STATE);
        columnMapping.put(AbstractIssue.STATE_REOPENED, "REOPENED");
        columnMapping.put(AbstractIssue.STATE_CLOSED, DONE_STATE);

        assertThatThrownBy(() -> gitlabRepositoryAccessor.isMappingValid(columnNames, columnMapping))
                .isInstanceOf(InvalidMappingException.class);
    }

}
