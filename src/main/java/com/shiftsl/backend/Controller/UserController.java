package com.shiftsl.backend.Controller;

import com.shiftsl.backend.DTO.UserDTO;
import com.shiftsl.backend.Service.UserService;
import com.shiftsl.backend.model.Role;
import com.shiftsl.backend.model.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    // Register a new user
    @PostMapping()
    @PreAuthorize("hasAnyAuthority('HR_ADMIN')")
    public ResponseEntity<User> registerUser(@RequestBody UserDTO user) {
        return new ResponseEntity<>(userService.registerUser(user), HttpStatus.OK);
    }

    // Get user by ID
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('HR_ADMIN', 'WARD_ADMIN', 'DOCTOR_PERM', 'DOCTOR_TEMP')")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/firebase/{userId}")
    @PreAuthorize("hasAnyAuthority('HR_ADMIN', 'WARD_ADMIN', 'DOCTOR_PERM', 'DOCTOR_TEMP')")
    public ResponseEntity<User> getUserByFirebaseId(@PathVariable String userId) {
        return ResponseEntity.ok(userService.findUserByFirebaseUid(userId));
    }

    // Get all registered users
    @GetMapping("/get-all")
    @PreAuthorize("hasAnyAuthority('HR_ADMIN', 'WARD_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Get Doctors
    @GetMapping("/role/doc")
    @PreAuthorize("hasAnyAuthority('HR_ADMIN', 'WARD_ADMIN', 'DOCTOR_PERM', 'DOCTOR_TEMP')")
    public ResponseEntity<List<User>> getDoctors() {
        return ResponseEntity.ok(userService.getDoctors());
    }

    // Update user by ID
    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('HR_ADMIN')")
    public ResponseEntity<User> updateUserById(@PathVariable Long userId, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUserById(userId, user));
    }

    // Delete user by ID
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('HR_ADMIN')")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.deleteUserById(userId));
    }

    // Get users by role (HR_ADMIN, WARD_ADMIN, DOCTOR)
    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyAuthority('HR_ADMIN', 'WARD_ADMIN')")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }
}
