package com.redhat.decodersTests;

import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.decoders.AbstractWebhookIssueDecoder;
import com.redhat.tasksyncer.decoders.JiraWebhookIssueDecoder;
import com.redhat.tasksyncer.decoders.TrelloWebhookIssueDecoder;
import org.codehaus.jettison.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


import java.io.IOException;


@RunWith(MockitoJUnitRunner.class)
public class WebhookIssueDecoderTests {

    @InjectMocks
    private AbstractRepositoryRepository mockRepositoryRepository = Mockito.mock(AbstractRepositoryRepository.class);

    @InjectMocks
    private Project project = Mockito.mock(Project.class);

    private AbstractRepository repository;

    private Long projectId = 1L;


    private String jiraIssueCallback = "{\"timestamp\":1569402421534,\"webhookEvent\":\"jira:issue_created\",\"issue_event_type_name\":\"issue_created\",\"user\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/user?accountId=5c8a699f4c5068698da7ae9e\",\"accountId\":\"5c8a699f4c5068698da7ae9e\",\"emailAddress\":\"\\\"?\\\"\",\"avatarUrls\":{\"48x48\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=48&s=48\",\"24x24\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=24&s=24\",\"16x16\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=16&s=16\",\"32x32\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=32&s=32\"},\"displayName\":\"DavidRajnoha\",\"active\":true,\"timeZone\":\"Europe/Prague\",\"accountType\":\"atlassian\"},\"issue\":{\"id\":\"10002\",\"self\":\"https://drajnoha.atlassian.net/rest/api/2/issue/10002\",\"key\":\"JIR-3\",\"fields\":{\"statuscategorychangedate\":null,\"issuetype\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/issuetype/10002\",\"id\":\"10002\",\"description\":\"Asmall,distinctpiecesofwork.\",\"iconUrl\":\"https://drajnoha.atlassian.net/secure/viewavatar?size=medium&avatarId=10318&avatarType=issuetype\",\"name\":\"Task\",\"subtask\":false,\"avatarId\":10318},\"timespent\":null,\"project\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/project/10000\",\"id\":\"10000\",\"key\":\"JIR\",\"name\":\"JiraTesting\",\"projectTypeKey\":\"software\",\"simplified\":false,\"avatarUrls\":{\"48x48\":\"https://drajnoha.atlassian.net/secure/projectavatar?pid=10000&avatarId=10419\",\"24x24\":\"https://drajnoha.atlassian.net/secure/projectavatar?size=small&s=small&pid=10000&avatarId=10419\",\"16x16\":\"https://drajnoha.atlassian.net/secure/projectavatar?size=xsmall&s=xsmall&pid=10000&avatarId=10419\",\"32x32\":\"https://drajnoha.atlassian.net/secure/projectavatar?size=medium&s=medium&pid=10000&avatarId=10419\"}},\"fixVersions\":[],\"aggregatetimespent\":null,\"resolution\":null,\"resolutiondate\":null,\"workratio\":-1,\"lastViewed\":null,\"watches\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/issue/JIR-3/watchers\",\"watchCount\":0,\"isWatching\":true},\"created\":\"2019-09-25T11:07:01.437+0200\",\"customfield_10020\":null,\"customfield_10021\":null,\"customfield_10022\":null,\"customfield_10023\":null,\"priority\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/priority/3\",\"iconUrl\":\"https://drajnoha.atlassian.net/images/icons/priorities/medium.svg\",\"name\":\"Medium\",\"id\":\"3\"},\"labels\":[],\"customfield_10016\":null,\"customfield_10017\":null,\"customfield_10018\":{\"hasEpicLinkFieldDependency\":false,\"showField\":false,\"nonEditableReason\":{\"reason\":\"PLUGIN_LICENSE_ERROR\",\"message\":\"PortfolioforJiramustbelicensedfortheParentLinktobeavailable.\"}},\"customfield_10019\":\"0|i0000f:\",\"aggregatetimeoriginalestimate\":null,\"timeestimate\":null,\"versions\":[],\"issuelinks\":[],\"assignee\":null,\"updated\":\"2019-09-25T11:07:01.437+0200\",\"status\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/status/10000\",\"description\":\"\",\"iconUrl\":\"https://drajnoha.atlassian.net/\",\"name\":\"Backlog\",\"id\":\"10000\",\"statusCategory\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/statuscategory/2\",\"id\":2,\"key\":\"new\",\"colorName\":\"blue-gray\",\"name\":\"New\"}},\"components\":[],\"timeoriginalestimate\":null,\"description\":null,\"customfield_10010\":null,\"customfield_10014\":null,\"timetracking\":{},\"customfield_10015\":null,\"customfield_10005\":null,\"customfield_10006\":null,\"security\":null,\"customfield_10007\":null,\"customfield_10008\":null,\"attachment\":[],\"customfield_10009\":null,\"aggregatetimeestimate\":null,\"summary\":\"Issuetestingwednesday\",\"creator\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/user?accountId=5c8a699f4c5068698da7ae9e\",\"name\":\"admin\",\"key\":\"admin\",\"accountId\":\"5c8a699f4c5068698da7ae9e\",\"emailAddress\":\"drajnoha@seznam.cz\",\"avatarUrls\":{\"48x48\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=48&s=48\",\"24x24\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=24&s=24\",\"16x16\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=16&s=16\",\"32x32\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=32&s=32\"},\"displayName\":\"DavidRajnoha\",\"active\":true,\"timeZone\":\"Europe/Prague\",\"accountType\":\"atlassian\"},\"subtasks\":[],\"reporter\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/user?accountId=5c8a699f4c5068698da7ae9e\",\"name\":\"admin\",\"key\":\"admin\",\"accountId\":\"5c8a699f4c5068698da7ae9e\",\"emailAddress\":\"drajnoha@seznam.cz\",\"avatarUrls\":{\"48x48\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=48&s=48\",\"24x24\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=24&s=24\",\"16x16\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=16&s=16\",\"32x32\":\"https://secure.gravatar.com/avatar/6ed85e64767d7058bc8ea52dce47f9e2?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FDR-6.png&size=32&s=32\"},\"displayName\":\"DavidRajnoha\",\"active\":true,\"timeZone\":\"Europe/Prague\",\"accountType\":\"atlassian\"},\"customfield_10000\":\"{}\",\"aggregateprogress\":{\"progress\":0,\"total\":0},\"customfield_10001\":null,\"customfield_10002\":null,\"customfield_10003\":null,\"customfield_10004\":null,\"environment\":null,\"duedate\":null,\"progress\":{\"progress\":0,\"total\":0},\"votes\":{\"self\":\"https://drajnoha.atlassian.net/rest/api/2/issue/JIR-3/votes\",\"votes\":0,\"hasVoted\":false}}}}";
    private String trelloIssueCallback = "{\"model\":{\"id\":\"5d665532a3b61963b8a4dc51\",\"name\":\"TaskSyncer\",\"desc\":\"\",\"descData\":null,\"closed\":false,\"idOrganization\":null,\"pinned\":false,\"url\":\"https://trello.com/b/zwQYthQw/task-syncer\",\"shortUrl\":\"https://trello.com/b/zwQYthQw\",\"prefs\":{\"permissionLevel\":\"private\",\"hideVotes\":false,\"voting\":\"disabled\",\"comments\":\"members\",\"invitations\":\"members\",\"selfJoin\":true,\"cardCovers\":true,\"isTemplate\":false,\"cardAging\":\"regular\",\"calendarFeedEnabled\":false,\"background\":\"5d61fb3140493340077e0857\",\"backgroundImage\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/2560x1707/733ef9444105a5ab90825b99ebe74a9e/photo-1566578143640-f0c819f43c8e\",\"backgroundImageScaled\":[{\"width\":140,\"height\":93,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/140x93/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":256,\"height\":171,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/256x171/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":480,\"height\":320,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/480x320/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":960,\"height\":640,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/960x640/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":1024,\"height\":683,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/1024x683/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":2048,\"height\":1366,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/2048x1366/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":1280,\"height\":854,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/1280x854/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":1920,\"height\":1280,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/1920x1280/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":2400,\"height\":1600,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/2400x1600/92999ff59cc42ad94bd9b80b563aec91/photo-1566578143640-f0c819f43c8e.jpg\"},{\"width\":2560,\"height\":1707,\"url\":\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/2560x1707/733ef9444105a5ab90825b99ebe74a9e/photo-1566578143640-f0c819f43c8e\"}],\"backgroundTile\":false,\"backgroundBrightness\":\"dark\",\"backgroundBottomColor\":\"#2c3630\",\"backgroundTopColor\":\"#b0b8be\",\"canBePublic\":true,\"canBeEnterprise\":true,\"canBeOrg\":true,\"canBePrivate\":true,\"canInvite\":true},\"labelNames\":{\"green\":\"\",\"yellow\":\"\",\"orange\":\"\",\"red\":\"\",\"purple\":\"\",\"blue\":\"\",\"sky\":\"\",\"lime\":\"\",\"pink\":\"\",\"black\":\"\"}},\"action\":{\"id\":\"5d9b3a202e23192f906a6992\",\"idMemberCreator\":\"5c8a806df33daf095b1a1d02\",\"data\":{\"card\":{\"id\":\"5d9b3a202e23192f906a6991\",\"name\":\"TestingCardCallback\",\"idShort\":23,\"shortLink\":\"s1GAW4kG\"},\"list\":{\"id\":\"5d665541334fc263e03baae4\",\"name\":\"Backlog\"},\"board\":{\"id\":\"5d665532a3b61963b8a4dc51\",\"name\":\"TaskSyncer\",\"shortLink\":\"zwQYthQw\"}},\"type\":\"createCard\",\"date\":\"2019-10-07T13:14:08.291Z\",\"limits\":{},\"display\":{\"translationKey\":\"action_create_card\",\"entities\":{\"card\":{\"type\":\"card\",\"id\":\"5d9b3a202e23192f906a6991\",\"shortLink\":\"s1GAW4kG\",\"text\":\"TestingCardCallback\"},\"list\":{\"type\":\"list\",\"id\":\"5d665541334fc263e03baae4\",\"text\":\"Backlog\"},\"memberCreator\":{\"type\":\"member\",\"id\":\"5c8a806df33daf095b1a1d02\",\"username\":\"drajnoha\",\"text\":\"drajnoha\"}}},\"memberCreator\":{\"id\":\"5c8a806df33daf095b1a1d02\",\"activityBlocked\":false,\"avatarHash\":\"\",\"avatarUrl\":null,\"fullName\":\"drajnoha\",\"idMemberReferrer\":null,\"initials\":\"D\",\"nonPublic\":{},\"nonPublicAvailable\":false,\"username\":\"drajnoha\"}}}";
    @Before
    public void setup(){
        Mockito.when(project.getId()).thenReturn(projectId);
    }


    @Test
    public void whenHttpRequestIsPassed_thenJSONObjectIsCreated() throws JSONException, org.json.JSONException, IOException {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();

        servletRequest.addHeader("key", "value");
        servletRequest.setRequestURI("/add/jira/hook/");

        String json = "{\"self\":\"https://jira.atlassian.com/rest/api/2/issue/10148/comment/252789\",\"id\":\"252789\",\"author\":{\"self\":\"https://jira.atlassian.com/rest/api/2/user?username=brollins\",\"name\":\"brollins\",\"key\":\"brollins\",\"emailAddress\":\"brollins at atlassian dot com\",\"avatarUrls\":{\"48x48\":\"https://avatar-cdn.atlassian.com/218cf7092ab4d9cb819cc4f0488c4d54?d=mm&s=48\",\"24x24\":\"https://avatar-cdn.atlassian.com/218cf7092ab4d9cb819cc4f0488c4d54?d=mm&s=24\",\"16x16\":\"https://avatar-cdn.atlassian.com/218cf7092ab4d9cb819cc4f0488c4d54?d=mm&s=16\",\"32x32\":\"https://avatar-cdn.atlassian.com/218cf7092ab4d9cb819cc4f0488c4d54?d=mm&s=32\"},\"displayName\":\"Bryan Rollins\",\"active\":true,\"timeZone\":\"Australia/Sydney\"},\"body\":\"Just in time for the Summit keynote this morning!\",\"updateAuthor\":{\"self\":\"https://jira.atlassian.com/rest/api/2/user?username=brollins\",\"name\":\"brollins\",\"key\":\"brollins\",\"emailAddress\":\"brollins at atlassian dot com\",\"avatarUrls\":{\"48x48\":\"https://avatar-cdn.atlassian.com/218cf7092ab4d9cb819cc4f0488c4d54?d=mm&s=48\",\"24x24\":\"https://avatar-cdn.atlassian.com/218cf7092ab4d9cb819cc4f0488c4d54?d=mm&s=24\",\"16x16\":\"https://avatar-cdn.atlassian.com/218cf7092ab4d9cb819cc4f0488c4d54?d=mm&s=16\",\"32x32\":\"https://avatar-cdn.atlassian.com/218cf7092ab4d9cb819cc4f0488c4d54?d=mm&s=32\"},\"displayName\":\"Bryan Rollins\",\"active\":true,\"timeZone\":\"Australia/Sydney\"},\"created\":\"2011-06-07T15:31:26.805+0000\",\"updated\":\"2011-06-07T15:31:26.805+0000\"}";
        //ObjectMapper mapper = new ObjectMapper();
        servletRequest.setContent(json.getBytes());

        JSONObject jsonObject = AbstractWebhookIssueDecoder.RequestToJsonDecoder.toJson(servletRequest);

         assertThat(jsonObject.getString("id")).isEqualTo("252789");
         assertThat(jsonObject.getJSONObject("author").getString("key")).isEqualTo("brollins");
    }


    @Test
    public void whenJIRADecodeIsCalled_thenAbstractIssueIsCreated() throws Exception {
        // Before
        repository = new JiraRepository();
        Mockito.when(mockRepositoryRepository.findByRepositoryNameAndProject_Id("JIR",projectId)).thenReturn(repository);

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader("key", "value");
        servletRequest.setContent(jiraIssueCallback.getBytes());

        // Test
        JiraWebhookIssueDecoder decoder = new JiraWebhookIssueDecoder();

        AbstractIssue correctAnswers = new JiraIssue();
        correctAnswers.setRemoteIssueId("10002");
        correctAnswers.setRepository(repository);
        correctAnswers.setTitle("Issuetestingwednesday");

        whenDecodeIsCalled_thenAbstractIssueIsCreated(decoder, mockRepositoryRepository, servletRequest, correctAnswers);


    }

   // TRELLO DECODER
    @Test
    public void whenTrelloDecodeIsCalled_thenAbstractIssueIsCreated() throws Exception {
        // Before
        repository = new TrelloRepository();
        Mockito.when(mockRepositoryRepository.findByRepositoryNameAndProject_Id("5d665532a3b61963b8a4dc51", projectId))
                .thenReturn(repository);

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setContent(trelloIssueCallback.getBytes());

        AbstractWebhookIssueDecoder issueDecoder = new TrelloWebhookIssueDecoder();

        AbstractIssue correctAnswers = new TrelloIssue();
        correctAnswers.setRemoteIssueId("23");
        correctAnswers.setRepository(repository);
        correctAnswers.setTitle("TestingCardCallback");


        whenDecodeIsCalled_thenAbstractIssueIsCreated(issueDecoder, mockRepositoryRepository, servletRequest, correctAnswers);
    }

    private void whenDecodeIsCalled_thenAbstractIssueIsCreated(AbstractWebhookIssueDecoder issueDecoder,
                                                               AbstractRepositoryRepository mockRepositoryRepository,
                                                               MockHttpServletRequest servletRequest,
                                                               AbstractIssue correctConvertedIssue) throws Exception {

        // Test
        AbstractIssue returnedIssue =  issueDecoder.decode(servletRequest, project, mockRepositoryRepository);


        assertThat(returnedIssue.getRemoteIssueId()).isEqualTo(correctConvertedIssue.getRemoteIssueId());
        assertThat(returnedIssue.getRepository()).isEqualTo(correctConvertedIssue.getRepository());
        assertThat(returnedIssue.getTitle()).isEqualTo(correctConvertedIssue.getTitle());
        assertThat(returnedIssue.getIssueType()).isEqualTo(correctConvertedIssue.getIssueType());
    }
}
