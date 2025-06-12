package com.shiftsl.backend.repo;

import com.shiftsl.backend.model.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.graphql.data.GraphQlRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@GraphQlRepository
public interface WardRepo extends JpaRepository<Ward, Long> {
    Optional<Ward> findByName(String name);
}
