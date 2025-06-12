package com.shiftsl.backend.repo;

import com.shiftsl.backend.model.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.graphql.data.GraphQlRepository;

import java.util.List;
import java.util.Optional;

@GraphQlRepository
public interface WardRepo extends JpaRepository<Ward, Long> {
    Optional<Ward> findByName(String name);
    List<Ward> findByWardAdmin_Id(Long wardAdminId);
}
