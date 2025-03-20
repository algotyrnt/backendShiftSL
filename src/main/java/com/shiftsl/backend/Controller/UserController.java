package com.shiftsl.backend.Controller;

import com.shiftsl.backend.DTO.UserDTO;
import com.shiftsl.backend.Service.UserService;
import com.shiftsl.backend.model.Role;
import com.shiftsl.backend.model.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserDTO user) {
        return new ResponseEntity<>(userService.registerUser(user), HttpStatus.OK);
    }

    // Get user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    //TODO: GET mapping to get all users
    @GetMapping("/get-all")
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Update user by ID
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUserById(@PathVariable Long userId, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUserById(userId, user));
    }

    // Delete user by ID
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.deleteUserById(userId));
    }

    // Get users by role (HR_ADMIN, WARD_ADMIN, DOCTOR)
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }
}
