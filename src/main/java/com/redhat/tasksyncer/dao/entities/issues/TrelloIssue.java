package com.redhat.tasksyncer.dao.entities.issues;

import com.julienvey.trello.domain.Card;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import com.redhat.tasksyncer.exceptions.TrelloCalllbackNotAboutCardException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author David Rajnoha
 * */

@Entity
public class TrelloIssue extends AbstractIssue {
    public TrelloIssue() {
        super(IssueType.TRELLO);
    }


    // TODO: upgrade the library julienvy.trello so the commented fields can be assigned
    public static class ObjectToTrelloIssueConvertor {
        public static TrelloIssue convert(Card trelloCard, Map<String, String> collumnMapping){
            TrelloIssue trelloIssue = new TrelloIssue();

            trelloIssue.setTitle(trelloCard.getName());
            trelloIssue.setDescription(trelloCard.getDesc());
            trelloIssue.setRemoteIssueId(trelloCard.getIdShort());
            //trelloIssue.setAssignee();
            //trelloIssue.setCreatedAt(trelloCard.get);
            trelloIssue.setDueDate(trelloCard.getDue());
            //trelloIssue.setComments;
            //trelloIssue.setState();
            Set<String> labels = new HashSet<>();
            Optional.ofNullable(trelloCard.getLabels()).ifPresent(tlabels -> tlabels.forEach(label -> {
                labels.add(label.getName());
            }));
            trelloIssue.setLabel(labels);


            // sets state based on mapping
            trelloIssue.setState(collumnMapping.get(trelloCard.getIdList()));

            return trelloIssue;
        }


        // TODO: Wait till I know the precise structure of the webhook callback
        public static AbstractIssue convert(JSONObject input, Map<String, String> columnMapping) throws JSONException, TrelloCalllbackNotAboutCardException {
            AbstractIssue trelloIssue = new TrelloIssue();

            if (input.getJSONObject("action").getJSONObject("data").has("card")) {

                trelloIssue.setTitle(input.getJSONObject("action").getJSONObject("data").getJSONObject("card").get("name").toString());
                trelloIssue.setRemoteIssueId(input.getJSONObject("action").getJSONObject("data").getJSONObject("card").get("idShort").toString());
                trelloIssue.setDescription(input.getJSONObject("action").getJSONObject("data").getJSONObject("card").has("desc") ?
                        input.getJSONObject("action").getJSONObject("data").getJSONObject("card").get("desc").toString() : null);
                trelloIssue.setDueDate(input.getJSONObject("action").getJSONObject("data").getJSONObject("card").has("date") ?
                        parseDate(input.getJSONObject("action").getJSONObject("data").getJSONObject("card").get("date").toString()) : null);
                trelloIssue.setAssignee(input.getJSONObject("action").getJSONObject("data").has("member") ?
                        input.getJSONObject("action").getJSONObject("data").getJSONObject("member").get("name").toString() : null);
                if (input.getJSONObject("action").getJSONObject("data").has("label")) {
                    Set<String> labels = trelloIssue.getLabels();
                    labels.add(input.getJSONObject("action").getJSONObject("data").getJSONObject("member").get("name").toString());
                    trelloIssue.setLabel(labels);
                }

                // TODO: set state according to webhook
                if (input.getJSONObject("action").getJSONObject("data").has("listAfter")){
                    trelloIssue.setState(columnMapping.get(
                            input.getJSONObject("action").getJSONObject("data").getJSONObject("listAfter").get("id").toString()));
                }


                return trelloIssue;
            } else {
                throw new TrelloCalllbackNotAboutCardException();
            }
        }

        private static Date parseDate(String stringDate){
            DateTimeFormatter trelloDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            LocalDate date = LocalDate.parse(stringDate, trelloDateFormatter);
            return Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        }
    }
}
