package com.redhat.tasksyncer.dao.accessors;


import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.*;
import com.redhat.tasksyncer.exceptions.RepositoryTypeNotSupportedException;

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
    private String gitHubPassword;
    private String gitHubUsername;

    public ProjectAccessor(Project project, AbstractBoardRepository boardRepository, AbstractRepositoryRepository repositoryRepository, AbstractIssueRepository issueRepository, AbstractCardRepository cardRepository, AbstractColumnRepository columnRepository, ProjectRepository projectRepository, String trelloApplicationKey, String trelloAccessToken, String gitlabURL, String gitlabAuthKey,
                           String gitHubUsername, String gitHubPassword) {
        this.project = project;

        this.boardRepository = boardRepository;
        this.repositoryRepository = repositoryRepository;
        this.issueRepository = issueRepository;
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.projectRepository = projectRepository;
        this.trelloApplicationKey = trelloApplicationKey;
        this.trelloAccessToken = trelloAccessToken;
        this.gitHubUsername = gitHubUsername;
        this.gitHubPassword = gitHubPassword;
    }


    public BoardAccessor getBoardAccessor() {
        if(boardAccessor == null)
            boardAccessor = new TrelloBoardAccessor((TrelloBoard) project.getBoard(), trelloApplicationKey, trelloAccessToken, boardRepository, cardRepository, columnRepository); // todo generify

        return boardAccessor;
    }


    private BoardAccessor createBoard(String boardType, String name) {
        TrelloBoard board = new TrelloBoard();
        board.setBoardName(name);

        this.boardAccessor = new TrelloBoardAccessor(board, trelloApplicationKey, trelloAccessToken, boardRepository, cardRepository, columnRepository);

        AbstractBoard b = this.boardAccessor.createItself();
        b.setProject(project);
        boardRepository.save(b);
        this.save();
        return this.boardAccessor;
    }

    public void save() {
        project = projectRepository.save(project);
    }

    /**
     * Takes a repository and board, creates a new board to display Issues,
     * and invokes method add repository that leads to setting this repository to this project, and syncing the issues
     * from the external repository with the internal repository and then also with trello
     * */
    public void initialize(AbstractRepository repository, String boardType, String boardName) throws Exception {
        // todo : maybe check whether not already initialised?
        createBoard(boardType, boardName);
        addRepository(repository);
    }

    /**
     * Takes a subclass of the AbstractRepository class and creates new accessor for this class.
     * The accessor is then used to sync the issues from the particular repository with the internal database
     * */
    public void addRepository(AbstractRepository repository) throws Exception {
        RepositoryAccessor repositoryAccessor = createRepositoryAccessor(repository);
        doSync(repositoryAccessor);
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
    public void doSync(RepositoryAccessor repositoryAccessor) throws Exception {
        List<AbstractIssue> issues = repositoryAccessor.downloadAllIssues();

        for(AbstractIssue i : issues) {
            i.setRepository(repositoryAccessor.getRepository());
            this.update(i);
        }
    }

    public void update(AbstractIssue newIssue) {
        AbstractIssue oldIssue = issueRepository.findByRemoteIssueIdAndIssueTypeAndRepository_repositoryName(newIssue.getRemoteIssueId(), newIssue.getIssueType(), newIssue.getRepository().getRepositoryName())
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



    //TODO: refactor into addRepository and initialize repository

    public void connectGithub(String webHookUrl, String repoName) throws Exception {

        //Creates a new githubRepository Object, now only acting as a container to pass repoName, UserName and Pass to the gitHubRepositoryAccessor
        //TODO: Decide what exactly is the function of the gitHubRepository entity, implement saving it
        GithubRepository githubRepository = new GithubRepository();
        githubRepository.setRepositoryName(repoName);
        githubRepository.setSecondLoginCredential(gitHubPassword);
        githubRepository.setFirstLoginCredential(gitHubUsername);

        //Creates new Accessor that is used for the commmunication with the particular githubrepository
        GithubRepositoryAccessor githubRepositoryAccessor = new GithubRepositoryAccessor(githubRepository, repositoryRepository, issueRepository);
        githubRepositoryAccessor.save();

        //Creating webhook in the desired repository
        githubRepositoryAccessor.createWebhook(new URL(webHookUrl));
        System.out.println("webHook Created");

        //Synchronization of the issues from github to local repository and trello
        doSync(githubRepositoryAccessor);
        System.out.println("Issues Synced");

    }


}