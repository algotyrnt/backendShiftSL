package com.shiftsl.backend.Service;

import com.shiftsl.backend.Exceptions.ShiftNotFoundException;
import com.shiftsl.backend.model.Shift;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.repo.ShiftRepo;
import com.shiftsl.backend.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ShiftService {

    private final ShiftRepo shiftRepo;
    private final UserRepo userRepo;

    // Ward Admin creates a shift and assigns a doctor
    @Transactional
    public Shift createShift(Long wardAdminId, Shift shift, Long doctorId) {
        User doctor = userRepo.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));


        return shiftRepo.save(shift);
    }

    // Get all available shifts in the shift pool
    public List<Shift> getAvailableShifts() {
        return shiftRepo.findByShiftAvailable(true);
    }

    // Doctor claims a shift from the shift pool
    @Transactional
    public void claimShift(Long doctorId, Long shiftId) {
        Shift shift = shiftRepo.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        User doctor = userRepo.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (!shift.isShiftAvailable()) {
            throw new RuntimeException("Shift is not available in the shift pool");
        }

        shift.setShiftAvailable(false);
        shiftRepo.save(shift);
    }

    public Shift getShiftByID(Long shiftID){
        return shiftRepo.findById(shiftID).orElseThrow(() -> new ShiftNotFoundException(shiftID));
    }
}
