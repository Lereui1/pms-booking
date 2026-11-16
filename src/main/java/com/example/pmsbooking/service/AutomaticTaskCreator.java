package com.example.pmsbooking.service;

import com.example.pmsbooking.entity.Booking;
import com.example.pmsbooking.entity.BookingStatus;
import com.example.pmsbooking.entity.Hotel;
import com.example.pmsbooking.entity.Room;
import com.example.pmsbooking.entity.Task;
import com.example.pmsbooking.exception.ResourceNotFoundException;
import com.example.pmsbooking.repository.BookingRepository;
import com.example.pmsbooking.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutomaticTaskCreator {

    private final TaskRepository taskRepository;
    private final BookingRepository bookingRepository;

    public AutomaticTaskCreator(
            TaskRepository taskRepository,
            BookingRepository bookingRepository
    ) {
        this.taskRepository = taskRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public boolean createBookingPreparationTask(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: " + bookingId
                ));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            return false;
        }

        String title = "Подготовить номер к заселению";

        if (taskRepository.existsByBookingIdAndTitle(booking.getId(), title)) {
            return false;
        }

        Room room = booking.getRoom();
        Hotel hotel = room.getHotel();

        Task task = new Task(
                hotel,
                room,
                booking,
                null,
                title,
                "Проверить готовность номера №" + room.getRoomNumber() + " к заселению",
                booking.getCheckIn()
        );

        taskRepository.saveAndFlush(task);

        return true;
    }

    @Transactional
    public boolean createCheckoutCleaningTask(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: " + bookingId
                ));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            return false;
        }

        String title = "Уборка номера после выезда";

        if (taskRepository.existsByBookingIdAndTitle(booking.getId(), title)) {
            return false;
        }

        Room room = booking.getRoom();
        Hotel hotel = room.getHotel();

        Task task = new Task(
                hotel,
                room,
                booking,
                null,
                title,
                "Провести уборку номера №" + room.getRoomNumber(),
                booking.getCheckOut()
        );

        taskRepository.saveAndFlush(task);

        return true;
    }
}
