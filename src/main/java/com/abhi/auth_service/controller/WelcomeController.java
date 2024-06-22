package com.abhi.auth_service.controller;

import com.abhi.auth_service.dto.UserDTO;
import com.abhi.auth_service.producer.UserDetailsProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class WelcomeController {
    private final UserDetailsProducer userDetailsProducer;

    public WelcomeController(UserDetailsProducer userDetailsProducer) {
        this.userDetailsProducer = userDetailsProducer;
    }

    @GetMapping("/")
    public ResponseEntity<String> welcome(){
        return ResponseEntity.ok("Welcome to Authentication Service of Spend Tracker APP.");
    }

    @PostMapping("/publish")
    public ResponseEntity<String> publish(@RequestBody UserDTO message){
        System.out.println("In Publish Controller\n");
        userDetailsProducer.sendMessage(message);
        return ResponseEntity.ok("Sent message to rabbitMQ exchange");
    }
}
