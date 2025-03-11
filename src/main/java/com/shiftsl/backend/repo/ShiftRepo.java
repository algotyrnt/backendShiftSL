package com.shiftsl.backend.repo;

import com.shiftsl.backend.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftRepo extends JpaRepository<Shift, Long> {
    List<Shift> findByShiftAvailable(boolean shiftAvailable);
}
