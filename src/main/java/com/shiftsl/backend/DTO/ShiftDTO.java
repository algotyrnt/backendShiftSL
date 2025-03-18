package com.shiftsl.backend.DTO;

import java.time.LocalDateTime;
import java.util.Set;

public record ShiftDTO(
        int noOfDoctors,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long wardId,
        Set<Long> doctorIds
) {
}
