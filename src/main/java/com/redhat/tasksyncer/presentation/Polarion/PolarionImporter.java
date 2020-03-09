package com.redhat.tasksyncer.presentation.Polarion;


import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class PolarionImporter {

    private RestTemplate restTemplate;

    // It is necessary to use configured restTemplate to accept ssl calls without verification
    @Autowired
    public PolarionImporter(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public String importToPolarion(Document document, String url, String type, String username, String password) {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        String filename = type + ".xml";

        // Convert dom4j document to string and then to bytes, then create ByteArrayResource to send the data via
        // Rest template
        ByteArrayResource fileContentAsResource = new ByteArrayResource(document.asXML().getBytes()) {
            @Override
            public String getFilename() {
                // Filename has to be returned in order to request to work
                return filename;
            }
        };

        map.add("name", filename);
        map.add("filename", filename);
        map.add("file", fileContentAsResource);

        // Set data type and authentication in headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBasicAuth(username, password);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

        ResponseEntity<String> result = restTemplate.postForEntity(url, requestEntity, String.class);

        return result.getBody();
    }

}
