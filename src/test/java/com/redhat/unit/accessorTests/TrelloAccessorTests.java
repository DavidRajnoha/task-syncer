package com.redhat.unit.accessorTests;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.TList;
import com.redhat.tasksyncer.dao.accessors.TrelloRepositoryAccessor;
import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.exceptions.InvalidMappingException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TrelloAccessorTests {
    @Mock
    private AbstractRepositoryRepository repositoryRepository;
    @Mock
    private Trello trelloApi;
    @Mock
    private Board trelloBoard;

    private AbstractRepository trelloRepository = new TrelloRepository();
    private Project project = new Project();
    private AbstractBoard board = new TrelloBoard();

    TrelloRepositoryAccessor trelloAccessor = new TrelloRepositoryAccessor(repositoryRepository);
    private String boardRemoteId = "trelloRepoName";

    private List<TList> trelloLists = new ArrayList<>();
    private TList tlistOne = new TList();
    private TList tlistTwo = new TList();

    private String tlistOneId = "id_01";
    private String tlistTwoId = "id_02";

    @Before
    public void setUp() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);

        FieldSetter.setField(trelloAccessor, trelloAccessor.getClass()
                        .getDeclaredField("trelloApi"), trelloApi);

        trelloAccessor.initializeRepository(trelloRepository);

        board.setRemoteBoardId(boardRemoteId);
        project.setBoard(board);
        trelloRepository.setProject(project);

        tlistOne.setId(tlistOneId);
        tlistTwo.setId(tlistTwoId);
        trelloLists.add(tlistOne);
        trelloLists.add(tlistTwo);

        Mockito.when(trelloApi.getBoard(boardRemoteId)).thenReturn(trelloBoard);
        Mockito.when(trelloBoard.getLists()).thenReturn(trelloLists);
    }

    @Test
    public void validColumnMapping_returnsColumnMapping() throws InvalidMappingException {
        Map<String, String> correctMapping = new LinkedHashMap<>();
        correctMapping.put(tlistOneId, "TODO");
        correctMapping.put(tlistTwoId, "DONE");

        Map<String, String> checked_mapping = trelloAccessor.isMappingValid(correctMapping);

        assertThat(checked_mapping).isEqualTo(correctMapping);
    }


    @Test
    public void invalidColumnMapping_throwsError(){
        Map<String, String> incorrectMapping = new LinkedHashMap<>();
        incorrectMapping.put(tlistOneId, "TODO");
        incorrectMapping.put(tlistTwoId, "DONE");
        incorrectMapping.put("id_03", "IN_PROGRESS");

        assertThatThrownBy(() -> trelloAccessor.isMappingValid(incorrectMapping))
                .isInstanceOf(InvalidMappingException.class);
    }
}
