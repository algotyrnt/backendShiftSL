package com.shiftsl.backend.repo;

import com.shiftsl.backend.model.Role;
import com.shiftsl.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.graphql.data.GraphQlRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@GraphQlRepository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNo(String phoneNo);
    Optional<User> findByFirebaseUid(String firebaseUid);
    List<User> findByRoleIn(List<Role> roles);
}
