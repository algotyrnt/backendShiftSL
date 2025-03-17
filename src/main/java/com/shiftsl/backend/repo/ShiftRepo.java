package com.shiftsl.backend.repo;

import com.shiftsl.backend.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRepo extends JpaRepository<Shift, Long> {
    List<Shift> findByShiftAvailable(boolean shiftAvailable);
}
