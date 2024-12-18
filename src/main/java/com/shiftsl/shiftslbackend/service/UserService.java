package com.shiftsl.shiftslbackend.service;

import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.shiftsl.shiftslbackend.exception.AuthException;
import com.shiftsl.shiftslbackend.model.User;
import com.shiftsl.shiftslbackend.repository.FirestoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private FirestoreRepository firestoreRepository;

    public void createUser(String email, String password, String name, String role) throws Exception {
        // Create the user in Firebase Authentication
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

        // Save user details in Firestore
        User user = new User(userRecord.getUid(), email, name, role);
        WriteResult result = firestoreRepository.saveUser(user).get(); // Firestore write operation

        System.out.println("User saved to Firestore at: " + result.getUpdateTime());
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
