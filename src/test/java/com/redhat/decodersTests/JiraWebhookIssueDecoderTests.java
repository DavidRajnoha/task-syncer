package com.redhat.decodersTests;

import com.redhat.tasksyncer.dao.entities.AbstractIssue;
import com.redhat.tasksyncer.dao.entities.AbstractRepository;
import com.redhat.tasksyncer.dao.entities.JiraRepository;
import com.redhat.tasksyncer.dao.entities.Project;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.decoders.JiraWebhookIssueDecoder;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


import java.io.IOException;


@RunWith(MockitoJUnitRunner.class)
public class JiraWebhookIssueDecoderTests {

    @InjectMocks
    private AbstractRepositoryRepository mockRepositoryRepository = Mockito.mock(AbstractRepositoryRepository.class);

    @InjectMocks
    private Project project = Mockito.mock(Project.class);

    private AbstractRepository repository;

    private Long projectId = 1L;


    private String jiraIssueCallback = "{\"timestamp\":1569402421534,\"webhookEvent\":\"jira:issue_created\",\"issue_event_type_name\":\"issue_created\",\"user\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/user?accountId=5c8a699f4c5068698da7ae9e\",\"accountId\":\"5c8a699f4c5068698da7ae9e\",\"emailAddress\":\"\\\"?\\\"\",\"avatarUrls\":{\"48x48\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=48&s=48\",\"24x24\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=24&s=24\",\"16x16\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=16&s=16\",\"32x32\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=32&s=32\"},\"displayName\":\"DavidRajnoha\",\"active\":true,\"timeZone\":\"Europe/Prague\",\"accountType\":\"atlassian\"},\"issue\":{\"id\":\"10002\",\"self\":\"https://drajnoha.atlassian.net/rest/api/2/issue/10002\",\"key\":\"JIR-3\",\"fields\":{\"statuscategorychangedate\":null,\"issuetype\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/issuetype/10002\",\"id\":\"10002\",\"description\":\"Asmall,distinctpiecesofwork.\",\"iconUrl\":\"https://drajnoha.atlassian.net/secure/viewavatar?size=medium&avatarId=10318&avatarType=issuetype\",\"name\":\"Task\",\"subtask\":false,\"avatarId\":10318},\"timespent\":null,\"project\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/project/10000\",\"id\":\"10000\",\"key\":\"JIR\",\"name\":\"JiraTesting\",\"projectTypeKey\":\"software\",\"simplified\":false,\"avatarUrls\":{\"48x48\":\"https://drajnoha.atlassian.net/secure/projectavatar?pid=10000&avatarId=10419\",\"24x24\":\"https://drajnoha.atlassian.net/secure/projectavatar?size=small&s=small&pid=10000&avatarId=10419\",\"16x16\":\"https://drajnoha.atlassian.net/secure/projectavatar?size=xsmall&s=xsmall&pid=10000&avatarId=10419\",\"32x32\":\"https://drajnoha.atlassian.net/secure/projectavatar?size=medium&s=medium&pid=10000&avatarId=10419\"}},\"fixVersions\":[],\"aggregatetimespent\":null,\"resolution\":null,\"resolutiondate\":null,\"workratio\":-1,\"lastViewed\":null,\"watches\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/issue/JIR-3/watchers\",\"watchCount\":0,\"isWatching\":true},\"created\":\"2019-09-25T11:07:01.437+0200\",\"customfield_10020\":null,\"customfield_10021\":null,\"customfield_10022\":null,\"customfield_10023\":null,\"priority\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/priority/3\",\"iconUrl\":\"https://drajnoha.atlassian.net/images/icons/priorities/medium.svg\",\"name\":\"Medium\",\"id\":\"3\"},\"labels\":[],\"customfield_10016\":null,\"customfield_10017\":null,\"customfield_10018\":{\"hasEpicLinkFieldDependency\":false,\"showField\":false,\"nonEditableReason\":{\"reason\":\"PLUGIN_LICENSE_ERROR\",\"message\":\"PortfolioforJiramustbelicensedfortheParentLinktobeavailable.\"}},\"customfield_10019\":\"0|i0000f:\",\"aggregatetimeoriginalestimate\":null,\"timeestimate\":null,\"versions\":[],\"issuelinks\":[],\"assignee\":null,\"updated\":\"2019-09-25T11:07:01.437+0200\",\"status\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/status/10000\",\"description\":\"\",\"iconUrl\":\"https://drajnoha.atlassian.net/\",\"name\":\"Backlog\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"New\"}},\"components\":[],\"timeoriginalestimate\":null,\"description\":null,\"customfield_10010\":null,\"customfield_10014\":null,\"timetracking\":{},\"customfield_10015\":null,\"customfield_10005\":null,\"customfield_10006\":null,\"security\":null,\"customfield_10007\":null,\"customfield_10008\":null,\"attachment\":[],\"customfield_10009\":null,\"aggregatetimeestimate\":null,\"summary\":\"Issuetestingwednesday\",\"creator\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/user?accountId=5c8a699f4c5068698da7ae9e\",\"name\":\"admin\",\"key\":\"admin\",\"accountId\":\"5c8a699f4c5068698da7ae9e\",\"emailAddress\":\"drajnoha@seznam.cz\",\"avatarUrls\":{\"48x48\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=48&s=48\",\"24x24\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=24&s=24\",\"16x16\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=16&s=16\",\"32x32\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=32&s=32\"},\"displayName\":\"DavidRajnoha\",\"active\":true,\"timeZone\":\"Europe/Prague\",\"accountType\":\"atlassian\"},\"subtasks\":[],\"reporter\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/user?accountId=5c8a699f4c5068698da7ae9e\",\"name\":\"admin\",\"key\":\"admin\",\"accountId\":\"5c8a699f4c5068698da7ae9e\",\"emailAddress\":\"drajnoha@seznam.cz\",\"avatarUrls\":{\"48x48\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=48&s=48\",\"24x24\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=24&s=24\",\"16x16\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=16&s=16\",\"32x32\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=32&s=32\"},\"displayName\":\"DavidRajnoha\",\"active\":true,\"timeZone\":\"Europe/Prague\",\"accountType\":\"atlassian\"},\"customfield_10000\":\"{}\",\"aggregateprogress\":{\"progress\":0,\"total\":0},\"customfield_10001\":null,\"customfield_10002\":null,\"customfield_10003\":null,\"customfield_10004\":null,\"environment\":null,\"duedate\":null,\"progress\":{\"progress\":0,\"total\":0},\"votes\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/issue/JIR-3/votes\",\"votes\":0,\"hasVoted\":false}}}}";

    @Before
    public void setup(){
        repository = new JiraRepository();
        Mockito.when(project.getId()).thenReturn(projectId);
        Mockito.when(mockRepositoryRepository.findByRepositoryNameAndProject_Id("JIR",projectId)).thenReturn(repository);


    }

/*
 *   Test used while development for testing the decoder.requestToInput method - this method is now private and its work is covered in the second test in the class.
 *
 */
//    @Test
//    public void whenHttpRequestIsPassed_thenJSONObjectIsCreated() throws JSONException, org.json.JSONException, IOException {
//        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
//
//        servletRequest.addHeader("key", "value");
//        servletRequest.setRequestURI("/add/jira/hook/");
//
//        String json = "{\"self\":\"https://jira.atlassian.com/rest/api/2/issue/10148/comment/252789\",\"id\":\"252789\",\"author\":{\"self\":\"https://jira.atlassian.com/rest/api/2/user?username=brollins\",\"name\":\"brollins\",\"key\":\"brollins\",\"emailAddress\":\"brollins at atlassian dot com\",\"avatarUrls\":{\"48x48\":\"https://avatar-cdn.atlassian.com/218cf7092ab4d9cb819cc4f0488c4d54?d=mm&s=48\",\"24x24\":\"https://avatar-cdn.atlassian.com/218cf7092ab4d9cb819cc4f0488c4d54?d=mm&s=24\",\"16x16\":\"https://avatar-cdn.atlassian.com/218cf7092ab4d9cb819cc4f0488c4d54?d=mm&s=16\",\"32x32\":\"https://avatar-cdn.atlassian.com/218cf7092ab4d9cb819cc4f0488c4d54?d=mm&s=32\"},\"displayName\":\"Bryan Rollins\",\"active\":true,\"timeZone\":\"Australia/Sydney\"},\"body\":\"Just in time for the Summit keynote this morning!\",\"updateAuthor\":{\"self\":\"https://jira.atlassian.com/rest/api/2/user?username=brollins\",\"name\":\"brollins\",\"key\":\"brollins\",\"emailAddress\":\"brollins at atlassian dot com\",\"avatarUrls\":{\"48x48\":\"https://avatar-cdn.atlassian.com/218cf7092ab4d9cb819cc4f0488c4d54?d=mm&s=48\",\"24x24\":\"https://avatar-cdn.atlassian.com/218cf7092ab4d9cb819cc4f0488c4d54?d=mm&s=24\",\"16x16\":\"https://avatar-cdn.atlassian.com/218cf7092ab4d9cb819cc4f0488c4d54?d=mm&s=16\",\"32x32\":\"https://avatar-cdn.atlassian.com/218cf7092ab4d9cb819cc4f0488c4d54?d=mm&s=32\"},\"displayName\":\"Bryan Rollins\",\"active\":true,\"timeZone\":\"Australia/Sydney\"},\"created\":\"2011-06-07T15:31:26.805+0000\",\"updated\":\"2011-06-07T15:31:26.805+0000\"}";
//        //ObjectMapper mapper = new ObjectMapper();
//        servletRequest.setContent(json.getBytes());
//
//        JiraWebhookIssueDecoder decoder = new JiraWebhookIssueDecoder();
//        JSONObject jsonObject = decoder.requestToInput(servletRequest);
//
//         assertThat(jsonObject.getString("id")).isEqualTo("252789");
//         assertThat(jsonObject.getJSONObject("author").getString("key")).isEqualTo("brollins");
//    }


    @Test
    public void whenDecodeIsCalled_thenAbstractIssueIsCreated() throws Exception {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader("key", "value");
        servletRequest.setContent(jiraIssueCallback.getBytes());

        JiraWebhookIssueDecoder decoder = new JiraWebhookIssueDecoder();
        AbstractIssue returnedIssue = decoder.decode(servletRequest, project, mockRepositoryRepository);

        assertThat(returnedIssue.getTitle()).isEqualTo("Issuetestingwednesday");
        assertThat(returnedIssue.getRepository()).isEqualTo(repository);
        assertThat(returnedIssue.getIssueType()).isEqualTo(IssueType.JIRA);
    }
}
