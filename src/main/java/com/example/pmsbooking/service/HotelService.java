package com.example.pmsbooking.service;

import com.example.pmsbooking.dto.hotel.HotelRequest;
import com.example.pmsbooking.dto.hotel.HotelResponse;
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

    public List<HotelResponse> getHotels() {
        return hotelRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public HotelResponse getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElse(null);

        if (hotel == null) {
            return null;
        }

        return toResponse(hotel);
    }

    public HotelResponse createHotel(HotelRequest request) {
        Hotel hotel = new Hotel(
                request.getName(),
                request.getAddress(),
                request.getDescription()
        );

        Hotel savedHotel = hotelRepository.save(hotel);

        return toResponse(savedHotel);
    }

    private HotelResponse toResponse(Hotel hotel) {
        return new HotelResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getAddress(),
                hotel.getDescription()
        );
    }
}
