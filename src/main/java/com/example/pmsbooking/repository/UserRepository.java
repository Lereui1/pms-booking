package com.example.pmsbooking.repository;

import com.example.pmsbooking.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "hotel")
    Optional<User> findByUsername(String username);
}
