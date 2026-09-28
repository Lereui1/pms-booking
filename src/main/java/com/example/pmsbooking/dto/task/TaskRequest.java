package com.example.pmsbooking.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TaskRequest {

    @NotNull
    private Long hotelId;

    private Long bookingId;

    @NotBlank
    private String title;

    @NotBlank
    private String description;
}
