package com.shiftsl.backend.model;

import jakarta.persistence.*;
import jdk.jfr.BooleanFlag;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "ward_id")
    private Ward ward;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @BooleanFlag
    private boolean shiftAvailable; // Shift available for other doctors to claim
}
