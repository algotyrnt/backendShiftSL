package com.shiftsl.backend.Controller;

import com.shiftsl.backend.Service.WardService;
import com.shiftsl.backend.model.Ward;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ward")
@AllArgsConstructor
public class WardController {

    private final WardService wardService;

    // Create a new ward
    @PostMapping("/create/{wardAdminID}/{wardName}")
    @PreAuthorize("hasAnyAuthority('HR_ADMIN')")
    public ResponseEntity<Ward> createWard(@PathVariable Long wardAdminID, @PathVariable String wardName) {
        return ResponseEntity.ok(wardService.createWard(wardAdminID, wardName));
    }

    // Update an existing ward
    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('HR_ADMIN')")
    public ResponseEntity<Ward> updateWard(@RequestBody Ward ward) {
        Ward updatedWard = wardService.updateWard(ward);
        return ResponseEntity.ok(updatedWard);
    }

    // Get a ward by name
    @GetMapping("/{name}")
    @PreAuthorize("hasAnyAuthority('HR_ADMIN', 'WARD_ADMIN', 'DOCTOR_PERM', 'DOCTOR_TEMP')")
    public ResponseEntity<Optional<Ward>> getWardByName(@PathVariable String name) {
        return ResponseEntity.ok(wardService.findByName(name));
    }

    // Get all wards
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('HR_ADMIN', 'WARD_ADMIN')")
    public ResponseEntity<List<Ward>> getAllWards() {
        return ResponseEntity.ok(wardService.getWardList());
    }

    // Delete a ward by ID
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('HR_ADMIN')")
    public ResponseEntity<String> deleteWardById(@PathVariable Long id) {
        wardService.deleteWardById(id);
        return ResponseEntity.ok("Ward deleted successfully");
    }
}