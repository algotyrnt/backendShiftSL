package com.shiftsl.shiftslbackend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FirebaseAuthService {

    public Optional<FirebaseToken> verifyIdToken(String idToken) {
        try {
            // Verify Firebase ID Token
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            return Optional.of(decodedToken);
        } catch (Exception e) {
            // Log the error and return empty if token is invalid
            return Optional.empty();
        }
    }
}
