package com.shiftsl.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "doc_leave")
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LeaveType type;

    private String cause;

    @ManyToOne
    private Shift shift;

    @ManyToOne
    private User doctor;

    @Enumerated(EnumType.STRING)
    private Status status;
}
