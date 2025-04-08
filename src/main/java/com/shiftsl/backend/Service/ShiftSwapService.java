package com.shiftsl.backend.Service;

import com.shiftsl.backend.model.Shift;
import com.shiftsl.backend.model.ShiftSwap;
import com.shiftsl.backend.model.StatusSwap;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.repo.ShiftSwapRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ShiftSwapService {

    private final ShiftSwapRepo shiftSwapRepo;
    private final ShiftService shiftService;
    private final UserService userService;


    public ShiftSwap requestSwap(Long sDocID, Long rDocID, Long shiftID) {
        ShiftSwap shiftSwap = new ShiftSwap();
        
        shiftSwap.setShift(shiftService.getShiftByID(shiftID));
        shiftSwap.setSender(userService.getUserById(sDocID));
        shiftSwap.setReceiver(userService.getUserById(rDocID));
        shiftSwap.setReceiverStat(StatusSwap.PENDING);
        
        return shiftSwapRepo.save(shiftSwap);
    }
    
    public ShiftSwap getShiftSwap(Long swapID){
        return shiftSwapRepo.findById(swapID).orElseThrow();
    }

    public String accept(Long swapID) {
        ShiftSwap shiftSwap = getShiftSwap(swapID);

        Shift shift = shiftSwap.getShift();
        Set<User> doctors = shift.getDoctors();
        doctors.remove(shiftSwap.getSender());
        doctors.add(shiftSwap.getReceiver());
        shift.setDoctors(doctors);
        shiftService.updateShiftByID(shift);
        
        shiftSwap.setReceiverStat(StatusSwap.ACCEPTED);
        
        shiftSwapRepo.save(shiftSwap);

        return "shift swap accepted by the receiving doctor";
    }

    public String reject(Long swapID) {
        ShiftSwap shiftSwap = getShiftSwap(swapID);

        shiftSwap.setReceiverStat(StatusSwap.REJECTED);

        shiftSwapRepo.save(shiftSwap);

        return "shift swap rejected by the receiving doctor";
    }

    public List<ShiftSwap> getSwapReceivedByDoctor(Long doctorID) {
        return shiftSwapRepo.findByReceiverId(doctorID);
    }

    public List<ShiftSwap> getSwapSentByDoctor(Long doctorID) {
        return shiftSwapRepo.findBySenderId(doctorID);
    }
}
