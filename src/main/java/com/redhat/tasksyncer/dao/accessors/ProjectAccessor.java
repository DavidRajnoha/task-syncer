package com.redhat.tasksyncer.dao.accessors;


import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Filip Cap
 */
public class ProjectAccessor {

    private Project project;

    private BoardAccessor boardAccessor;
    private RepositoryAccessor gitlabRepositoryAccessor;

    private AbstractIssueRepository issueRepository;
    private AbstractCardRepository cardRepository;
    private AbstractColumnRepository columnRepository;
    private ProjectRepository projectRepository;
    private AbstractRepositoryRepository repositoryRepository;
    private AbstractBoardRepository boardRepository;

    private String trelloApplicationKey;
    private String trelloAccessToken;
    private String gitlabURL;
    private String gitlabAuthKey;
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
        this.gitlabURL = gitlabURL;
        this.gitlabAuthKey = gitlabAuthKey;
        this.gitHubUsername = gitHubUsername;
        this.gitHubPassword = gitHubPassword;
    }


    public BoardAccessor getBoardAccessor() {
        if(boardAccessor == null)
            boardAccessor = new TrelloBoardAccessor((TrelloBoard) project.getBoard(), trelloApplicationKey, trelloAccessToken, boardRepository, cardRepository, columnRepository); // todo generify

        return boardAccessor;
    }

    public RepositoryAccessor getGitlabRepositoryAccessor() {
        if(gitlabRepositoryAccessor == null)
            gitlabRepositoryAccessor = new GitlabRepositoryAccessor((GitlabRepository) project.getRepositories(), repositoryRepository, issueRepository, gitlabURL, gitlabAuthKey);  // todo generify

        return gitlabRepositoryAccessor;
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

    public void initialize(String repoType, String repoNamespace, String repoName, String boardType, String boardName) throws Exception {
        // todo : maybe check whether not already initialised?
        createRepositoryList();
        createBoard(boardType, boardName);
        createRepository(repoType, repoNamespace, repoName);
        doSync(gitlabRepositoryAccessor);
    }

    private void createRepositoryList() {
        this.project.setRepositories(new ArrayList<>());
    }

    private RepositoryAccessor createRepository(String repoType, String repoNamespace, String repoName /*, RepositoryAccessor repositoryAccessor*/) {
        GitlabRepository repository = new GitlabRepository();

        repository.setRepositoryNamespace(repoNamespace);
        repository.setRepositoryName(repoName);

        this.gitlabRepositoryAccessor = new GitlabRepositoryAccessor(repository, repositoryRepository, issueRepository, gitlabURL, gitlabAuthKey);

        AbstractRepository r = this.gitlabRepositoryAccessor.createItself();
        r.setProject(project);
        repositoryRepository.save(r);

        return this.gitlabRepositoryAccessor;
    }

    /**
     * Method takes takes object that extends the RepositoryAccessor abstract class and uses the downloadAllIssues()
     * method to get a list of issues from that particular repository, than updates all of these issues in issueRepository and
     * in Trello
     * */
    public void doSync(RepositoryAccessor repositoryAccessor) throws Exception {
        List<AbstractIssue> issues = repositoryAccessor.downloadAllIssues();

        for(AbstractIssue i : issues) {
            i.setRepository(repositoryAccessor.getRepository());
            this.update(i);
        }
    }

    public void update(AbstractIssue newIssue) {
        AbstractIssue oldIssue = issueRepository.findByRemoteIssueIdAndIssueTypeAndRepository_repositoryName(newIssue.getRemoteIssueId(), newIssue.getIssueType(), newIssue.getRepositoryName())
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

    public void connectGithub(String webHookUrl, String repoName) throws Exception {

        //Creates a new githubRepository Object, now only acting as a container to pass repoName, UserName and Pass to the gitHubRepositoryAccessor
        //TODO: Decide what exactly is the function of the gitHubRepository entity, implement saving it
        GithubRepository githubRepository = new GithubRepository();
        githubRepository.setRepositoryName(repoName);
        githubRepository.setGithubPassword(gitHubPassword);
        githubRepository.setGithubUsername(gitHubUsername);

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

        public void addGitlabRepository(String repoName, String repoNamespace) throws Exception {
        createRepository(GitlabRepository.class.getName(), repoNamespace, repoName);
        doSync(gitlabRepositoryAccessor);
        System.out.println("Issues Synced");
    }
}