package com.redhat.unit.polarionTests;

import com.redhat.tasksyncer.presentation.Polarion.PolarionImporter;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.Objects;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;


@RunWith(MockitoJUnitRunner.class)
public class PolarionImporterTest {

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<HttpEntity<MultiValueMap<String, Object>>> argumentCaptor;

    @InjectMocks
    private PolarionImporter polarionImporter;

    private String type = "name";

    private Document document;

    @Before
    public void setup(){
        document = DocumentHelper.createDocument();
        document.setName("doc");

        Mockito.doAnswer(i -> ResponseEntity.ok().body("Something")).when(restTemplate).postForEntity(anyString(),
                argumentCaptor.capture(), any());
    }

    @Test
    public void importToPolarionSendsCorrectHttpEntity(){
        polarionImporter.importToPolarion(document, "url", type, "username", "username");

        Mockito.verify(restTemplate).postForEntity(anyString(), argumentCaptor.capture(), any());

        HttpEntity<MultiValueMap<String, Object>> postedEntity = argumentCaptor.getValue();

        MultiValueMap<String, Object> body = Objects.requireNonNull(postedEntity.getBody());

        Object postedName = body.get("name").get(0);
        assertThat(postedName).isInstanceOf(String.class);
        assertThat((String) postedName).isEqualTo(type + ".xml");

        Object postedFileName = body.get("filename").get(0);
        assertThat(postedFileName).isInstanceOf(String.class);
        assertThat((String) postedFileName).isEqualTo(type + ".xml");

        Object postedFile = body.get("file").get(0);
        assertThat(postedFile).isInstanceOf(ByteArrayResource.class);
        assertThat((ByteArrayResource) postedFile).isEqualTo(new ByteArrayResource(document.asXML().getBytes()));
    }

    @Test
    public void headersAreOfCorrecType(){
        polarionImporter.importToPolarion(document, "url", type, "username", "username");

        Mockito.verify(restTemplate).postForEntity(anyString(), argumentCaptor.capture(), any());

        HttpEntity<MultiValueMap<String, Object>> postedEntity = argumentCaptor.getValue();
        HttpHeaders headers = postedEntity.getHeaders();

        assertThat(headers.getContentType()).isEqualTo(MediaType.MULTIPART_FORM_DATA);
    }

}
