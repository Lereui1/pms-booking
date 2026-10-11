package com.example.pmsbooking.service;

import com.example.pmsbooking.dto.booking.BookingRequest;
import com.example.pmsbooking.dto.booking.BookingResponse;
import com.example.pmsbooking.entity.Booking;
import com.example.pmsbooking.entity.BookingStatus;
import com.example.pmsbooking.entity.Room;
import com.example.pmsbooking.entity.User;
import com.example.pmsbooking.exception.BookingConflictException;
import com.example.pmsbooking.exception.ResourceNotFoundException;
import com.example.pmsbooking.repository.BookingRepository;
import com.example.pmsbooking.repository.RoomRepository;
import com.example.pmsbooking.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public BookingService(
            BookingRepository bookingRepository,
            RoomRepository roomRepository,
            UserRepository userRepository) {

        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Room room = roomRepository.findByIdWithPessimisticLock(request.getRoomId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found"));

        if (!request.getCheckIn().isBefore(request.getCheckOut())) {
            throw new IllegalArgumentException(
                    "Check-out must be after check-in"
            );
        }

        boolean alreadyBooked = bookingRepository
                .existsByRoomIdAndStatusNotAndCheckInLessThanAndCheckOutGreaterThan(
                        request.getRoomId(),
                        BookingStatus.CANCELLED,
                        request.getCheckOut(),
                        request.getCheckIn()
                );

        if (alreadyBooked) {
            throw new BookingConflictException(
                    "Room is already booked for selected dates"
            );
        }

        Booking booking = new Booking(
                user,
                room,
                request.getCheckIn(),
                request.getCheckOut(),
                BookingStatus.CONFIRMED,
                LocalDateTime.now()
        );

        Booking savedBooking = bookingRepository.save(booking);

        return new BookingResponse(
                savedBooking.getId(),
                savedBooking.getUser().getId(),
                savedBooking.getRoom().getId(),
                savedBooking.getCheckIn(),
                savedBooking.getCheckOut(),
                savedBooking.getStatus().name(),
                savedBooking.getCreatedAt()
        );
    }
}
