package com.shiftsl.backend.Controller;

import com.shiftsl.backend.DTO.LeaveDTO;
import com.shiftsl.backend.Service.LeaveService;
import com.shiftsl.backend.model.Leave;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave")
@AllArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    // request a leave
    @PostMapping("/request")
    @PreAuthorize("hasAnyAuthority('DOCTOR_PERM', 'DOCTOR_TEMP')")
    public ResponseEntity<Leave> requestLeave(@RequestBody LeaveDTO leaveDTO){
        return ResponseEntity.ok(leaveService.requestLeave(leaveDTO));
    }

    //approve a leave request
    @PutMapping("/approve/{leaveID}")
    @PreAuthorize("hasAnyAuthority('WARD_ADMIN')")
    public ResponseEntity<String> approveLeave(@PathVariable Long leaveID){
        return ResponseEntity.ok(leaveService.approve(leaveID));
    }

    //reject a leave request
    @PutMapping("/reject/{leaveID}")
    @PreAuthorize("hasAnyAuthority('WARD_ADMIN')")
    public ResponseEntity<String> rejectLeave(@PathVariable Long leaveID){
        return ResponseEntity.ok(leaveService.reject(leaveID));
    }

    //get a leave from leaveID
    @GetMapping("/{leaveID}")
    @PreAuthorize("hasAnyAuthority('WARD_ADMIN', 'DOCTOR_PERM', 'DOCTOR_TEMP')")
    public ResponseEntity<Leave> getLeave(@PathVariable Long leaveID){
        return ResponseEntity.ok(leaveService.getLeave(leaveID));
    }

    //get all leaves
    @GetMapping
    @PreAuthorize("hasAnyAuthority('WARD_ADMIN', 'DOCTOR_PERM', 'DOCTOR_TEMP')")
    public ResponseEntity<List<Leave>> getLeaves(){
        return ResponseEntity.ok(leaveService.getLeaves());
    }

    //get all leaves for a doctor
    @GetMapping("/byDoc/{doctorID}")
    @PreAuthorize("hasAnyAuthority('DOCTOR_PERM', 'DOCTOR_TEMP')")
    public ResponseEntity<List<Leave>> getLeaveByDoctor(@PathVariable Long doctorID){
        return ResponseEntity.ok(leaveService.getLeaveByDoctor(doctorID));
    }
}
