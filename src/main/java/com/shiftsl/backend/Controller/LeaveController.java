package com.shiftsl.backend.Controller;

import com.shiftsl.backend.DTO.LeaveDTO;
import com.shiftsl.backend.Service.LeaveService;
import com.shiftsl.backend.model.Leave;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave")
@AllArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping("/request")
    public ResponseEntity<Leave> requestLeave(@RequestBody LeaveDTO leaveDTO){
        return ResponseEntity.ok(leaveService.requestLeave(leaveDTO));
    }

    @PutMapping("/{leaveID}/approve")
    public ResponseEntity<String> approveLeave(@PathVariable Long leaveID){
        return ResponseEntity.ok(leaveService.approve(leaveID));
    }

    @PutMapping("/{leaveID}/reject")
    public ResponseEntity<String> rejectLeave(@PathVariable Long leaveID){
        return ResponseEntity.ok(leaveService.reject(leaveID));
    }

    @GetMapping("/{leaveID}")
    public ResponseEntity<Leave> getLeave(@PathVariable Long leaveID){
        return ResponseEntity.ok(leaveService.getLeave(leaveID));
    }

    @GetMapping
    public ResponseEntity<List<Leave>> getLeaves(){
        return ResponseEntity.ok(leaveService.getLeaves());
    }
}
