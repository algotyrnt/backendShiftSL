package com.shiftsl.backend.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.shiftsl.backend.Exceptions.AccountNotCreatedException;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {

    public String createUser(String email) {
        try {
            CreateRequest request = new CreateRequest()
                    .setEmail(email)
                    .setPassword("DefaultPassword123"); // Set a default password

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            return userRecord.getUid();
        } catch (Exception e) {
            throw new AccountNotCreatedException("Error creating user: " + e.getMessage());
        }
    }
}

