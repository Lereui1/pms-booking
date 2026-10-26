package com.example.pmsbooking.scheduler;

import com.example.pmsbooking.entity.Booking;
import com.example.pmsbooking.entity.BookingStatus;
import com.example.pmsbooking.repository.BookingRepository;
import com.example.pmsbooking.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

class CheckoutSchedulerTest {

    private BookingRepository bookingRepository;
    private TaskService taskService;
    private CheckoutScheduler checkoutScheduler;

    /**
     * Инициализирует заглушки для BookingRepository и TaskService, а затем внедряет
     * их в новый объект класса CheckoutScheduler перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        taskService = mock(TaskService.class);

        checkoutScheduler = new CheckoutScheduler(
                bookingRepository,
                taskService
        );
    }

    /**
     * Проверяет, что задачи на уборку успешно создаются для подтвержденных бронирований,
     * у которых дата выезда запланирована на сегодня.
     */
    @Test
    void shouldCreateCleaningTasksForBookingsCheckingOutToday() {
        LocalDate today = LocalDate.now();

        Booking booking = mock(Booking.class);

        when(bookingRepository.findByCheckOutAndStatus(
                today,
                BookingStatus.CONFIRMED
        )).thenReturn(List.of(booking));

        checkoutScheduler.createCheckoutCleaningTasks();

        verify(bookingRepository).findByCheckOutAndStatus(
                today,
                BookingStatus.CONFIRMED
        );

        verify(taskService).createCheckoutCleaningTask(booking);
    }

    /**
     * Проверяет, что задачи на уборку не создаются, если на сегодня
     * нет бронирований с запланированным выездом.
     */
    @Test
    void shouldNotCreateTasksWhenNoBookingsCheckOutToday() {
        LocalDate today = LocalDate.now();

        when(bookingRepository.findByCheckOutAndStatus(
                today,
                BookingStatus.CONFIRMED
        )).thenReturn(List.of());

        checkoutScheduler.createCheckoutCleaningTasks();

        verify(taskService, never())
                .createCheckoutCleaningTask(any());
    }
}
