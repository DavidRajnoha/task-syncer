package com.redhat.tasksyncer.services.presentation;

import com.redhat.tasksyncer.dao.accessors.issue.AbstractIssueAccessor;
import com.redhat.tasksyncer.dao.accessors.project.ProjectAccessor;
import com.redhat.tasksyncer.presentation.trello.TrelloBoardAccessor;
import com.redhat.tasksyncer.presentation.trello.TrelloCardAccessor;
import com.redhat.tasksyncer.presentation.trello.TrelloColumnAccessor;
import com.redhat.tasksyncer.dao.entities.trello.AbstractColumn;
import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.projects.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class TrelloService {


    private TrelloBoardAccessor boardAccessor;
    private TrelloColumnAccessor columnAccessor;
    private TrelloCardAccessor cardAccessor;
    private ProjectAccessor projectAccessor;
    private AbstractIssueAccessor issueAccessor;

    @Autowired
    public TrelloService(TrelloBoardAccessor trelloBoardAccessor, ProjectAccessor projectAccessor,
                         TrelloColumnAccessor columnAccessor, TrelloCardAccessor cardAccessor,
                         AbstractIssueAccessor issueAccessor){
        this.boardAccessor = trelloBoardAccessor;
        this.columnAccessor = columnAccessor;
        this.cardAccessor = cardAccessor;
        this.projectAccessor = projectAccessor;
        this.issueAccessor = issueAccessor;
    }

    public void createBoard(String projectName, String trelloApplicationKey, String trelloAccessToken) throws Exception {
        Project project = projectAccessor.getProject(projectName);

        if (project.getBoard() != null) {
            // TODO: Handle exceptions in a better way
            throw new Exception();
        }

        boardAccessor.connectToTrello(trelloApplicationKey, trelloAccessToken);
        boardAccessor.createBoard(project);

        columnAccessor.connectToTrello(trelloApplicationKey, trelloAccessToken);
        columnAccessor.createColumns(project);

        updateBoard(projectName, trelloApplicationKey, trelloAccessToken);

    }


    public void deleteBoard(String projectName, String trelloApplicationKey, String trelloAccessToken) throws IOException {
        Project project = projectAccessor.getProject(projectName);

        boardAccessor.deleteBoard(trelloApplicationKey, trelloAccessToken, project);

    }

    public void updateBoard(String projectName, String trelloApplicationKey, String trelloAccesToken){
        boardAccessor.connectToTrello(trelloApplicationKey, trelloAccesToken);

        cardAccessor.connectToTrello(trelloApplicationKey, trelloAccesToken);

        List<AbstractIssue> issues = issueAccessor.getProject(projectName);

        issues.forEach(this::updateCard);

    }




    public void updateCard(AbstractIssue issue) {
        List<AbstractColumn> columns = issue.getRepository().getProject().getBoard().getColumns();  // for now we assume that there exists such column for mapping


        if (issue.getCard() != null) {
            issue = cardAccessor.updateCard(issue, columns);
        } else {
            issue = cardAccessor.createCard(issue, columns);
        }

        issueAccessor.update(issue);
    }

}
