package com.shiftsl.backend.Service;

import com.shiftsl.backend.DTO.LeaveDTO;
import com.shiftsl.backend.Exceptions.LeaveNotFoundException;
import com.shiftsl.backend.Exceptions.LeaveNotSavedException;
import com.shiftsl.backend.Exceptions.LeaveRetrievalException;
import com.shiftsl.backend.model.Leave;
import com.shiftsl.backend.model.Shift;
import com.shiftsl.backend.model.Status;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.repo.LeaveRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class LeaveService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveService.class);

    private final LeaveRepo leaveRepo;
    private final ShiftService shiftService;
    private final UserService userService;

    @Transactional
    public Leave requestLeave(LeaveDTO leaveDTO) {
        try {
            User doctor = userService.getUserById(leaveDTO.doctorID());
            Shift shift = shiftService.getShiftByID(leaveDTO.shiftID());

            Leave leave = new Leave();
            leave.setShift(shift);
            leave.setDoctor(doctor);
            leave.setStatus(Status.PENDING);
            leave.setType(leaveDTO.type());
            leave.setCause(leaveDTO.cause());

            return leaveRepo.save(leave);
        } catch(Exception e) {
            logger.error("Error while making leave request for doctorID={}, shiftID={}", leaveDTO.doctorID(), leaveDTO.shiftID(), e);
            throw new LeaveNotSavedException(String.format(
                    "Failed to save leave request for doctorID=%d and shiftID=%d",
                    leaveDTO.doctorID(), leaveDTO.shiftID()
            ));
        }
    }

    @Transactional
    public String approve(Long leaveID) {
        try {
            Leave leave = getLeave(leaveID);
            leave.setStatus(Status.APPROVED);

            Shift shift = shiftService.getShiftWithLock(leave.getShift().getId());
            Set<User> doctors = shift.getDoctors();
            doctors.remove(leave.getDoctor());
            shift.setDoctors(doctors);
            shift.setNoOfDoctors(doctors.size());
            shiftService.updateShiftByID(shift);
            leaveRepo.save(leave);

            return "leave request approved";
        } catch(Exception e) {
            logger.error("Error while saving approved Leave Request for LeaveID={}", leaveID, e);
            throw new LeaveNotSavedException(String.format(
                    "Failed to update leave status in database for leaveId=%d", leaveID
            ));
        }
    }

    @Transactional
    public String reject(Long leaveID) {
        try {
            Leave leave = getLeave(leaveID);
            leave.setStatus(Status.REJECTED);
            leaveRepo.save(leave);

            return "leave request rejected";
        } catch(Exception e) {
            logger.error("Error while trying to reject Leave Request for LeaveID={}", leaveID, e);
            throw new LeaveNotSavedException(String.format("Failed to update leave status in database for leaveId=%d", leaveID));
        }
    }

    @Transactional
    public Leave getLeave(Long leaveID){
        return leaveRepo.findById(leaveID).orElseThrow(() -> new LeaveNotFoundException("User - (" + leaveID + ") not found."));
    }

    @Transactional
    public List<Leave> getLeaves() {
        try {
            return leaveRepo.findAll();
        } catch (Exception e) {
            logger.error("Failed to fetch all leaves from database", e);
            throw new LeaveRetrievalException("Error occurred while retrieving all leaves");
        }
    }

    @Transactional
    public List<Leave> getLeaveByDoctor(Long doctorID) {
        try {
            return leaveRepo.findByDoctorId(doctorID);
        } catch (Exception e) {
            logger.error("Failed to get leaves for doctorID={}", doctorID, e);
            throw new LeaveRetrievalException(String.format(
                    "Error occurred while retrieving leaves for doctorID=%d", doctorID
            ));
        }
    }
}
