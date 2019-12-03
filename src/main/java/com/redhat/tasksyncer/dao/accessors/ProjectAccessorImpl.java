package com.redhat.tasksyncer.dao.accessors;


import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.dao.repositories.ProjectRepository;
import com.redhat.tasksyncer.exceptions.CannotConnectToRepositoryException;
import com.redhat.tasksyncer.exceptions.InvalidMappingException;
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
import java.util.*;

/**
 * @author Filip Cap, David Rajnoha
 */

@Service
@EntityScan(basePackages = {"com.redhat.tasksyncer.dao.entities"})
@EnableJpaRepositories(basePackages = {"com.redhat.tasksyncer.dao.repositories"})
public class ProjectAccessorImpl implements ProjectAccessor{


    private AbstractIssueRepository issueRepository;
    private ProjectRepository projectRepository;
    private AbstractRepositoryRepository repositoryRepository;
    private BoardAccessor boardAccessor;


    @Value("${trello.appKey}")
    private String trelloApplicationKey;

    @Value("${trello.token}")
    private String trelloAccessToken;

    private Project project;


    private Map<String, RepositoryAccessor> repositoryAccessors;

    @Autowired
    public ProjectAccessorImpl(AbstractIssueRepository issueRepository, ProjectRepository projectRepository,
                               AbstractRepositoryRepository repositoryRepository, BoardAccessor boardAccessor,
                               Map<String, RepositoryAccessor> repositoryAccessors){
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
        this.repositoryRepository = repositoryRepository;
        this.boardAccessor = boardAccessor;
        this.repositoryAccessors = repositoryAccessors;
    }

    private BoardAccessor getBoardAccessor() {
        return boardAccessor;
    }


    private BoardAccessor createBoard(String boardType, String name) throws HttpClientErrorException {
        TrelloBoard board = new TrelloBoard();
        board.setBoardName(name);

        // Saves the board locally and creates the trelloApi Object inside the board accessor to communicate with trello
        this.boardAccessor = boardAccessor.initializeAndSave(board, trelloApplicationKey, trelloAccessToken);

        // Creates the board on trello, creates two columns - done and todo
        AbstractBoard b = this.boardAccessor.createBoard(project.getColumnNames());

        // Adds this board to this project
        b.setProject(project);
        boardAccessor.save();
        return this.boardAccessor;
    }

    public void deleteBoard(String trelloApplicationKey, String trelloAccessToken) throws  CannotConnectToRepositoryException {
        try {
            boardAccessor.deleteBoard(trelloApplicationKey, trelloAccessToken);
        } catch (IOException e){
            e.printStackTrace();
            throw new CannotConnectToRepositoryException(e.getMessage());
        }
    }

    @Override
    public Project saveProject(Project project) {
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
    public RepositoryAccessor addRepository(AbstractRepository repository,
                                            Map<String, String> columnMapping)
            throws RepositoryTypeNotSupportedException, CannotConnectToRepositoryException, InvalidMappingException {
        RepositoryAccessor repositoryAccessor = createRepositoryAccessor(repository);

        if (columnMapping != null) {
            repositoryAccessor.setColumnMapping(columnMapping);
        }

        try {
            doSync(repositoryAccessor);
        } catch (CannotConnectToRepositoryException exception){
            exception.printStackTrace();
            repositoryAccessor.deleteRepository(repository);
            throw exception;
        }
        return repositoryAccessor;
    }


    /**
     * Creates a repositoryAccessor to serve as a middle layer between repository object and "real" network repository,
     * Also adds project to the repository and saves the repository TODO: This violates the single responsibility principle
     * */
    public RepositoryAccessor createRepositoryAccessor(AbstractRepository repository) throws RepositoryTypeNotSupportedException, CannotConnectToRepositoryException {
        String serviceType = repository.getClass().getSimpleName().concat("Accessor");
        String firstLetter = serviceType.substring(0,1).toLowerCase();
        String stLower = firstLetter + serviceType.substring(1);
        RepositoryAccessor repositoryAccessor = repositoryAccessors.get(stLower);

        if (repositoryAccessor == null){
            throw new RepositoryTypeNotSupportedException("Repo type: " + serviceType + " not supported");
        }

        repositoryAccessor.getConnectedInstance(repository);

        // RepositoryAccessor repositoryAccessor = RepositoryAccessor.getConnectedInstance(repository, repositoryRepository, issueRepository);
        // Creates the repository accessor and saves the repository
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
    private void doSync(RepositoryAccessor repositoryAccessor) throws CannotConnectToRepositoryException {
        List<AbstractIssue> issues;
        try {
            // downloads issue from external repository
            issues = repositoryAccessor.downloadAllIssues();
        } catch (IOException | GitLabApiException e) {
            throw new CannotConnectToRepositoryException(e.getMessage());
        }

        for(AbstractIssue i : issues) {
                //
                i.setRepository(repositoryAccessor.getRepository());
                this.syncIssue(i);
        }
    }

    public AbstractIssue update(AbstractIssue newIssue) {
        AbstractIssue oldIssue = issueRepository.findByRemoteIssueIdAndRepository_repositoryName(newIssue.getRemoteIssueId(),
                newIssue.getRepository().getRepositoryName())
                .orElse(newIssue);

        Optional<AbstractIssue> superIssue = issueRepository.findByRemoteIssueIdAndRepository_repositoryName(newIssue.getRemoteIssueId(),
                newIssue.getRepository().getRepositoryName());

        if(oldIssue.getId() != null) {
            // there exists such issue (the old issue has an id, therefor was saved, therefor exists in repository)
            oldIssue.updateProperties(newIssue);

        }

        // Gets the child issues, if there are child issues present, the child issue is updated
        // recursive calling of the update function is used, it stops when we reach issue with no childIssues
        // the update of the innermost issues is therefor finished first
        // It has to be prohibited to save circular referencing issues - TODO: assert this will not happen

        // If the issues has subIssue(s) then updates the subIssue(s)
        Optional.ofNullable(newIssue.getChildIssues()).ifPresent(childIssueSet -> {
            // New copy of set so the concurrentModification exception wouldn't be thrown
            Set<AbstractIssue> copyChildIssuesSet = new HashSet<>(childIssueSet.values());

            for (AbstractIssue childIssue : copyChildIssuesSet) {
                oldIssue.removeChildIssue(childIssue);
                oldIssue.addChildIssue(update(childIssue));
            }
        });

        // its new issue
        return oldIssue;
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
        // sets card on the issue and also syncs this card with Trello
        issue = setCard(issue);
        issueRepository.save(issue);
    }

    public AbstractIssue setCard(AbstractIssue issue){
        issue = updateCard(issue); // setting new properties to the card
        issue.setCard(this.getBoardAccessor().update(issue.getCard())); // saving and syncing the card, if new card then
        // then card with id is returned
        return issue;
    }

    public void hookRepository(AbstractRepository repository, String webhookUrl,
                               Map<String, String> columnMapping) throws
            RepositoryTypeNotSupportedException, IOException, SynchronizationFailedException, GitLabApiException,
            CannotConnectToRepositoryException, InvalidMappingException {
        RepositoryAccessor repositoryAccessor;
        //Adds the repository to the project, syncs it and returns the particular repository Accessor
        repositoryAccessor = addRepository(repository, columnMapping);
        repositoryAccessor.createWebhook(webhookUrl);
    }

    public void deleteProject(Project project) {
        if (this.project == project) this.project = null;
        projectRepository.delete(project);
    }

    public void setColumnNames(List<String> columnNames){
        project.setColumnNames(columnNames);
        projectRepository.save(project);
    }

}