package com.example.pmsbooking.service;

import com.example.pmsbooking.entity.Hotel;
import com.example.pmsbooking.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public List<Hotel> getHotels() {
        return hotelRepository.findAll();
    }

    public Hotel getHotelById(Long id) {
        return hotelRepository.findById(id)
                .orElse(null);
    }

    public Hotel createHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }
}
