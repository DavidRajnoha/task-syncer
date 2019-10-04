package com.redhat.tasksyncer.dao.accessors;


import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.*;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;
import com.redhat.tasksyncer.exceptions.SynchronizationFailedException;
import org.gitlab4j.api.GitLabApiException;
import org.json.JSONException;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @author Filip Cap
 */
public class ProjectAccessor {

    private Project project;

    private BoardAccessor boardAccessor;

    private AbstractIssueRepository issueRepository;
    private AbstractCardRepository cardRepository;
    private AbstractColumnRepository columnRepository;
    private ProjectRepository projectRepository;
    private AbstractRepositoryRepository repositoryRepository;
    private AbstractBoardRepository boardRepository;

    private String trelloApplicationKey;
    private String trelloAccessToken;

    public ProjectAccessor(Project project, AbstractBoardRepository boardRepository, AbstractRepositoryRepository repositoryRepository, AbstractIssueRepository issueRepository, AbstractCardRepository cardRepository, AbstractColumnRepository columnRepository, ProjectRepository projectRepository, String trelloApplicationKey, String trelloAccessToken) {
        this.project = project;

        this.boardRepository = boardRepository;
        this.repositoryRepository = repositoryRepository;
        this.issueRepository = issueRepository;
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.projectRepository = projectRepository;
        this.trelloApplicationKey = trelloApplicationKey;
        this.trelloAccessToken = trelloAccessToken;
    }


    public BoardAccessor getBoardAccessor() {
        if(boardAccessor == null)
            boardAccessor = new TrelloBoardAccessor((TrelloBoard) project.getBoard(), trelloApplicationKey, trelloAccessToken, boardRepository, cardRepository, columnRepository); // todo generify

        return boardAccessor;
    }


    private BoardAccessor createBoard(String boardType, String name) throws HttpClientErrorException {
        TrelloBoard board = new TrelloBoard();
        board.setBoardName(name);

        this.boardAccessor = new TrelloBoardAccessor(board, trelloApplicationKey, trelloAccessToken, boardRepository, cardRepository, columnRepository);

        AbstractBoard b = this.boardAccessor.createItself();
        b.setProject(project);
        boardRepository.save(b);
        return this.boardAccessor;
    }

    public void deleteBoard(String trelloApplicationKey, String trelloAccessToken) throws IOException {
        boardAccessor.deleteBoard(trelloApplicationKey, trelloAccessToken);
    }

    public void save() {
        project = projectRepository.save(project);
    }

    /**
     * Creates board to display the issues
     * */
    public void initialize(String boardType, String boardName) {
        // todo : maybe check whether not already initialised?
        createBoard(boardType, boardName);
    }

    /**
     * Takes a subclass of the AbstractRepository class and creates new accessor for this class.
     * The accessor is then used to sync the issues from the particular repository with the internal database
     * */
    public RepositoryAccessor addRepository(AbstractRepository repository) throws SynchronizationFailedException, IOException, RepositoryTypeNotSupportedException {
        RepositoryAccessor repositoryAccessor = createRepositoryAccessor(repository);
        try {
            doSync(repositoryAccessor);
        } catch (GitLabApiException | IOException  glException){
            repositoryAccessor.deleteRepository(repository);
            System.out.println(glException.getMessage());
            throw new SynchronizationFailedException("Synchronization with " + repository.getClass().toString() + " failed");
        }
        return repositoryAccessor;
    }


    /**
     * Creates a repositoryAccessor to serve as a middle layer between repository object and "real" network repository,
     * Also adds project to the repository and saves the repository TODO: This violates the single responsibility principle
     * */
    private RepositoryAccessor createRepositoryAccessor(AbstractRepository repository) throws RepositoryTypeNotSupportedException, IOException {
        RepositoryAccessor repositoryAccessor = RepositoryAccessor.getConnectedInstance(repository, repositoryRepository, issueRepository);

        AbstractRepository r = repositoryAccessor.createItself();
        r.setProject(project);
        repositoryRepository.save(r);

        return repositoryAccessor;
    }

    /**
     * Method takes takes object that extends the RepositoryAccessor abstract class and uses the downloadAllIssues()
     * method to get a list of issues from that particular repository, then updates and syncs those issues with the internal
     * IssueRepository and Trello using the update method
     * */
    private void doSync(RepositoryAccessor repositoryAccessor) throws IOException, GitLabApiException {
        List<AbstractIssue> issues = repositoryAccessor.downloadAllIssues();

        for(AbstractIssue i : issues) {
            i.setRepository(repositoryAccessor.getRepository());
            this.update(i);
        }
    }

    public void update(AbstractIssue newIssue) {
        AbstractIssue oldIssue = issueRepository.findByRemoteIssueIdAndRepository_repositoryName(newIssue.getRemoteIssueId(), newIssue.getRepository().getRepositoryName())
                .orElse(newIssue);

        if(oldIssue.getId() != null) {  // there exists such issue (the old issue has an id, therefor was saved, therefor exists in repository)
            oldIssue.updateProperties(newIssue);

            List<AbstractColumn> columns = getBoardAccessor().getColumns();  // for now we assume that there exists such column for mapping

            oldIssue.getCard().updateProperties(TrelloCard.IssueToCardConverter.convert(newIssue, columns));

            this.getBoardAccessor().update(oldIssue.getCard());

            issueRepository.save(oldIssue);

            return;
        }

        // its new issue

        List<AbstractColumn> columns = getBoardAccessor().getColumns();  // for now we assume that there exists such column for mapping
        AbstractCard c = this.getBoardAccessor().update(TrelloCard.IssueToCardConverter.convert(newIssue, columns));  // todo use generic converter
        newIssue.setCard(c);

        issueRepository.save(newIssue);
    }

    public void hookRepository(AbstractRepository repository, String webhookUrl) throws Exception {
        RepositoryAccessor repositoryAccessor;
        //Adds the repository to the project, syncs it and returns the particular repository Accessor
        repositoryAccessor = addRepository(repository);
        repositoryAccessor.createWebhook(webhookUrl);
    }


    public void deleteProject(Project project) {
        projectRepository.delete(project);
    }
}