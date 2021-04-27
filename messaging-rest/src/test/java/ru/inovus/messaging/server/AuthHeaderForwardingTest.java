package ru.inovus.messaging.server;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true",
                "server.port=8456",
                "messaging.user-provider-url=http://localhost:${server.port}",
                "cxf.jaxrs.client.classes-scan-packages=ru.i_novus.messaging.api",
                "spring.cloud.consul.config.enabled=false",
                "spring.liquibase.contexts=test"})
@EnableEmbeddedPg
@TestPropertySource("classpath:application.properties")
public class AuthHeaderForwardingTest {

    @LocalServerPort
    private int port;

    @Test
    public void testHeaderForwarding() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", Collections.singletonList("Bearer test_token"));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity resp = restTemplate.exchange("http://localhost:" + port + "/api/users", HttpMethod.GET, entity, Object.class);
        MatcherAssert.assertThat(resp.getStatusCode(), Matchers.is(HttpStatus.OK));
    }
}