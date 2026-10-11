package com.example.pmsbooking.dto.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingResponse {

    private Long id;
    private Long userId;
    private Long roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String status;
    private LocalDateTime createdAt;

    public BookingResponse(
            Long id,
            Long userId,
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut,
            String status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
