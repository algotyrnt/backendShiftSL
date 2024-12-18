package com.shiftsl.shiftslbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;
    private String name;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;  // Enum for user roles

    public enum Role {
        HR_ADMIN,
        WARD_ADMIN,
        MED_STAFF
    }
}
