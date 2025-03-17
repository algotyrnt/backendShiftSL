package com.shiftsl.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @OneToOne
    private Shift shift;

    @OneToOne
    private User doctor;

    @Enumerated(EnumType.STRING)
    private Status status;
}
