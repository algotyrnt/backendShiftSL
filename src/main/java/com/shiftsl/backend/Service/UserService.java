package com.shiftsl.backend.Service;

import com.shiftsl.backend.DTO.UserDTO;
import com.shiftsl.backend.Exceptions.AccountNotCreatedException;
import com.shiftsl.backend.Exceptions.PhoneAlreadyInUseException;
import com.shiftsl.backend.Exceptions.UserNotFoundException;
import com.shiftsl.backend.Exceptions.UserNotUpdatedException;
import com.shiftsl.backend.model.Role;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepo userRepo;
    private final FirebaseAuthService firebaseAuthService;

    @Transactional
    public User registerUser(UserDTO userDTO) {
        try {
            logger.info("Checking if user is already registered with given phone number");
            userRepo.findByPhoneNo(userDTO.phoneNo()).ifPresent(user -> {
                throw new PhoneAlreadyInUseException("Phone Number already in use.");
            });

            logger.info("Creating User object for User {} {}, and number {}", userDTO.firstName(), userDTO.lastName(), userDTO.phoneNo());
            User user = new User();
            user.setFirstName(userDTO.firstName());
            user.setLastName(userDTO.lastName());
            user.setSlmcReg(userDTO.slmcReg());
            user.setFirebaseUid(firebaseAuthService.createUser(userDTO.email()));
            user.setEmail(userDTO.email());
            user.setPhoneNo(userDTO.phoneNo());
            user.setRole(userDTO.role());

            logger.info("Registering User with phone number: {}", user.getPhoneNo());
            return userRepo.save(user);
        } catch (Exception e) {
            logger.error("Unable to save user to database", e);
            throw new AccountNotCreatedException("Unable to register the current user");
        }
    }

    @Transactional
    public User getUserById(Long userId) {
        return userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User - (" + userId + ") not found."));
    }

    @Transactional
    public List<User> getUsersByRole(Role role) {
        return userRepo.findByRoleIn(List.of(role));
    }

    @Transactional
    public User updateUserById(Long userId, User user) {
        try {
            getUserById(userId); //check whether the user exists or else throws UserNotFoundException
            return userRepo.save(user);
        } catch (UserNotFoundException e) {
            throw new UserNotUpdatedException("User not found to update details"+ e);
        }
    }

    @Transactional
    public String deleteUserById(Long userId) {
        userRepo.deleteById(userId);
        return "User deleted successfully.";
    }

    @Transactional
    public User findUserByFirebaseUid(String uid) {
        return userRepo.findByFirebaseUid(uid).orElseThrow(() -> {
            logger.error("Unable to find user by Firebase UID - " + uid);
            return new UserNotFoundException("Unable to find user by Firebase UID - " + uid);
        });
    }

    @Transactional
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Transactional
    public List<User> getDoctors() {
        return userRepo.findByRoleIn(List.of(Role.DOCTOR_PERM, Role.DOCTOR_TEMP));
    }
}
