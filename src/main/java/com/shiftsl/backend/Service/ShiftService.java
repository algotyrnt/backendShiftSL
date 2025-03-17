package com.shiftsl.backend.Service;

import com.shiftsl.backend.DTO.ShiftDTO;
import com.shiftsl.backend.Exceptions.DoctorCountExceededException;
import com.shiftsl.backend.Exceptions.ShiftNotFoundException;
import com.shiftsl.backend.model.Shift;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.repo.ShiftRepo;
import com.shiftsl.backend.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ShiftService {

    private final ShiftRepo shiftRepo;
    private final UserService userService;

    // Ward Admin creates a shift and assigns a doctor
    @Transactional
    public Shift createShift(ShiftDTO shiftDTO) {
        Shift shift = new Shift();
        shift.setNoOfDoctors(shiftDTO.noOfDoctors());
        shift.setStartTime(shiftDTO.startTime());
        shift.setEndTime(shiftDTO.endTime());

        // Fetch doctors using Streams
        Set<User> doctors = shiftDTO.doctorIds().stream()
                .map(userService::getUserById)
                .collect(Collectors.toSet());

        // Ensure the doctor count does not exceed the allowed limit
        if (doctors.size() > shiftDTO.noOfDoctors()) {
            throw new DoctorCountExceededException("Number of assigned doctors exceeds the allowed limit.");
        }

        shift.setDoctors(doctors);
        shift.setShiftAvailable(doctors.size() < shiftDTO.noOfDoctors());

        return shiftRepo.save(shift);
    }

    // Get all available shifts in the shift pool
    public List<Shift> getAvailableShifts() {
        return shiftRepo.findByShiftAvailable(true);
    }

    // Doctor claims a shift from the shift pool
    @Transactional
    public void claimShift(Long doctorId, Long shiftId) {
        //to do
    }

    public Shift getShiftByID(Long shiftID){
        return shiftRepo.findById(shiftID).orElseThrow(() -> new ShiftNotFoundException(shiftID));
    }
}
