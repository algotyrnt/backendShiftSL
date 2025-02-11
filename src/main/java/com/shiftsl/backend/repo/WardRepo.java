package com.shiftsl.backend.repo;

import com.shiftsl.backend.model.Ward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WardRepo extends JpaRepository<Ward, Long> {
}
