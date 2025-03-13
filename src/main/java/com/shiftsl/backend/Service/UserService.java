package com.shiftsl.backend.Service;

import com.shiftsl.backend.Exceptions.UserNotFoundException;
import com.shiftsl.backend.model.Role;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.repo.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    public User registerUser(User user) {
        if (userRepo.findByPhoneNo(user.getPhoneNo()).isPresent()) {
            throw new RuntimeException("Phone Number already in use.");
        }
        return userRepo.save(user);
    }

    public User getUserById(Long userId) {
        return userRepo.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepo.findAll().stream().filter(user -> user.getRole().equals(role)).toList();
    }

    public User updateUserById(Long userId, User user) {
        userRepo.findById(userId).orElseThrow(UserNotFoundException::new);
        return userRepo.save(user);
    }

    public String deleteUserById(Long userId) {
        userRepo.deleteById(userId);
        return "User deleted successfully.";
    }
}
