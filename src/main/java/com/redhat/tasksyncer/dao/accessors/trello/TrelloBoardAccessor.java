package com.redhat.tasksyncer.dao.accessors.trello;

import com.julienvey.trello.domain.Argument;
import com.julienvey.trello.domain.Board;
import com.redhat.tasksyncer.dao.entities.trello.AbstractBoard;
import com.redhat.tasksyncer.dao.entities.trello.TrelloBoard;
import com.redhat.tasksyncer.dao.entities.projects.Project;
import com.redhat.tasksyncer.dao.repositories.AbstractBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Filip Cap, David Rajnoha
 */

@Service
@EntityScan(basePackages = {"com.redhat.tasksyncer.dao.entities"})
public class TrelloBoardAccessor extends AbstractTrelloAccessor implements BoardAccessor{

    private AbstractBoardRepository boardRepository;



    @Autowired
    public TrelloBoardAccessor(AbstractBoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }


    @Override
    public void createBoard(Project project) throws HttpClientErrorException {
        AbstractBoard trelloBoard = new TrelloBoard();

        Board tBoard = trelloApi.createBoard(
                project.getName(),
                new Argument("defaultLabels", "false"),
                new Argument("defaultLists", "false")
        );

        trelloBoard.setRemoteBoardId(tBoard.getId());
        trelloBoard.setProject(project);

        boardRepository.save(trelloBoard);
    }


    @Override
        public String deleteBoard(String trelloApplicationKey, String trelloAccessToken, Project project) throws IOException {
        URL url = new URL("https://api.trello.com/1/boards/" + project.getBoard().getRemoteBoardId() /* + "?key=" + trelloApplicationKey + "&token=" + trelloAccessToken */);
        // TODO: Move the implementation details into the library
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setDoOutput(true);


        Map<String, String> parameters = new HashMap<>();
        parameters.put("key", trelloApplicationKey);
        parameters.put("token", trelloAccessToken);


        try(DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            outputStream.flush();
        }

        StringBuilder content;

        try (BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
            String line;
            content = new StringBuilder();
                while ((line = input.readLine()) != null) {
                    // Append each line of the response and separate them
                    content.append(line);
                    content.append(System.lineSeparator());
                }
        }

        return connection.getResponseMessage();
    }

    public static class ParameterStringBuilder {
        public static String getParamsString(Map<String, String> param) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            for (Map.Entry<String, String> entry : param.entrySet()) {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
            }

            String resultString = result.toString();

            return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
        }
    }
}
