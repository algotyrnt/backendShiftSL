package com.shiftsl.backend.Controller;

import com.shiftsl.backend.DTO.ShiftDTO;
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
    @PostMapping("/create/{wardID}")
    public ResponseEntity<Shift> createShift(@RequestBody ShiftDTO shift, @PathVariable Long wardID) {
        return new ResponseEntity<>(shiftService.createShift(shift, wardID), HttpStatus.OK);
    }

    // Get all available shifts in the shift pool
    @GetMapping("/available")
    public ResponseEntity<List<Shift>> getAvailableShifts() {
        return ResponseEntity.ok(shiftService.getAvailableShifts());
    }

    // Doctor request a shift from the shift pool
    @PutMapping("/claim/{doctorId}/{shiftId}")
    public ResponseEntity<String> requestShift(@PathVariable Long doctorId, @PathVariable Long shiftId) {
        shiftService.claimShift(doctorId, shiftId);
        return ResponseEntity.ok("Shift claimed successfully");
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<List<Shift>> getShiftsForDoctor(@PathVariable Long doctorId){
        return ResponseEntity.ok(shiftService.getShiftsForDoctor(doctorId));
    }

    @GetMapping
    public ResponseEntity<List<Shift>> getAllShifts(){
        return ResponseEntity.ok(shiftService.getAllShifts());
    }

    //to do - shift swap, swap accept or reject
}
