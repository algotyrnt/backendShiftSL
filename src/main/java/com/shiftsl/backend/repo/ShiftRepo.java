package com.shiftsl.backend.repo;

import com.shiftsl.backend.model.Shift;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepo extends JpaRepository<Shift, Long> {
    List<Shift> findByShiftAvailable(boolean shiftAvailable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Shift s WHERE s.id = :shiftID")
    Optional<Shift> findShiftWithLock(@Param("shiftID") Long shiftID);
}
