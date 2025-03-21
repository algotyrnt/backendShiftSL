package com.shiftsl.backend.DTO;

import com.shiftsl.backend.model.LeaveType;

public record LeaveDTO(
        LeaveType type,
        String cause,
        Long shiftID,
        Long doctorID
) {
}
