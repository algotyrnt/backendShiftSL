package com.shiftsl.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "doc_leave")
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Enumerated(EnumType.STRING)
    private LeaveType type;

    private String cause;

    @OneToOne
    private Shift shift;

    @OneToOne
    private User doctor;

    @Enumerated(EnumType.STRING)
    private Status status;
}
