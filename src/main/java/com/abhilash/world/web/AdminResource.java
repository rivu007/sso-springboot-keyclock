package com.abhilash.world.web;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminResource {

    @GetMapping(path = "/")
    public ResponseEntity<?> index(Principal principal){
        return ResponseEntity.ok("Admin id: " + principal.getName());
    }

    @GetMapping(path = "/protected1")
    public ResponseEntity<?> firstProtectedResource(){
        return ResponseEntity.ok("Hey Admin, wassup...");
    }
}
