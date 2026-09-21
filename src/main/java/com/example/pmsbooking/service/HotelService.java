package com.example.pmsbooking.service;

import com.example.pmsbooking.entity.Hotel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {

    public List<Hotel> getHotels() {
        return List.of(
                new Hotel(
                        1L,
                        "Северная Звезда",
                        "Amsterdam, Netherlands",
                        "Современный городской отель в центре Амстердама. К услугам гостей комфортные " +
                                "номера, бесплатный Wi-Fi и круглосуточная стойка регистрации."
                )
        );
    }

    public Hotel getHotelById(Long id) {
        return getHotels().stream()
                .filter(hotel -> hotel.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
