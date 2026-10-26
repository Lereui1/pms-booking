package com.example.pmsbooking.repository;

import com.example.pmsbooking.entity.Booking;
import com.example.pmsbooking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByRoomIdAndStatusNotAndCheckInLessThanAndCheckOutGreaterThan(
            Long roomId,
            BookingStatus status,
            LocalDate checkOut,
            LocalDate checkIn
    );

    List<Booking> findByCheckOutAndStatus(
            LocalDate checkOut,
            BookingStatus status
    );
}
