package com.shiftsl.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalDoctors;
    private int noOfDoctors;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    private Ward ward;

    @ManyToMany
    @JoinTable(
            name = "shift_doctors",
            joinColumns = @JoinColumn(name = "shift", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user", referencedColumnName = "id")
    )
    private Set<User> doctors;
}
