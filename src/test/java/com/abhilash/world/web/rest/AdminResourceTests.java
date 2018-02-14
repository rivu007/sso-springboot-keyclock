package com.abhilash.world.web.rest;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.http.HttpHeaders;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.abhilash.world.SingleSignonApplicationTest;

public class AdminResourceTests extends SingleSignonApplicationTest {

    @Test
    public void testAdminBaseURL_WithoutBearerToken_status401() throws Exception {
        mvc().perform(get("/admin")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("Unauthorized"));
    }

    @Test
    public void testAdminBaseURL_withFalseBearerToken_status401() throws Exception {
        //String accessToken = obtainAccessToken("admin", "nimda");
        mvc().perform(get("/admin")
                .header(HttpHeaders.AUTHORIZATION, "Bearer dummyToken")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("Unable to authenticate using the Authorization header"));
    }

    @Test
    public void testAdminBaseURL_withAnonymousUser_unauthorized() throws Exception {
        String accessToken = obtainAccessToken("test", "test");
        mvc().perform(get("/admin")
                .header(HttpHeaders.AUTHORIZATION, "Bearer dummyToken")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("Unable to authenticate using the Authorization header"));
    }

    @Test
    public void testAdminBaseURL_withValidBearerToken_ok() throws Exception {
        String accessToken = obtainAccessToken("test", "test");
        mvc().perform(get("/admin")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private String obtainAccessToken(String username, String password) throws Exception {

        String authServer = keycloakSpringBootProperties.getAuthServerUrl();
        String realm = keycloakSpringBootProperties.getRealm();

        final HttpEntity<?> entity = createHttpEntity(username, password);

        ResponseEntity<AccessTokenResponse> accessTokenResponse =
                rest.exchange(String.format("%s/realms/%s/protocol/openid-connect/token", authServer, realm),
                        POST,
                        entity,
                        AccessTokenResponse.class);

        return accessTokenResponse.getBody().getToken();

    }

    private HttpEntity<?> createHttpEntity(String username, String password) {

        final org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", keycloakSpringBootProperties.getResource());
        body.add("username", username);
        body.add("password", password);
        body.add("client_secret", keycloakSpringBootProperties.getCredentials().get("secret").toString());

        return new HttpEntity<>(body, requestHeaders);
    }
}
