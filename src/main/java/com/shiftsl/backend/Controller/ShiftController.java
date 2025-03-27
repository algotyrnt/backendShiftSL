package com.shiftsl.backend.Controller;

import com.shiftsl.backend.DTO.ShiftDTO;
import com.shiftsl.backend.Service.ShiftService;
import com.shiftsl.backend.model.Shift;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shift")
@AllArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    // Ward Admin creates a shift and assigns a doctor
    @PostMapping("/create/{wardID}")
    @PreAuthorize("hasAnyAuthority('WARD_ADMIN')")
    public ResponseEntity<Shift> createShift(@RequestBody ShiftDTO shift, @PathVariable Long wardID) {
        return new ResponseEntity<>(shiftService.createShift(shift, wardID), HttpStatus.OK);
    }

    // Get all available shifts in the shift pool
    @GetMapping("/available")
    @PreAuthorize("hasAnyAuthority('WARD_ADMIN', 'DOCTOR_PERM', 'DOCTOR_TEMP')")
    public ResponseEntity<List<Shift>> getAvailableShifts() {
        return ResponseEntity.ok(shiftService.getAvailableShifts());
    }

    // Doctor request a shift from the shift pool
    @PutMapping("/claim/{doctorId}/{shiftId}")
    @PreAuthorize("hasAnyAuthority('DOCTOR_PERM', 'DOCTOR_TEMP')")
    public ResponseEntity<String> requestShift(@PathVariable Long doctorId, @PathVariable Long shiftId) {
        shiftService.claimShift(doctorId, shiftId);
        return ResponseEntity.ok("Shift claimed successfully");
    }

    // Get all shifts allocated for the selected doctor
    @GetMapping("/{doctorId}")
    @PreAuthorize("hasAnyAuthority('DOCTOR_PERM', 'DOCTOR_TEMP')")
    public ResponseEntity<List<Shift>> getShiftsForDoctor(@PathVariable Long doctorId){
        return ResponseEntity.ok(shiftService.getShiftsForDoctor(doctorId));
    }

    // Get all shifts
    @GetMapping
    @PreAuthorize("hasAnyAuthority('WARD_ADMIN', 'DOCTOR_PERM', 'DOCTOR_TEMP')")
    public ResponseEntity<List<Shift>> getAllShifts(){
        return ResponseEntity.ok(shiftService.getAllShifts());
    }

    //to do - shift swap, swap accept or reject
}
