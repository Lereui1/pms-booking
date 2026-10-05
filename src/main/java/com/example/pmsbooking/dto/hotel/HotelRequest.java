package com.example.pmsbooking.dto.hotel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class HotelRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 255)
    private String address;

    @NotBlank
    @Size(max = 1000)
    private String description;

    public HotelRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
