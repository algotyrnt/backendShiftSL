package com.shiftsl.backend.Controller;

import com.shiftsl.backend.Service.ShiftService;
import com.shiftsl.backend.model.Shift;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shift")
@AllArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    // Ward Admin creates a shift and assigns a doctor
    @PostMapping("/create/{wardAdminId}/{doctorId}")
    public ResponseEntity<Shift> createShift(
            @PathVariable Long wardAdminId,
            @RequestBody Shift shift,
            @PathVariable Long doctorId) {
        Shift createdShift = shiftService.createShift(wardAdminId, shift, doctorId);
        return new ResponseEntity<>(createdShift, HttpStatus.OK);
    }

    // Get all available shifts in the shift pool
    @GetMapping("/available")
    public ResponseEntity<List<Shift>> getAvailableShifts() {
        return ResponseEntity.ok(shiftService.getAvailableShifts());
    }

    // Doctor claims a shift from the shift pool
    @PutMapping("/claim/{doctorId}/{shiftId}")
    public ResponseEntity<String> claimShift(@PathVariable Long doctorId, @PathVariable Long shiftId) {
        shiftService.claimShift(doctorId, shiftId);
        return ResponseEntity.ok("Shift claimed successfully");
    }
}
