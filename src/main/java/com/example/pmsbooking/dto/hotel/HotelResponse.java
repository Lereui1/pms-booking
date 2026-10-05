package com.example.pmsbooking.dto.hotel;

public class HotelResponse {

    private Long id;
    private String name;
    private String address;
    private String description;

    public HotelResponse() {
    }

    public HotelResponse(Long id, String name, String address, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }
}
