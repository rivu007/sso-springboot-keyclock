package com.abhilash.world.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.http.HttpHeaders;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;

import com.abhilash.world.SingleSignonApplicationTest;

public class UserResourceTests extends SingleSignonApplicationTest {

    @Test
    public void testUnprotectedURL_WithoutBearerToken_ok() throws Exception {
        mvc().perform(get("/user/public")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string("This is not the end of the world..."));
    }

    @Test
    public void testProtected1URL_withDummyBearerToken_unauthorized() throws Exception {
        mvc().perform(get("/user/protected1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer dummyToken")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("Unable to authenticate using the Authorization header"));
    }

    @Test
    public void testProtected1URL_validTokenAndRole_ok() throws Exception {
        String accessToken = obtainAccessToken("test", "test");
        mvc().perform(get("/user/protected1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("I'm glad you've made all the way..."));
    }

    @Test
    public void testProtected2URL_validTokenAndRole_ok() throws Exception {
        String accessToken = obtainAccessToken("test", "test");
        mvc().perform(get("/user/protected2")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Not Bad! Looks like SSO is working fine..."));
    }

    @Test
    public void testSingleSignOn_callTwoEndpointWithValidToken_ok() throws Exception {
        //Get the token
        String accessToken = obtainAccessToken("test", "test");

        //Call the first endpoint
        mvc().perform(get("/user/protected1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("I'm glad you've made all the way..."));

        //Call the second endpoint
        mvc().perform(get("/user/protected2")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Not Bad! Looks like SSO is working fine..."));
    }
}
