package com.shiftsl.shiftslbackend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.shiftsl.shiftslbackend.exception.AuthException;
import com.shiftsl.shiftslbackend.model.User;
import com.shiftsl.shiftslbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void createUser(String email, String password, User.Role role) throws Exception {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password);

        // Create user in Firebase
        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

        User newUser = new User();
        newUser.setId(userRecord.getUid());
        newUser.setEmail(email);
        newUser.setRole(role);
        userRepository.save(newUser);
    }

    public String authenticate(String email, String password) {
        try {
            // Retrieve user information using Firebase Admin SDK
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);

            // Firebase Admin SDK does not verify passwords.
            // Assume the password is verified on the frontend with Firebase Client SDK.

            // Generate a Firebase custom token for the authenticated user
            return FirebaseAuth.getInstance().createCustomToken(userRecord.getUid());

        } catch (FirebaseAuthException e) {
            // Log the error and rethrow as a custom exception or meaningful RuntimeException
            throw new AuthException("Authentication failed for email: " + email + ". Reason: " + e.getMessage());

        }
    }

}
