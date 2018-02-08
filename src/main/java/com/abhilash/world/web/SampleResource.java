package com.abhilash.world.web;

import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sample")
public class SampleResource {

    @GetMapping(path = "/home")
    public ResponseEntity<?> index(){
        return ResponseEntity.ok("<B>Welcome</B>");
    }

    @GetMapping(path = "/products")
    public ResponseEntity<?> getProducts(){
        return ResponseEntity.ok(Arrays.asList("Milch","Cola","Beer"));
    }

    @GetMapping(path = "/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return ResponseEntity.ok("Logged out successfully...");
    }
}
