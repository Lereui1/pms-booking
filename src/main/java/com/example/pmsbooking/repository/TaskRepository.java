package com.example.pmsbooking.repository;

import com.example.pmsbooking.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
