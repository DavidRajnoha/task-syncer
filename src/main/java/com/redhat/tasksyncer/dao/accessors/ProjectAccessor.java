package com.redhat.tasksyncer.dao.accessors;


import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.*;
import org.kohsuke.github.GHEvent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Filip Cap
 */
public class ProjectAccessor {

    private Project project;

    private BoardAccessor board;
    private RepositoryAccessor gitlabRepository;

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


    public BoardAccessor getBoard() {
        if(board == null)
            board = new TrelloBoardAccessor((TrelloBoard) project.getBoard(), trelloApplicationKey, trelloAccessToken, boardRepository, cardRepository, columnRepository); // todo generify

        return board;
    }

    public RepositoryAccessor getGitlabRepository() {
        if(gitlabRepository == null)
            gitlabRepository = new GitlabRepositoryAccessor((GitlabRepository) project.getRepository(), repositoryRepository, issueRepository, gitlabURL, gitlabAuthKey);  // todo generify

        return gitlabRepository;
    }

    private BoardAccessor createBoard(String boardType, String name) {
        TrelloBoard board = new TrelloBoard();
        board.setBoardName(name);

        this.board = new TrelloBoardAccessor(board, trelloApplicationKey, trelloAccessToken, boardRepository, cardRepository, columnRepository);

        AbstractBoard b = this.board.createItself();
        project.setBoard(b);  // todo: maybe propagate to boardAccessor if created

        return this.board;
    }

    public void save() {
        project = projectRepository.save(project);
    }

    public void initialize(String repoType, String repoNamespace, String repoName, String boardType, String boardName) {
        // todo : maybe check whether not already initialised?
        createBoard(boardType, boardName);
        createRepository(repoType, repoNamespace, repoName);
    }

    private RepositoryAccessor createRepository(String repoType, String repoNamespace, String repoName) {
        GitlabRepository repository = new GitlabRepository();

        repository.setRepositoryNamespace(repoNamespace);
        repository.setRepositoryName(repoName);

        this.gitlabRepository = new GitlabRepositoryAccessor(repository, repositoryRepository, issueRepository, gitlabURL, gitlabAuthKey);

        AbstractRepository r = this.gitlabRepository.createItself();
        project.setRepository(r);  // todo: maybe propagate to repositoryAccessor if created

        return this.gitlabRepository;
    }

    public void doInitialSync() throws Exception {
        List<AbstractIssue> issues = getGitlabRepository().downloadAllIssues();

        for(AbstractIssue i : issues) {
            this.update(i);
        }
    }

    public void update(AbstractIssue newIssue) {
        AbstractIssue oldIssue = issueRepository.findByRemoteIssueId(newIssue.getRemoteIssueId())
                .orElse(newIssue);

        if(oldIssue.getId() != null) {  // there exists such issue
            oldIssue.updateProperties(newIssue);

            List<AbstractColumn> columns = getBoard().getColumns();  // for now we assume that there exists such column for mapping

            oldIssue.getCard().updateProperties(TrelloCard.IssueToCardConverter.convert(newIssue, columns));

            this.getBoard().update(oldIssue.getCard());

            issueRepository.save(oldIssue);

            return;
        }

        // its new issue

        newIssue.setRepository(project.getRepository());

        List<AbstractColumn> columns = getBoard().getColumns();  // for now we assume that there exists such column for mapping
        AbstractCard c = this.getBoard().update(TrelloCard.IssueToCardConverter.convert(newIssue, columns));  // todo use generic converter
        newIssue.setCard(c);

        issueRepository.save(newIssue);
    }

    public void connectGithub(java.net.URL webHookUrl, String repoName) throws IOException {
        GithubRepository githubRepository = new GithubRepository();
        githubRepository.setRepositoryName(repoName);
        githubRepository.setGithubPassword(gitHubPassword);
        githubRepository.setGithubUsername(gitHubUsername);

        GithubRepositoryAccessor githubRepositoryAccessor = new GithubRepositoryAccessor(githubRepository, repositoryRepository, issueRepository);

        githubRepositoryAccessor.createWebhook(webHookUrl);

    }
}
