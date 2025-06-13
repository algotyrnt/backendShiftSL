package com.shiftsl.backend.Service;

import com.shiftsl.backend.Exceptions.ShiftNotFoundException;
import com.shiftsl.backend.Exceptions.ShiftSwapNotFoundException;
import com.shiftsl.backend.model.Shift;
import com.shiftsl.backend.model.ShiftSwap;
import com.shiftsl.backend.model.StatusSwap;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.repo.ShiftSwapRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ShiftSwapService {

    private final static Logger logger = LoggerFactory.getLogger(ShiftSwapService.class);

    private final ShiftSwapRepo shiftSwapRepo;
    private final ShiftService shiftService;
    private final UserService userService;


    @Transactional
    public ShiftSwap requestSwap(Long sDocID, Long rDocID, Long shiftID) {
        logger.info("creating request to swap {} with {} for shift {}", rDocID, sDocID, shiftID);
        ShiftSwap shiftSwap = new ShiftSwap();

        shiftSwap.setShift(shiftService.getShiftByID(shiftID));
        shiftSwap.setSender(userService.getUserById(sDocID));
        shiftSwap.setReceiver(userService.getUserById(rDocID));
        shiftSwap.setReceiverStat(StatusSwap.PENDING);

        return shiftSwapRepo.save(shiftSwap);
    }

    @Transactional
    public ShiftSwap getShiftSwap(Long swapID){
        logger.info("getting swap details for  id: {}", swapID);
        return shiftSwapRepo.findById(swapID).orElseThrow(() ->{
            logger.error("Unable to find swap details for swap id : {}", swapID);
            return new ShiftNotFoundException("Shift swap " + swapID + "not found.");
        });
    }

    @Transactional
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

    @Transactional
    public String reject(Long swapID) {
        ShiftSwap shiftSwap = getShiftSwap(swapID);

        shiftSwap.setReceiverStat(StatusSwap.REJECTED);

        shiftSwapRepo.save(shiftSwap);

        return "shift swap rejected by the receiving doctor";
    }

    @Transactional
    public List<ShiftSwap> getSwapReceivedByDoctor(Long doctorID) {
        try {
            logger.info("getting swap received by doctor {}", doctorID);
            return shiftSwapRepo.findByReceiverId(doctorID);
        } catch (Exception e) {
            logger.error("Unable to retrieve shift swap details for receiving doctor {}", doctorID);
            throw new ShiftSwapNotFoundException("Unable to find shift swap request details for doctor " + doctorID);
        }
    }

    @Transactional
    public List<ShiftSwap> getSwapSentByDoctor(Long doctorID) {
        try {
            logger.info("Getting swap sent by doctor {}", doctorID);
            return shiftSwapRepo.findBySenderId(doctorID);
        } catch (Exception e) {
            logger.error("Unable to retrieve shift swap details sent by doctor {}", doctorID);
            throw new ShiftSwapNotFoundException("Unable to find shift swap request details by doctor " + doctorID);
        }
    }
}
