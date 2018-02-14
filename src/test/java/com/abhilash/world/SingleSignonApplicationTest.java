package com.abhilash.world;

import static org.springframework.http.HttpMethod.POST;

import java.util.Arrays;

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
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

/**
 * This is the base class which setups up the basic structure to run the test.
 *
 * Every test should extend this class.
 */
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
        this.keycloak = initKeycloakClient();
    }

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        setupKeycloakUser("test", "test", "user");
        setupKeycloakUser("admin", "admin", "admin");
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

    /**
     * Returns the @{link MockMvc} instance which can be used by the other classes extending this class.
     *
     * @return instance of MockMvc
     */
    protected MockMvc mvc() {
        return mockMvc;
    }

    /**
     * Retrieves the access token from Keycloak.
     *
     * @param username of the user to issue the access token
     * @param password of the user to issue the access token
     * @return access token
     */
    protected String obtainAccessToken(String username, String password) throws Exception {

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

    /**
     * Initialise the keycloak admin Client.
     *
     * @return Keycloak client instance
     */
    private Keycloak initKeycloakClient() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakSpringBootProperties.getAuthServerUrl())
                .realm("master")
                .clientId("admin-cli")
                .username("admin")
                .password("password")
                .build();
    }

    /**
     * Create user in keycloak for running the test
     *
     * @param username of the user to be created
     * @param password of the user to be created
     * @param role of the user to be created
     */
    private void setupKeycloakUser(String username,
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

        // Set the user specific details
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
