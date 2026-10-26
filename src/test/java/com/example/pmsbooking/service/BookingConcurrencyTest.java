package com.example.pmsbooking.service;

import com.example.pmsbooking.dto.booking.BookingRequest;
import com.example.pmsbooking.entity.Hotel;
import com.example.pmsbooking.entity.Room;
import com.example.pmsbooking.entity.User;
import com.example.pmsbooking.exception.BookingConflictException;
import com.example.pmsbooking.repository.HotelRepository;
import com.example.pmsbooking.repository.RoomRepository;
import com.example.pmsbooking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
class BookingConcurrencyTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    /**
     * Проверяет защиту от Race Condition: два параллельных потока пытаются одновременно
     * забронировать один номер на одинаковые даты. Тест успешен, если сработает только одна бронь.
     */
    @Test
    void shouldPreventConcurrentBookingsForSameRoomAndDates() {
        String uniqueId = java.util.UUID.randomUUID().toString();

        User user = new User(
                "test-user-" + uniqueId,
                "test-" + uniqueId + "@example.com",
                "test-password",
                "CLIENT"
        );

        user = userRepository.save(user);
        final User savedUser = user;

        Hotel hotel = new Hotel(
                "Тестовый отель " + uniqueId,
                "Тестовый адрес",
                "Отель для проверки конкурентного бронирования"
        );

        hotel = hotelRepository.save(hotel);

        Room room = new Room(
                "101",
                "STANDARD",
                new java.math.BigDecimal("5000.00"),
                hotel
        );

        room = roomRepository.save(room);

        LocalDate checkIn = LocalDate.now().plusDays(30);
        LocalDate checkOut = checkIn.plusDays(5);

        BookingRequest request = new BookingRequest(
                room.getId(),
                checkIn,
                checkOut
        );

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            java.util.concurrent.Callable<Boolean> bookingTask = () -> {

                SecurityContext context =
                        SecurityContextHolder.createEmptyContext();

                context.setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                savedUser.getUsername(),
                                null,
                                List.of()
                        )
                );

                SecurityContextHolder.setContext(context);

                try {
                    ready.countDown();

                    if (!start.await(5, TimeUnit.SECONDS)) {
                        throw new IllegalStateException(
                                "Потоки не получили сигнал старта"
                        );
                    }

                    bookingService.createBooking(request);

                    return true;

                } catch (BookingConflictException e) {
                    return false;

                } finally {
                    SecurityContextHolder.clearContext();
                }
            };

            Future<Boolean> firstBooking = executor.submit(bookingTask);
            Future<Boolean> secondBooking = executor.submit(bookingTask);

            if (!ready.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException(
                        "Не все потоки готовы к старту"
                );
            }

            start.countDown();

            boolean firstSucceeded =
                    firstBooking.get(10, TimeUnit.SECONDS);

            boolean secondSucceeded =
                    secondBooking.get(10, TimeUnit.SECONDS);

            org.junit.jupiter.api.Assertions.assertNotEquals(
                    firstSucceeded,
                    secondSucceeded,
                    "Ровно одно бронирование должно завершиться успешно"
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Тест был прерван",
                    e
            );

        } catch (java.util.concurrent.ExecutionException
                 | java.util.concurrent.TimeoutException e) {

            throw new IllegalStateException(
                    "Ошибка при выполнении конкурентного бронирования",
                    e
            );

        } finally {
            start.countDown();
            executor.shutdownNow();
        }
    }
}
