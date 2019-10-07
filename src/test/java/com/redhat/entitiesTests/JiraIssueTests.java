package com.redhat.entitiesTests;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.jira.rest.client.api.domain.User;
import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.GitlabIssue;
import com.redhat.tasksyncer.dao.entities.JiraIssue;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class JiraIssueTests {

    String jsonInputIssueChanged;
    JSONObject jsonObject;
    Issue jiraIssueTwo;

    Issue jiraIssueOne;
    List<String> labels;

    String label_name = "label";
    String assigneeName = "assignee";
    String titleOne = "Title_one";
    String description = "description";
    Date dueDate = new Date();
    Date createdAt = new Date();
    Date closedAt = new Date();


    @Before
    public void setup() throws JSONException {
        jsonInputIssueChanged = "{\"timestamp\":1569402421534,\"webhookEvent\":\"jira:issue_created\",\"issue_event_type_name\":\"issue_created\",\"user\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/user?accountId=5c8a699f4c5068698da7ae9e\",\"accountId\":\"5c8a699f4c5068698da7ae9e\",\"emailAddress\":\"\\\"?\\\"\",\"avatarUrls\":{\"48x48\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=48&s=48\",\"24x24\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=24&s=24\",\"16x16\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=16&s=16\",\"32x32\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=32&s=32\"},\"displayName\":\"DavidRajnoha\",\"active\":true,\"timeZone\":\"Europe/Prague\",\"accountType\":\"atlassian\"},\"issue\":{\"id\":\"10002\",\"self\":\"https://drajnoha.atlassian.net/rest/api/2/issue/10002\",\"key\":\"JIR-3\",\"fields\":{\"statuscategorychangedate\":null,\"issuetype\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/issuetype/10002\",\"id\":\"10002\",\"description\":\"Asmall,distinctpiecesofwork.\",\"iconUrl\":\"https://drajnoha.atlassian.net/secure/viewavatar?size=medium&avatarId=10318&avatarType=issuetype\",\"name\":\"Task\",\"subtask\":false,\"avatarId\":10318},\"timespent\":null,\"project\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/project/10000\",\"id\":\"10000\",\"key\":\"JIR\",\"name\":\"JiraTesting\",\"projectTypeKey\":\"software\",\"simplified\":false,\"avatarUrls\":{\"48x48\":\"https://drajnoha.atlassian.net/secure/projectavatar?pid=10000&avatarId=10419\",\"24x24\":\"https://drajnoha.atlassian.net/secure/projectavatar?size=small&s=small&pid=10000&avatarId=10419\",\"16x16\":\"https://drajnoha.atlassian.net/secure/projectavatar?size=xsmall&s=xsmall&pid=10000&avatarId=10419\",\"32x32\":\"https://drajnoha.atlassian.net/secure/projectavatar?size=medium&s=medium&pid=10000&avatarId=10419\"}},\"fixVersions\":[],\"aggregatetimespent\":null,\"resolution\":null,\"resolutiondate\":null,\"workratio\":-1,\"lastViewed\":null,\"watches\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/issue/JIR-3/watchers\",\"watchCount\":0,\"isWatching\":true},\"created\":\"2019-09-25T11:07:01.437+0200\",\"customfield_10020\":null,\"customfield_10021\":null,\"customfield_10022\":null,\"customfield_10023\":null,\"priority\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/priority/3\",\"iconUrl\":\"https://drajnoha.atlassian.net/images/icons/priorities/medium.svg\",\"name\":\"Medium\",\"id\":\"3\"},\"labels\":[],\"customfield_10016\":null,\"customfield_10017\":null,\"customfield_10018\":{\"hasEpicLinkFieldDependency\":false,\"showField\":false,\"nonEditableReason\":{\"reason\":\"PLUGIN_LICENSE_ERROR\",\"message\":\"PortfolioforJiramustbelicensedfortheParentLinktobeavailable.\"}},\"customfield_10019\":\"0|i0000f:\",\"aggregatetimeoriginalestimate\":null,\"timeestimate\":null,\"versions\":[],\"issuelinks\":[],\"assignee\":null,\"updated\":\"2019-09-25T11:07:01.437+0200\",\"status\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/status/10000\",\"description\":\"\",\"iconUrl\":\"https://drajnoha.atlassian.net/\",\"name\":\"Backlog\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"New\"}},\"components\":[],\"timeoriginalestimate\":null,\"description\":null,\"customfield_10010\":null,\"customfield_10014\":null,\"timetracking\":{},\"customfield_10015\":null,\"customfield_10005\":null,\"customfield_10006\":null,\"security\":null,\"customfield_10007\":null,\"customfield_10008\":null,\"attachment\":[],\"customfield_10009\":null,\"aggregatetimeestimate\":null,\"summary\":\"Issuetestingwednesday\",\"creator\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/user?accountId=5c8a699f4c5068698da7ae9e\",\"name\":\"admin\",\"key\":\"admin\",\"accountId\":\"5c8a699f4c5068698da7ae9e\",\"emailAddress\":\"drajnoha@seznam.cz\",\"avatarUrls\":{\"48x48\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=48&s=48\",\"24x24\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=24&s=24\",\"16x16\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=16&s=16\",\"32x32\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=32&s=32\"},\"displayName\":\"DavidRajnoha\",\"active\":true,\"timeZone\":\"Europe/Prague\",\"accountType\":\"atlassian\"},\"subtasks\":[],\"reporter\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/user?accountId=5c8a699f4c5068698da7ae9e\",\"name\":\"admin\",\"key\":\"admin\",\"accountId\":\"5c8a699f4c5068698da7ae9e\",\"emailAddress\":\"drajnoha@seznam.cz\",\"avatarUrls\":{\"48x48\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=48&s=48\",\"24x24\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=24&s=24\",\"16x16\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=16&s=16\",\"32x32\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=32&s=32\"},\"displayName\":\"DavidRajnoha\",\"active\":true,\"timeZone\":\"Europe/Prague\",\"accountType\":\"atlassian\"},\"customfield_10000\":\"{}\",\"aggregateprogress\":{\"progress\":0,\"total\":0},\"customfield_10001\":null,\"customfield_10002\":null,\"customfield_10003\":null,\"customfield_10004\":null,\"environment\":null,\"duedate\":null,\"progress\":{\"progress\":0,\"total\":0},\"votes\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/issue/JIR-3/votes\",\"votes\":0,\"hasVoted\":false}}}}";
        jsonObject = new JSONObject(jsonInputIssueChanged);
    }

    @Test
    public void whenJSONtoIssueDecoderIsUsed_thenCorrectAbstractIssueIsCreated() throws JSONException {
        AbstractIssue issue = JiraIssue.ObjectToJiraIssueConverter.convert(jsonObject);
        assertThat(issue.getRemoteIssueId()).isEqualTo("10002");
        assertThat(issue.getTitle()).isEqualTo("Issuetestingwednesday");
        assertThat(issue.getDescription()).isEqualTo("null");
        assertThat(issue.getState()).isEqualTo(AbstractIssue.STATE_OPENED);
    }

    @Test
    public void whenDecodingIssueFromJRJCIssue_thenAbstractIssueIsCreated(){
        // Preparation
        Status status = new Status(URI.create("uri"), 1L, "opened", "issue is open", null);
        Map<String, URI> avatarURI = new HashMap<String, URI>();
        avatarURI.put("48x48", URI.create("avatar"));

        User assignee = new User(URI.create("assignee"), assigneeName, "David", "dr@email.com", null, avatarURI, null);

        Set<String> labels = new HashSet<>();
        labels.add(label_name);
        labels.add("label_two");


        jiraIssueOne = new Issue(titleOne, null, null, 2L, null, null, status, description, null, null, null, null, assignee,
                new DateTime(createdAt), null, new DateTime(dueDate), null, null, null, null, null, new HashSet<>(), null, null,
                null, null, null, null, null, null, null, labels);


        // Test
        AbstractIssue convertedIssue = JiraIssue.ObjectToJiraIssueConverter.convert(jiraIssueOne);

        // Assertion
        assertThat(convertedIssue.getIssueType()).isEqualTo(IssueType.JIRA);
        assertThat(convertedIssue.getTitle()).isEqualTo(titleOne);
        assertThat(convertedIssue.getState()).isEqualTo(AbstractIssue.STATE_OPENED);
        assertThat(convertedIssue.getDescription()).isEqualTo(description);
        assertThat(convertedIssue.getDueDate()).isEqualTo(dueDate);
        assertThat(convertedIssue.getCreatedAt()).isEqualTo(createdAt);
//        assertThat(convertedIssue.getClosedAt()).isEqualTo(closedAt);
        assertThat(convertedIssue.getAssignee()).isEqualTo(assigneeName);
//        assertThat(convertedIssue.getClosedBy()).isEqualTo(assigneeName);
        assertThat(convertedIssue.getRemoteIssueId()).isEqualTo(String.valueOf(2));


        assert(convertedIssue.getLabels().contains(label_name));
    }

    @Test
    public void whenDecodingIssueWithNullValuesButId_thenNoExceptionIsThrown(){
        Status status = new Status(URI.create("uri"), 1L, "n", "d", null);
        jiraIssueTwo = new Issue(null, null, null, 2L, null, null, status, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, new HashSet<>(), null, null,
                null, null, null, null, null, null, null, null);

        AbstractIssue convertedIssue = JiraIssue.ObjectToJiraIssueConverter.convert(jiraIssueTwo);

        assertThat(convertedIssue.getRemoteIssueId()).isEqualTo(String.valueOf(2L));

    }
}
