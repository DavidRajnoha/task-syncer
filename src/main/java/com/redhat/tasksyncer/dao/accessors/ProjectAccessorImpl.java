package com.redhat.tasksyncer.dao.accessors;


import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.*;
import com.redhat.tasksyncer.exceptions.IssueSyncFailedException;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;
import com.redhat.tasksyncer.exceptions.SynchronizationFailedException;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.List;

/**
 * @author Filip Cap
 */

@Service
@EntityScan(basePackages = {"com.redhat.tasksyncer.dao.entities"})
@EnableJpaRepositories(basePackages = {"com.redhat.tasksyncer.dao.repositories"})
public class ProjectAccessorImpl implements ProjectAccessor{


    private BoardAccessor boardAccessor;

    @Autowired
    private AbstractIssueRepository issueRepository;
    @Autowired
    private AbstractCardRepository cardRepository;
    @Autowired
    private AbstractColumnRepository columnRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private AbstractRepositoryRepository repositoryRepository;
    @Autowired
    private AbstractBoardRepository boardRepository;


    @Value("${trello.appKey}")
    private String trelloApplicationKey;

    @Value("${trello.token}")
    private String trelloAccessToken;

    private Project project;

    public ProjectAccessorImpl(){
    }

    // Deprecated - used just in test so they wont throw errors until changed
    public ProjectAccessorImpl(Project project, String trelloApplicationKey, String trelloAccessToken) {
        this.project = project;

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
        b.setProjectImpl(project);
        boardRepository.save(b);
        return this.boardAccessor;
    }

    public void deleteBoard(String trelloApplicationKey, String trelloAccessToken) throws IOException {
        boardAccessor.deleteBoard(trelloApplicationKey, trelloAccessToken);
    }

    @Override
    public Project saveAndInitialize(Project project) {
        this.project = projectRepository.save(project);
        return this.project;
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
        } catch (IssueSyncFailedException | GitLabApiException glException){
            glException.printStackTrace();
            repositoryAccessor.deleteRepository(repository);
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
        r.setProjectImpl(project);
        repositoryRepository.save(r);

        return repositoryAccessor;
    }

    /**
     * Method takes takes object that extends the RepositoryAccessor abstract class and uses the downloadAllIssues()
     * method to get a list of issues from that particular repository, then updates and syncs those issues with the internal
     * IssueRepository and Trello using the update method
     * */
    private void doSync(RepositoryAccessor repositoryAccessor) throws IOException, GitLabApiException, IssueSyncFailedException {
        List<AbstractIssue> issues = repositoryAccessor.downloadAllIssues();

        for(AbstractIssue i : issues) {
            try {
                i.setRepository(repositoryAccessor.getRepository());
                this.syncIssue(i);
            } catch (Exception e){
                i.setRepository(null);
                throw new IssueSyncFailedException(e);
            }
        }
    }

    public AbstractIssue update(AbstractIssue newIssue) {
        AbstractIssue oldIssue = issueRepository.findByRemoteIssueIdAndRepository_repositoryName(newIssue.getRemoteIssueId(),
                newIssue.getRepository().getRepositoryName())
                .orElse(newIssue);

        if(oldIssue.getId() != null) {
            // there exists such issue (the old issue has an id, therefor was saved, therefor exists in repository)
            oldIssue.updateProperties(newIssue);
            return oldIssue;
        }

        // its new issue
        return newIssue;
    }

    public AbstractIssue updateCard(AbstractIssue issue) {
        List<AbstractColumn> columns = getBoardAccessor().getColumns();  // for now we assume that there exists such column for mapping

        if (issue.getCard() == null) {
            // It is a new issue and the card does not exist yet
            AbstractCard c = TrelloCard.IssueToCardConverter.convert(issue, columns);  // todo use generic converter
            issue.setCard(c);

            return issue;
        }

        issue.getCard().updateProperties(TrelloCard.IssueToCardConverter.convert(issue, columns));

        return issue;
    }

    public void syncIssue(AbstractIssue issue) {
        issue = update(issue);
        issue = issueRepository.save(issue); // so the issue has id and is saved in repository before saving card

        issue = updateCard(issue); // setting new properties to the card
        issue.setCard(this.getBoardAccessor().update(issue.getCard())); // saving and syncing the card, if new card then
                                                                        // then card with id is returned

        issueRepository.save(issue);
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