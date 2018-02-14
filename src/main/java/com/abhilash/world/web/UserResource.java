package com.abhilash.world.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserResource {

    @Autowired
    KeycloakRestTemplate keycloakRestTemplate;

    @GetMapping(path = "/protected1")
    public ResponseEntity<?> firstProtectedResource(){
        return ResponseEntity.ok("I'm glad you've made all the way...");
    }

    @GetMapping(path = "/protected2")
    public ResponseEntity<?> secondProtectedResource(){
        return ResponseEntity.ok("Not Bad! Looks like SSO is working fine...");
    }

    @GetMapping(path = "/unprotected")
    public ResponseEntity<?> unprotected(){
        return ResponseEntity.ok("This is not the end of the world...");
    }

    @GetMapping(path = "/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return ResponseEntity.ok("Logged out successfully...");
    }
}
