package com.shiftsl.backend.repo;

import com.shiftsl.backend.model.ShiftSwap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftSwapRepo extends JpaRepository<ShiftSwap, Long> {

    List<ShiftSwap> findBySenderId(Long doctorId);

    List<ShiftSwap> findByReceiverId(Long doctorId);

}
