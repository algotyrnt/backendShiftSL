package com.shiftsl.backend.Controller;

import com.shiftsl.backend.Service.WardService;
import com.shiftsl.backend.model.Ward;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ward")
@AllArgsConstructor
public class WardController {

    private final WardService wardService;

    // Create a new ward
    @PostMapping("/create")
    public ResponseEntity<Ward> createWard(@RequestBody Ward ward) {
        Ward savedWard = wardService.createWard(ward);
        return new ResponseEntity<>(savedWard, HttpStatus.OK);
    }

    // Update an existing ward
    @PutMapping("/update")
    public ResponseEntity<Ward> updateWard(@RequestBody Ward ward) {
        Ward updatedWard = wardService.updateWard(ward);
        return ResponseEntity.ok(updatedWard);
    }

    // Get a ward by name
    @GetMapping("/name/{name}")
    public ResponseEntity<Optional<Ward>> getWardByName(@PathVariable String name) {
        return ResponseEntity.ok(wardService.findByName(name));
    }

    // Get all wards
    @GetMapping("/all")
    public ResponseEntity<List<Ward>> getAllWards() {
        return ResponseEntity.ok(wardService.getWardList());
    }

    // Delete a ward by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteWardById(@PathVariable Long id) {
        wardService.deleteWardById(id);
        return ResponseEntity.ok("Ward deleted successfully");
    }
}