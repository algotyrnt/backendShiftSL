package com.shiftsl.shiftslbackend.controller;

import com.shiftsl.shiftslbackend.request.CreateUserRequest;
import com.shiftsl.shiftslbackend.request.LoginRequest;
import com.shiftsl.shiftslbackend.model.LoginResponse;
import com.shiftsl.shiftslbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) {
        try {
            userService.createUser(request.getEmail(), request.getPassword(), request.getName(), request.getRole());
            return ResponseEntity.ok("User created successfully and saved to Firestore!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating user: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(new LoginResponse("Login successful", token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new LoginResponse("Login failed: " + e.getMessage(), null));
        }
    }
}
