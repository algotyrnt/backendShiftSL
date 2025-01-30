package com.shiftsl.backend.Service;

import com.shiftsl.backend.model.Role;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.repo.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    public User registerUser(User user) {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use.");
        }
        // Encrypt password must be implemented
        return userRepo.save(user);
    }

    public Optional<User> getUserById(Long userId) {
        return userRepo.findById(userId);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepo.findAll().stream().filter(user -> user.getRole().equals(role)).toList();
    }
}
