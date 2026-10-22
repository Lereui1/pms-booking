package com.example.pmsbooking.dto.task;

import com.example.pmsbooking.entity.Task;
import com.example.pmsbooking.entity.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskResponse {

    private Long id;

    private Long hotelId;

    private Long roomId;

    private Long bookingId;

    private Long assignedStaffId;

    private String title;

    private String description;

    private TaskStatus status;

    private LocalDate dueDate;

    private LocalDateTime createdAt;

    public TaskResponse(
            Long id,
            Long hotelId,
            Long roomId,
            Long bookingId,
            Long assignedStaffId,
            String title,
            String description,
            TaskStatus status,
            LocalDate dueDate,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.hotelId = hotelId;
        this.roomId = roomId;
        this.bookingId = bookingId;
        this.assignedStaffId = assignedStaffId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
    }

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getHotel().getId(),
                task.getRoom() != null ? task.getRoom().getId() : null,
                task.getBooking() != null ? task.getBooking().getId() : null,
                task.getAssignedStaff() != null
                        ? task.getAssignedStaff().getId()
                        : null,
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate(),
                task.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public Long getAssignedStaffId() {
        return assignedStaffId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
