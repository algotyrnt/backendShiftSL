package com.shiftsl.backend.Controller;

import com.shiftsl.backend.Service.LeaveService;
import com.shiftsl.backend.model.Leave;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave")
@AllArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping("/request/{shiftID}/{doctorID}")
    public ResponseEntity<Leave> requestLeave(@PathVariable Long shiftID, @PathVariable Long doctorID){
        return ResponseEntity.ok(leaveService.requestLeave(shiftID,doctorID));
    }
}
