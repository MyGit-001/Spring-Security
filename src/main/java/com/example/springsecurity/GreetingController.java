package com.example.springsecurity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @GetMapping("/hello")
    public String sayHello(){
        return "Hello";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String userEndpoint(){
        return "Hello, Admin";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String adminEndpoint(){
        return "Hello, User";
    }


}
