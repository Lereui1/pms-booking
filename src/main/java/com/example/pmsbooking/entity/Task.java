package com.example.pmsbooking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_staff_id")
    private User assignedStaff;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDate dueDate;

    private LocalDateTime createdAt;

    protected Task() {
    }

    public Task(
            Hotel hotel,
            Room room,
            Booking booking,
            User assignedStaff,
            String title,
            String description,
            LocalDate dueDate
    ) {
        this.hotel = hotel;
        this.room = room;
        this.booking = booking;
        this.assignedStaff = assignedStaff;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = TaskStatus.BACKLOG;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public Room getRoom() {
        return room;
    }

    public Booking getBooking() {
        return booking;
    }

    public User getAssignedStaff() {
        return assignedStaff;
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

    public void updateDetails(
            Hotel hotel,
            Room room,
            Booking booking,
            User assignedStaff,
            String title,
            String description,
            LocalDate dueDate
    ) {
        this.hotel = hotel;
        this.room = room;
        this.booking = booking;
        this.assignedStaff = assignedStaff;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public void changeStatus(TaskStatus status) {
        this.status = status;
    }
}
