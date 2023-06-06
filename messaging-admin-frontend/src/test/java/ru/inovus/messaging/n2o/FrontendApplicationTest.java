package ru.inovus.messaging.n2o;

import net.n2oapp.framework.engine.data.rest.SpringRestDataProviderEngine;
import net.n2oapp.security.auth.context.SpringSecurityUserContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FrontendApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class FrontendApplicationTest {

    @Autowired
    private SpringRestDataProviderEngine springRestDataProviderEngine;

    @Autowired
    private SpringSecurityUserContext springSecurityUserContext;

    @Test
    void contextLoads() {
    }

    @Test
    void authorizationHeaderTest() throws Exception {
        RestTemplate restTemplate = getN2oRestTemplate();
        mockSecurityContext();
        MockRestServiceServer testServer = createTestServer(restTemplate);
        restTemplate.getForObject("/test", Object.class);
        testServer.verify();
    }

    @Test
    void tokenInConfigRequestTest(){
        mockSecurityContext();
        String token = (String) springSecurityUserContext.get("token");
        assert token.equals("test_token");
        SecurityContextHolder.setContext(new SecurityContextImpl());
        token = (String) springSecurityUserContext.get("token");
        assert token == null;
    }

    private RestTemplate getN2oRestTemplate() throws Exception {
        Field restTemplateField = springRestDataProviderEngine.getClass().getDeclaredField("restTemplate");
        restTemplateField.setAccessible(true);
        return (RestTemplate) restTemplateField.get(springRestDataProviderEngine);
    }

    private MockRestServiceServer createTestServer(RestTemplate restTemplate) {
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(manyTimes(), requestTo("/test"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer test_token"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        return server;
    }

    private void mockSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        OidcUser oidcUser = new DefaultOidcUser(
                AuthorityUtils.createAuthorityList("SCOPE_message:read"),
                OidcIdToken.withTokenValue("test_token").claim("user_name", "test_user").build(),
                "user_name");

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(oidcUser);
    }
}
