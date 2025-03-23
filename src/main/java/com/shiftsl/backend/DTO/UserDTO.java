package com.shiftsl.backend.DTO;

import com.shiftsl.backend.model.Role;

public record UserDTO(
        String firstName,
        String lastName,
        String email,
        String phoneNo,
        Role role
) {
}
