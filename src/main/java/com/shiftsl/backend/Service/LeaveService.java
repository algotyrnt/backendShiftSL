package com.shiftsl.backend.Service;

import com.shiftsl.backend.model.Leave;
import com.shiftsl.backend.model.Shift;
import com.shiftsl.backend.model.Status;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.repo.LeaveRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LeaveService {

    private final LeaveRepo leaveRepo;
    private final ShiftService shiftService;
    private final UserService userService;

    public Leave requestLeave(Long shiftID, Long doctorID) {
        User doctor = userService.getUserById(doctorID);
        Shift shift = shiftService.getShiftByID(shiftID);

        Leave leave = new Leave();
        leave.setShift(shift);
        leave.setDoctor(doctor);
        leave.setStatus(Status.PENDING);

        return leaveRepo.save(leave);
    }
}
