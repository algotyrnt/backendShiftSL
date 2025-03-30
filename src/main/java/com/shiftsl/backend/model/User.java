package com.shiftsl.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;

    private String slmcReg;

    @Column(unique = true, nullable = false)
    private String firebaseUid;

    @Column(unique = true)
    private String phoneNo;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;
}
