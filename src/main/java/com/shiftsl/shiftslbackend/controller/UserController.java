package com.shiftsl.shiftslbackend.controller;

import com.shiftsl.shiftslbackend.model.User;
import com.shiftsl.shiftslbackend.service.FirebaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private FirebaseUserService firebaseUserService;

    @PostMapping("/create/hr-admin")
    public HttpStatus createHrAdmin(@RequestBody CreateUserRequest createUserRequest) {
        try {
            // Create an HR Admin user
            firebaseUserService.createUser(createUserRequest.getEmail(), createUserRequest.getPassword(), User.Role.HR_ADMIN);
            return HttpStatus.CREATED;  // HR Admin created successfully
        } catch (Exception e) {
            System.err.println("Error creating HR Admin: " + e.getMessage());
            return HttpStatus.INTERNAL_SERVER_ERROR;  // If an error occurs while creating the user
        }
    }
}
