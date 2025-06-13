package com.shiftsl.backend.repo;

import com.shiftsl.backend.model.Leave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepo extends JpaRepository<Leave, Long> {

    List<Leave> findByDoctors_Id(Long doctorId);
    
}
