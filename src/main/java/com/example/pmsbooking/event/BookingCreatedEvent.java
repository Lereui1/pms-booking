package com.example.pmsbooking.event;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingCreatedEvent(
        Long bookingId,
        Long userId,
        Long roomId,
        Long hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        LocalDateTime createdAt
) {
}
