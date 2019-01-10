package com.redhat.tasksyncer.dao.entities;


import com.redhat.tasksyncer.dao.AbstractBoardRepository;
import com.redhat.tasksyncer.dao.AbstractRepositoryRepository;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.util.List;

/**
 * @author Filip Cap
 */
@PropertySource("classpath:other.properties")
@Entity(name = "project")
public class Project {
    @Transient
    @Value("${gitlabURL}")
    private String gitlabURL;

    @Transient
    @Value("${gitlabAuthKey}")
    private String gitlabAuthKey;

    @Transient
    private GitLabApi gitlabApi;

    @Transient
    @Autowired
    private AbstractRepositoryRepository repositoryRepository;

    @Transient
    @Autowired
    private AbstractBoardRepository boardRepository;

    @PostConstruct
    public void init() {
        gitlabApi = new GitLabApi(gitlabURL, Constants.TokenType.PRIVATE, gitlabAuthKey);
    }

// --- end of transient values ---------------------------------------


    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;



    private String boardType;
    private String boardName;

    @Column(name = "blah")
    private String boardId;

    private String repoType;
    private String repoNamespace;
    private String repoName;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private AbstractRepository repository;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private AbstractBoard board;

    public Project() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getRepoType() {
        return repoType;
    }

    public void setRepoType(String repoType) {
        this.repoType = repoType;
    }

    public String getRepoNamespace() {
        return repoNamespace;
    }

    public void setRepoNamespace(String repoNamespace) {
        this.repoNamespace = repoNamespace;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getBoardType() {
        return boardType;
    }

    public void setBoardType(String boardType) {
        this.boardType = boardType;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(AbstractRepository repository) {
        this.repository = repository;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(AbstractBoard board) {
        this.board = board;
    }

    public void createBoard() {
        board = boardRepository.save(board.createItself());
    }

    public void initialize(AutowireCapableBeanFactory factory, String projectName, String repoType, String repoNamespace, String repoName, String boardType, String boardName) {
        this.name = projectName;

        GitlabRepository r = factory.createBean(GitlabRepository.class);
        r.setRepositoryNamespace(repoNamespace);
        r.setRepositoryName(repoName);


        this.repository = repositoryRepository.save(r);  // todo: use repotype to determine type
//        this.repoType = repoType;

        TrelloBoard b = factory.createBean(TrelloBoard.class);
        b.setBoardName(boardName);

        this.board = boardRepository.save(b);  // todo: user boardtype to determine type
//        this.boardType = boardType;
    }
}
