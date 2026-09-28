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
}
