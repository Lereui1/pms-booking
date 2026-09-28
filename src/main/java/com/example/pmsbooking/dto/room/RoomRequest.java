package com.example.pmsbooking.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class RoomRequest {

    @NotBlank
    private String roomNumber;

    @NotBlank
    private String type;

    @NotNull
    private BigDecimal pricePerNight;

    @NotNull
    private Long hotelId;
}
