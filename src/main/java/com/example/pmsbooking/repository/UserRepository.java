package com.example.pmsbooking.repository;

import com.example.pmsbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
