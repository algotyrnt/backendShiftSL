package com.shiftsl.backend.Service;

import com.shiftsl.backend.DTO.ShiftDTO;
import com.shiftsl.backend.Exceptions.*;
import com.shiftsl.backend.model.Shift;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.model.Ward;
import com.shiftsl.backend.repo.ShiftRepo;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final static Logger logger = LoggerFactory.getLogger(ShiftService.class);

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
    @Transactional
    public List<Shift> getAvailableShifts() {
        try {
            logger.info("Getting available shifts");
            return shiftRepo.findAvailableShifts();
        } catch (Exception e) {
            logger.warn("Unable to retrieve available shifts.");
            throw new ShiftRetrievalException("Error occurred while trying to retrieve available shifts from database");
        }
    }

    // Doctor claims a shift from the shift pool
    @Transactional
    public void claimShift(Long doctorId, Long shiftId) {
        String errorMessage = null;
        try {
            logger.info("Claiming shift " + shiftId);
            Shift shift = getShiftWithLock(shiftId);
            User user = userService.getUserById(doctorId);

            int sizeD = shift.getNoOfDoctors();

            if (sizeD >= shift.getTotalDoctors()) {
                logger.error("Number of doctors is greater than total number of doctors allowed");
                errorMessage = "Number of assigned doctors exceeds the allowed limit.";
                throw new DoctorCountExceededException(errorMessage);
            }

            Set<User> doctors = shift.getDoctors();
            doctors.add(user);
            shift.setDoctors(doctors);
            shift.setNoOfDoctors(++sizeD);

            shiftRepo.save(shift);
        } catch (LockTimeoutException | PessimisticLockException e) {
            logger.error("Too many threads are trying to claim shift.");
            errorMessage = "System is experiencing high load. Please try again later. " + e.getMessage();
            throw new ShiftClaimFailedException(errorMessage);
        } catch (Exception e) {
            logger.error("Error occurred while trying to store shift {} for doctor {} in database", shiftId, doctorId);

            String fullMessage = String.format(
                    "Unable to claim shiftId: %d for doctorId: %d", shiftId, doctorId
            );

            if (errorMessage != null) {
                fullMessage += ". " + errorMessage;
            } else {
                fullMessage += ". " + e.getMessage();
            }

            throw new ShiftClaimFailedException(fullMessage);
        }
    }

    @Transactional
    public Shift getShiftByID(Long shiftID){
        logger.info("Retrieving Shift '{}' from database", shiftID);
        return shiftRepo.findById(shiftID).orElseThrow(() -> {
            logger.warn("Unable to find shift with ID {}", shiftID);
            return new ShiftNotFoundException("Shift ID - (" + shiftID + ") not found.");
        });
    }

    @Transactional
    public Shift getShiftWithLock(Long shiftID){
        logger.info("Retrieving Shift {} with pessimistic lock", shiftID);
        return shiftRepo.findShiftWithLock(shiftID).orElseThrow(() -> {
            logger.warn("Unable to find shift with locking implemented for ID {}", shiftID);
            return new ShiftNotFoundException("Shift ID - (" + shiftID + ") not found.");
        });
    }

    @Transactional
    public List<Shift> getShiftsForDoctor(Long doctorId) {
        logger.info("Retrieving shifts for doctor {}", doctorId);
        userService.getUserById(doctorId); //check whether the doctor exists or else throws UserNotFoundException
        List<Shift> shifts = shiftRepo.findByDoctors_Id(doctorId);
        if (shifts.isEmpty()) {
            logger.warn("No shift found for doctor {}", doctorId);
            throw new ShiftsNotFoundException("No shifts found for doctor with ID " + doctorId);
        }
        return shifts;
    }

    @Transactional
    public List<Shift> getAllShifts() {
        try {
            logger.info("Retrieving all shifts from database");
            return shiftRepo.findAll();
        } catch (Exception e) {
            logger.error("Unable to get all shifts.");
            throw new ShiftRetrievalException("Error occurred while trying to retrieve all shifts from database");
        }
    }

    @Transactional
    public void deleteShiftByID(Long shiftId) {
        try {
            logger.info("Deleting shift {} from database", shiftId);
            Shift shift = getShiftByID(shiftId);
            shiftRepo.delete(shift);
        } catch (Exception e) {
            logger.error("Error occurred while trying to delete shift {} from database", shiftId);
            throw new ShiftRetrievalException("Shift ID - (" + shiftId + ") not found.");
        }
    }

    @Transactional
    public Shift updateShiftByID(Shift shift) {
        getShiftByID(shift.getId());
        return shiftRepo.save(shift);
    }

    @Transactional
    public List<Shift> getRoster(int month) {
        // Ensure the month is valid (1-12)
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid month. Please provide a value between 1 and 12.");
        }

        try {
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


            return shiftRepo.findByStartTimeBetween(startDateTime, endDateTime);
        } catch (Exception e) {
            logger.error("Error occurred while trying to retrieve roster for month {}", month);
            throw  new ShiftsNotFoundException("Unable to retrieve shifts for the given month from database");
        }

    }
}
