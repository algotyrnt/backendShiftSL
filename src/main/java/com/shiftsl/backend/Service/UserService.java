package com.shiftsl.backend.Service;

import com.shiftsl.backend.Exceptions.UserNotFoundException;
import com.shiftsl.backend.model.Role;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    @Transactional
    public User registerUser(User user) {
        if (userRepo.findByPhoneNo(user.getPhoneNo()).isPresent()) {
            throw new RuntimeException("Phone Number already in use.");
        }
        return userRepo.save(user);
    }

    public User getUserById(Long userId) {
        return userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    public List<User> getUsersByRole(Role role) {
        return userRepo.findAll().stream().filter(user -> user.getRole().equals(role)).toList();
    }

    public User updateUserById(Long userId, User user) {
        userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return userRepo.save(user);
    }

    public String deleteUserById(Long userId) {
        userRepo.deleteById(userId);
        return "User deleted successfully.";
    }
}
