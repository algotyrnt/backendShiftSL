package com.shiftsl.backend.Service;

import com.shiftsl.backend.DTO.LeaveDTO;
import com.shiftsl.backend.Exceptions.LeaveNotFoundException;
import com.shiftsl.backend.model.Leave;
import com.shiftsl.backend.model.Shift;
import com.shiftsl.backend.model.Status;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.repo.LeaveRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class LeaveService {

    private final LeaveRepo leaveRepo;
    private final ShiftService shiftService;
    private final UserService userService;

    @Transactional
    public Leave requestLeave(LeaveDTO leaveDTO) {
        User doctor = userService.getUserById(leaveDTO.doctorID());
        Shift shift = shiftService.getShiftByID(leaveDTO.shiftID());

        Leave leave = new Leave();
        leave.setShift(shift);
        leave.setDoctor(doctor);
        leave.setStatus(Status.PENDING);
        leave.setType(leaveDTO.type());
        leave.setCause(leaveDTO.cause());

        return leaveRepo.save(leave);
    }

    @Transactional
    public String approve(Long leaveID) {
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
    }

    @Transactional
    public String reject(Long leaveID) {
        Leave leave = getLeave(leaveID);
        leave.setStatus(Status.REJECTED);
        leaveRepo.save(leave);

        return "leave request rejected";
    }

    public Leave getLeave(Long leaveID){
        return leaveRepo.findById(leaveID).orElseThrow(() -> new LeaveNotFoundException("User - (" + leaveID + ") not found."));
    }

    public List<Leave> getLeaves() {
        return leaveRepo.findAll();
    }

    public List<Leave> getLeaveByDoctor(Long doctorID) {
        return leaveRepo.findByDoctors_Id(doctorID);
    }
}
