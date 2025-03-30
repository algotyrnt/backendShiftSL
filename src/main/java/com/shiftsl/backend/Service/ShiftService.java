package com.shiftsl.backend.Service;

import com.shiftsl.backend.DTO.ShiftDTO;
import com.shiftsl.backend.Exceptions.DoctorCountExceededException;
import com.shiftsl.backend.Exceptions.ShiftClaimFailedException;
import com.shiftsl.backend.Exceptions.ShiftNotFoundException;
import com.shiftsl.backend.Exceptions.ShiftsNotFoundException;
import com.shiftsl.backend.model.Shift;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.model.Ward;
import com.shiftsl.backend.repo.ShiftRepo;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ShiftService {

    private final ShiftRepo shiftRepo;
    private final UserService userService;
    private final WardService wardService;

    // Ward Admin creates a shift and assigns a doctor
    @Transactional
    public Shift createShift(ShiftDTO shiftDTO, Long wardID) {
        Shift shift = new Shift();
        shift.setStartTime(shiftDTO.startTime());
        shift.setEndTime(shiftDTO.endTime());
        shift.setTotalDoctors(shiftDTO.totalDoctors());

        Ward ward = wardService.getWardByID(wardID);
        shift.setWard(ward);

        // Fetch doctors using Streams
        Set<User> doctors = shiftDTO.doctorIds().stream()
                .map(userService::getUserById)
                .collect(Collectors.toSet());

        shift.setNoOfDoctors(doctors.size());

        //Ensure the doctor count does not exceed the allowed limit
        if (shift.getNoOfDoctors() > shift.getTotalDoctors()) {
            throw new DoctorCountExceededException("Number of assigned doctors exceeds the allowed limit.");
        }

        shift.setDoctors(doctors);

        return shiftRepo.save(shift);
    }

    // Get all available shifts in the shift pool
    public List<Shift> getAvailableShifts() {
        return shiftRepo.findAvailableShifts();
    }

    // Doctor claims a shift from the shift pool
    @Transactional
    public void claimShift(Long doctorId, Long shiftId) {
        try {
            Shift shift = getShiftWithLock(shiftId);
            User user = userService.getUserById(doctorId);

            int sizeD = shift.getNoOfDoctors();

            if (sizeD >= shift.getTotalDoctors()) {
                throw new DoctorCountExceededException("Number of assigned doctors exceeds the allowed limit.");
            }

            Set<User> doctors = shift.getDoctors();
            doctors.add(user);
            shift.setDoctors(doctors);
            shift.setNoOfDoctors(++sizeD);

            shiftRepo.save(shift);
        } catch (LockTimeoutException | PessimisticLockException e) {
            throw new ShiftClaimFailedException("System is experiencing high load. Please try again later."+ e);
        }
    }

    public Shift getShiftByID(Long shiftID){
        return shiftRepo.findById(shiftID).orElseThrow(() -> new ShiftNotFoundException("Shift ID - (" + shiftID + ") not found."));
    }

    public Shift getShiftWithLock(Long shiftID){
        return shiftRepo.findShiftWithLock(shiftID).orElseThrow(() -> new ShiftNotFoundException("Shift ID - (" + shiftID + ") not found."));
    }

    public List<Shift> getShiftsForDoctor(Long doctorId) {
        userService.getUserById(doctorId); //check whether the doctor exists or else throws UserNotFoundException
        List<Shift> shifts = shiftRepo.findByDoctorId(doctorId);
        if (shifts.isEmpty()) {
            throw new ShiftsNotFoundException("No shifts found for doctor with ID " + doctorId);
        }
        return shifts;
    }

    public List<Shift> getAllShifts() {
        return shiftRepo.findAll();
    }

    public void deleteShiftByID(Long shiftId) {
        Shift shift = getShiftByID(shiftId);
        shiftRepo.delete(shift);
    }

    public Shift updateShiftByID(Shift shift) {
        getShiftByID(shift.getId());
        return shiftRepo.save(shift);
    }

    public List<Shift> getRoster(int month) {
        // Ensure the month is valid (1-12)
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid month. Please provide a value between 1 and 12.");
        }

        // Determine the year (assume current year by default)
        LocalDate today = LocalDate.now();
        int year = today.getYear();

        // If the selected month is ahead of the current month, assume it's from the previous year
        if (month > today.getMonthValue()) {
            year -= 1;
        }

        // Calculate start and end dates
        LocalDate startDate = LocalDate.of(year, month, 21).minusMonths(1);
        LocalDate endDate = LocalDate.of(year, month, 21);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atStartOfDay();

        return shiftRepo.findShiftsWithinPeriod(startDateTime, endDateTime);
    }
}
