package com.example.pmsbooking.scheduler;

import com.example.pmsbooking.entity.Booking;
import com.example.pmsbooking.entity.BookingStatus;
import com.example.pmsbooking.service.TaskService;
import com.example.pmsbooking.repository.BookingRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class CheckoutScheduler {

    private final BookingRepository bookingRepository;
    private final TaskService taskService;

    public CheckoutScheduler(
            BookingRepository bookingRepository,
            TaskService taskService) {
        this.bookingRepository = bookingRepository;
        this.taskService = taskService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void createCheckoutCleaningTasks() {
        LocalDate today = LocalDate.now();

        List<Booking> bookings =
                bookingRepository.findByCheckOutAndStatus(
                        today,
                        BookingStatus.CONFIRMED
                );

        for (Booking booking : bookings) {
            taskService.createCheckoutCleaningTask(booking);
        }
    }
}
