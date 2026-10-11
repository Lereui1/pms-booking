package com.example.pmsbooking.controller;

import com.example.pmsbooking.dto.booking.BookingRequest;
import com.example.pmsbooking.dto.booking.BookingResponse;
import com.example.pmsbooking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request) {

        return ResponseEntity.ok(
                bookingService.createBooking(request)
        );
    }
}
