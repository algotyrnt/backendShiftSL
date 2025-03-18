package com.shiftsl.backend.Service;

import com.shiftsl.backend.DTO.UserDTO;
import com.shiftsl.backend.Exceptions.PhoneNotInUseException;
import com.shiftsl.backend.Exceptions.UserNotFoundException;
import com.shiftsl.backend.Exceptions.UserNotUpdatedException;
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
    public User registerUser(UserDTO userDTO) {
        userRepo.findByPhoneNo(userDTO.phoneNo()).ifPresent(user -> {
            throw new PhoneNotInUseException("Phone Number already in use.");
        });

        User user = new User();
        user.setFirstName(userDTO.firstName());
        user.setLastName(userDTO.lastName());
        user.setPhoneNo(userDTO.phoneNo());
        user.setRole(userDTO.role());

        return userRepo.save(user);
    }

    public User getUserById(Long userId) {
        return userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User - (" + userId + ") not found."));
    }

    public List<User> getUsersByRole(Role role) {
        return userRepo.findAll().stream().filter(user -> user.getRole().equals(role)).toList();
    }

    public User updateUserById(Long userId, User user) {
        try {
            getUserById(userId); //check whether the user exists or else throws UserNotFoundException
            return userRepo.save(user);
        } catch (UserNotFoundException e) {
            throw new UserNotUpdatedException("User - (" + userId + ") not found to update details");
        }
    }

    public String deleteUserById(Long userId) {
        userRepo.deleteById(userId);
        return "User deleted successfully.";
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
}
