package com.shiftsl.backend.model;

import jakarta.persistence.*;
import jdk.jfr.BooleanFlag;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int noOfDoctors;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToMany
    @JoinTable(
            name = "shift_doctors",
            joinColumns = @JoinColumn(name = "shift", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user", referencedColumnName = "id")
    )
    private Set<User> doctors;

    @BooleanFlag
    private boolean shiftAvailable; // Shift available for other doctors to claim
}
