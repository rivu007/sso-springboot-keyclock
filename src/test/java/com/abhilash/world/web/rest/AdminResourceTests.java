package com.abhilash.world.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.http.HttpHeaders;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import com.abhilash.world.SingleSignonApplicationTest;

public class AdminResourceTests extends SingleSignonApplicationTest {

    @Test
    public void testAdminBaseURL_WithoutBearerToken_unauthorized() throws Exception {
        mvc().perform(get("/admin/")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("Unauthorized"));
    }

    @Test
    public void testAdminBaseURL_withDummyBearerToken_unauthorized() throws Exception {
        mvc().perform(get("/admin/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer dummyToken")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("Unable to authenticate using the Authorization header"));
    }

    @Test
    @WithAnonymousUser
    public void testAdminBaseURL_withAnonymousUser_unauthorized() throws Exception {
        mvc().perform(get("/admin/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer dummyToken")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("Unable to authenticate using the Authorization header"));
    }

    @Test
    public void testAdminBaseURL_validTokenButUserWithDifferentRole_forbidden() throws Exception {
        String accessToken = obtainAccessToken("test", "test");
        mvc().perform(get("/admin/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(status().reason("Access is denied"));
    }

    @Test
    public void testAdminBaseURL_validTokenAndRole_ok() throws Exception {
        String accessToken = obtainAccessToken("admin", "admin");
        mvc().perform(get("/admin/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
