package com.example.pmsbooking.repository;

import com.example.pmsbooking.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByOrderByCreatedAtDesc();

    List<Task> findByHotelIdOrderByCreatedAtDesc(Long hotelId);

    List<Task> findByAssignedStaffIdOrderByCreatedAtDesc(Long assignedStaffId);
}
