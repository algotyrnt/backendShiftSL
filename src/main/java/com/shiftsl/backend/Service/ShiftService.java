package com.shiftsl.backend.Service;

import com.shiftsl.backend.DTO.ShiftDTO;
import com.shiftsl.backend.Exceptions.DoctorCountExceededException;
import com.shiftsl.backend.Exceptions.ShiftClaimFailedException;
import com.shiftsl.backend.Exceptions.ShiftNotFoundException;
import com.shiftsl.backend.Exceptions.UserNotFoundException;
import com.shiftsl.backend.model.Shift;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.model.Ward;
import com.shiftsl.backend.repo.ShiftRepo;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Service;

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
    public Shift createShift(ShiftDTO shiftDTO) {
        Shift shift = new Shift();
        shift.setStartTime(shiftDTO.startTime());
        shift.setEndTime(shiftDTO.endTime());
        shift.setNoOfDoctors(shiftDTO.noOfDoctors());

        Ward ward = wardService.getWardByID(shiftDTO.wardId());
        shift.setWard(ward);

        // Fetch doctors using Streams
        Set<User> doctors = shiftDTO.doctorIds().stream()
                .map(userService::getUserById)
                .collect(Collectors.toSet());

        //Ensure the doctor count does not exceed the allowed limit
        if (doctors.size() > shiftDTO.noOfDoctors()) {
            throw new DoctorCountExceededException("Number of assigned doctors exceeds the allowed limit.");
        }

        shift.setDoctors(doctors);
        shift.setShiftAvailable(doctors.size() < shift.getNoOfDoctors());

        return shiftRepo.save(shift);
    }

    // Get all available shifts in the shift pool
    public List<Shift> getAvailableShifts() {
        return shiftRepo.findByShiftAvailable(true);
    }

    // Doctor claims a shift from the shift pool
    @Transactional
    public void claimShift(Long doctorId, Long shiftId) {
        try{
            Shift shift = getShiftWithLock(shiftId);
            User user = userService.getUserById(doctorId);

            if(!shift.isShiftAvailable()){throw new DoctorCountExceededException("Number of assigned doctors exceeds the allowed limit.");}

            Set<User> doctors = shift.getDoctors();
            doctors.add(user);

            shift.setShiftAvailable(doctors.size() < shift.getNoOfDoctors());

            shiftRepo.save(shift);
        } catch (UserNotFoundException e) {
            throw new ShiftClaimFailedException("User not found to allocate the shift"+ e);
        } catch (ShiftNotFoundException e){
            throw new ShiftClaimFailedException("Shift not found to allocate the shift to the user"+ e);
        } catch (LockTimeoutException e) {
            throw new ShiftClaimFailedException("Shift is currently being updated by another user."+ e);
        } catch (PessimisticLockException e) {
            throw new ShiftClaimFailedException("System is experiencing high load."+ e);
        }
    }

    public Shift getShiftByID(Long shiftID){
        return shiftRepo.findById(shiftID).orElseThrow(() -> new ShiftNotFoundException("Shift ID - (" + shiftID + ") not found."));
    }

    public Shift getShiftWithLock(Long shiftID){
        return shiftRepo.findShiftWithLock(shiftID).orElseThrow(() -> new ShiftNotFoundException("Shift ID - (" + shiftID + ") not found."));
    }
}
