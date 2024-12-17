package com.shiftsl.shiftslbackend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.shiftsl.shiftslbackend.model.User;
import com.shiftsl.shiftslbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FirebaseUserService {

    @Autowired
    private UserRepository userRepository;

    // Create a user in Firebase Authentication and assign the HR_ADMIN role
    public UserRecord createUser(String email, String password, User.Role role) throws Exception {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password);

        // Create user in Firebase
        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

        // After creating the user in Firebase, create the user in the database with role
        User newUser = new User();
        newUser.setId(userRecord.getUid());
        newUser.setEmail(email);
        newUser.setRole(role);  // Assign the HR_ADMIN role
        userRepository.save(newUser);

        return userRecord;
    }
}
