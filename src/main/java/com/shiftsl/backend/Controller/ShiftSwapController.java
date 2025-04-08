package com.shiftsl.backend.Controller;

import com.shiftsl.backend.Service.ShiftSwapService;
import com.shiftsl.backend.model.ShiftSwap;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shiftSwap")
@AllArgsConstructor
public class ShiftSwapController {

    private final ShiftSwapService shiftSwapService;

    // request a shift swap
    @PostMapping("/request/{sDocID}/{rDocID}/{shiftID}")
    @PreAuthorize("hasAnyAuthority('DOCTOR_PERM', 'DOCTOR_TEMP')")
    public ResponseEntity<ShiftSwap> requestLeave(@PathVariable Long sDocID, @PathVariable Long rDocID, @PathVariable Long shiftID){
        return ResponseEntity.ok(shiftSwapService.requestSwap(sDocID, rDocID, shiftID));
    }

    //accept a shift swap request
    @PutMapping("/accept/{swapID}")
    @PreAuthorize("hasAnyAuthority('WARD_ADMIN')")
    public ResponseEntity<String> approveSwap(@PathVariable Long swapID){
        return ResponseEntity.ok(shiftSwapService.accept(swapID));
    }

    //reject a shift swap request
    @PutMapping("/reject/{swapID}")
    @PreAuthorize("hasAnyAuthority('WARD_ADMIN')")
    public ResponseEntity<String> rejectSwap(@PathVariable Long swapID){
        return ResponseEntity.ok(shiftSwapService.reject(swapID));
    }

    //get sent swap req for a doctor
    @GetMapping("/sent/{doctorID}")
    @PreAuthorize("hasAnyAuthority('DOCTOR_PERM', 'DOCTOR_TEMP')")
    public ResponseEntity<List<ShiftSwap>> getSwapSentByDoctor(@PathVariable Long doctorID){
        return ResponseEntity.ok(shiftSwapService.getSwapSentByDoctor(doctorID));
    }

    //get received swap req for a doctor
    @GetMapping("/received/{doctorID}")
    @PreAuthorize("hasAnyAuthority('DOCTOR_PERM', 'DOCTOR_TEMP')")
    public ResponseEntity<List<ShiftSwap>> getSwapReceivedByDoctor(@PathVariable Long doctorID){
        return ResponseEntity.ok(shiftSwapService.getSwapReceivedByDoctor(doctorID));
    }
}
