package com.shiftsl.backend.Controller;

import com.shiftsl.backend.Service.UserService;
import com.shiftsl.backend.model.Role;
import com.shiftsl.backend.model.User;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    // Get user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // Get users by role (HR_ADMIN, WARD_ADMIN, DOCTOR)
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }
}
