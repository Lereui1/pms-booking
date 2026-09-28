package com.example.pmsbooking.dto.task;

import java.time.LocalDateTime;

public class TaskResponse {

    private Long id;
    private Long hotelId;
    private Long bookingId;
    private String title;
    private String description;
    private String status;
    private LocalDateTime createdAt;
}
