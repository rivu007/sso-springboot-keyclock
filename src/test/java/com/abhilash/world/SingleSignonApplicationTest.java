package com.abhilash.world;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class SingleSignonApplicationTest {

    @Autowired
    protected KeycloakSpringBootProperties keycloakSpringBootProperties;

    @Autowired
    protected TestRestTemplate rest;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private Keycloak keycloak;

    private UserRepresentation user;

    @PostConstruct
    public void init() {
        this.keycloak = getKeycloakClient();
    }

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        setupKeycloakUser(keycloak, "test", "test", "user");
        setupKeycloakUser(keycloak, "admin", "admin", "admin");
    }

    @After
    public void tearDown() {
        //TODO: Find a way to delete the test user after the test
        if(!StringUtils.isEmpty(user.getId())) {
            //keycloak.realm(keycloakSpringBootProperties.getRealm()).users().get(user.getId()).remove();
        }
    }

    /**
     * Get the realm specific to the test environment. @See application.yml for more details
     *
     * @return realm resource
     */
    protected RealmResource getRealm() {
        return keycloak.realm(keycloakSpringBootProperties.getRealm());
    }

    protected MockMvc mvc() {
        return mockMvc;
    }

    /**
     * Builder pattern to get the instance of keycloak instance.
     *
     * @return Keycloak client instance
     */
    private Keycloak getKeycloakClient() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakSpringBootProperties.getAuthServerUrl())
                .realm("master")
                .clientId("admin-cli")
                .username("admin")
                .password("password")
                .build();
    }

    /**
     * Setup a dummy user in keycloak for running the test
     */
    private void setupKeycloakUser(Keycloak keycloak,
                                   String username,
                                   String password,
                                   String role) {

        // Get the Realm specific to test environment
        RealmResource realmResource = getRealm();

        // Get the Realm specific role
        RoleRepresentation realmRole = realmResource.roles().get(role).toRepresentation();

        // Set the credentials for the user
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);

        user = new UserRepresentation();
        user.setUsername(username);
        user.setFirstName(username);
        user.setLastName("User");
        user.setEnabled(true);
        user.setCredentials(Arrays.asList(credential));
        Response response = realmResource.users().create(user);

        if (response.getStatusInfo().getStatusCode() == 201) {
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            user.setId(userId);

            //update user role
            UsersResource userResource = realmResource.users();
            userResource.get(userId).roles().realmLevel().add(Arrays.asList(realmRole));
        }
    }

}
