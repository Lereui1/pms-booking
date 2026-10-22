package com.example.pmsbooking.service;

import com.example.pmsbooking.dto.task.TaskRequest;
import com.example.pmsbooking.dto.task.TaskResponse;
import com.example.pmsbooking.entity.Booking;
import com.example.pmsbooking.entity.Hotel;
import com.example.pmsbooking.entity.Room;
import com.example.pmsbooking.entity.Task;
import com.example.pmsbooking.entity.TaskStatus;
import com.example.pmsbooking.entity.User;
import com.example.pmsbooking.exception.AccessDeniedException;
import com.example.pmsbooking.exception.ResourceNotFoundException;
import com.example.pmsbooking.exception.ValidationException;
import com.example.pmsbooking.repository.BookingRepository;
import com.example.pmsbooking.repository.HotelRepository;
import com.example.pmsbooking.repository.RoomRepository;
import com.example.pmsbooking.repository.TaskRepository;
import com.example.pmsbooking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            HotelRepository hotelRepository,
            RoomRepository roomRepository,
            BookingRepository bookingRepository,
            UserRepository userRepository
    ) {
        this.taskRepository = taskRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = getTaskOrThrow(id);
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        Hotel hotel = getHotelOrThrow(request.getHotelId());
        Room room = getRoomOrThrow(request.getRoomId());
        Booking booking = getBookingOrThrow(request.getBookingId());
        User assignedStaff = getStaffOrThrow(request.getAssignedStaffId());

        validateRelationships(hotel, room, booking);

        Task task = new Task(
                hotel,
                room,
                booking,
                assignedStaff,
                request.getTitle(),
                request.getDescription(),
                request.getDueDate()
        );

        Task savedTask = taskRepository.save(task);

        return TaskResponse.from(savedTask);
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = getTaskOrThrow(id);

        Hotel hotel = getHotelOrThrow(request.getHotelId());
        Room room = getRoomOrThrow(request.getRoomId());
        Booking booking = getBookingOrThrow(request.getBookingId());
        User assignedStaff = getStaffOrThrow(request.getAssignedStaffId());

        validateRelationships(hotel, room, booking);

        task.updateDetails(
                hotel,
                room,
                booking,
                assignedStaff,
                request.getTitle(),
                request.getDescription(),
                request.getDueDate()
        );

        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long id, TaskStatus newStatus) {
        Task task = getTaskOrThrow(id);

        boolean validTransition =
                (task.getStatus() == TaskStatus.BACKLOG
                        && newStatus == TaskStatus.IN_PROGRESS)
                        || (task.getStatus() == TaskStatus.IN_PROGRESS
                        && newStatus == TaskStatus.DONE);

        if (!validTransition) {
            throw new ValidationException(
                    "Invalid task status transition: "
                            + task.getStatus() + " -> " + newStatus
            );
        }

        task.changeStatus(newStatus);

        return TaskResponse.from(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = getTaskOrThrow(id);
        taskRepository.delete(task);
    }

    private Task getTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found"));
    }

    private Hotel getHotelOrThrow(Long id) {
        if (id == null) {
            throw new ValidationException("Hotel ID is required");
        }

        return hotelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hotel not found"));
    }

    private Room getRoomOrThrow(Long id) {
        if (id == null) {
            return null;
        }

        return roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found"));
    }

    private Booking getBookingOrThrow(Long id) {
        if (id == null) {
            return null;
        }

        return bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking not found"));
    }

    private User getStaffOrThrow(Long id) {
        if (id == null) {
            return null;
        }

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (!"STAFF".equals(user.getRole())) {
            throw new AccessDeniedException(
                    "Assigned user must have STAFF role"
            );
        }

        return user;
    }

    private void validateRelationships(
            Hotel hotel,
            Room room,
            Booking booking
    ) {
        if (room != null
                && !room.getHotel().getId().equals(hotel.getId())) {
            throw new ValidationException(
                    "Room does not belong to the selected hotel"
            );
        }

        if (booking != null
                && !booking.getRoom().getHotel().getId().equals(hotel.getId())) {
            throw new ValidationException(
                    "Booking does not belong to the selected hotel"
            );
        }

        if (booking != null && room != null
                && !booking.getRoom().getId().equals(room.getId())) {
            throw new ValidationException(
                    "Booking does not belong to the selected room"
            );
        }
    }
}
