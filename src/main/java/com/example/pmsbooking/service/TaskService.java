package com.example.pmsbooking.service;

import com.example.pmsbooking.dto.task.TaskRequest;
import com.example.pmsbooking.dto.task.TaskResponse;
import com.example.pmsbooking.entity.*;
import com.example.pmsbooking.exception.AccessDeniedException;
import com.example.pmsbooking.exception.ResourceNotFoundException;
import com.example.pmsbooking.exception.ValidationException;
import com.example.pmsbooking.repository.*;
import com.example.pmsbooking.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        User currentUser = getCurrentUser();
        Long hotelId = getCurrentHotelIdOrThrow(currentUser);

        return taskRepository.findByHotelIdOrderByCreatedAtDesc(hotelId)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        User currentUser = getCurrentUser();
        Long hotelId = getCurrentHotelIdOrThrow(currentUser);

        Task task = getTaskOrThrow(id);

        if (!task.getHotel().getId().equals(hotelId)) {
            throw new AccessDeniedException(
                    "You do not have access to this task"
            );
        }

        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        User currentUser = getCurrentUser();
        Long currentHotelId = getCurrentHotelIdOrThrow(currentUser);

        if (!currentHotelId.equals(request.getHotelId())) {
            throw new AccessDeniedException(
                    "You cannot create tasks for another hotel"
            );
        }

        Hotel hotel = getHotelOrThrow(request.getHotelId());
        Room room = getRoomOrThrow(request.getRoomId());
        Booking booking = getBookingOrThrow(request.getBookingId());
        User assignedStaff = getStaffOrThrow(request.getAssignedStaffId());

        if (assignedStaff != null
                && (assignedStaff.getHotel() == null
                || !assignedStaff.getHotel().getId().equals(currentHotelId))) {
            throw new AccessDeniedException(
                    "Assigned staff must belong to your hotel"
            );
        }

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
    public boolean createCheckoutCleaningTask(Booking booking) {
        String title = "Уборка номера после выезда";

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            return false;
        }

        if (taskRepository.existsByBookingIdAndTitle(
                booking.getId(),
                title
        )) {
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

        taskRepository.save(task);
        return true;
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {
        User currentUser = getCurrentUser();
        Long currentHotelId = getCurrentHotelIdOrThrow(currentUser);

        Task task = getTaskOrThrow(id);

        if (!task.getHotel().getId().equals(currentHotelId)) {
            throw new AccessDeniedException(
                    "You do not have access to this task"
            );
        }

        if (!currentHotelId.equals(request.getHotelId())) {
            throw new AccessDeniedException(
                    "You cannot move tasks to another hotel"
            );
        }

        Hotel hotel = getHotelOrThrow(request.getHotelId());
        Room room = getRoomOrThrow(request.getRoomId());
        Booking booking = getBookingOrThrow(request.getBookingId());
        User assignedStaff = getStaffOrThrow(request.getAssignedStaffId());

        if (assignedStaff != null
                && (assignedStaff.getHotel() == null
                || !assignedStaff.getHotel().getId().equals(currentHotelId))) {
            throw new AccessDeniedException(
                    "Assigned staff must belong to your hotel"
            );
        }

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
        User currentUser = getCurrentUser();
        Long currentHotelId = getCurrentHotelIdOrThrow(currentUser);

        Task task = getTaskOrThrow(id);

        if (!task.getHotel().getId().equals(currentHotelId)) {
            throw new AccessDeniedException(
                    "You do not have access to this task"
            );
        }

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
        User currentUser = getCurrentUser();
        Long currentHotelId = getCurrentHotelIdOrThrow(currentUser);

        Task task = getTaskOrThrow(id);

        if (!task.getHotel().getId().equals(currentHotelId)) {
            throw new AccessDeniedException(
                    "You do not have access to this task"
            );
        }

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

    private User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new AccessDeniedException("Access denied");
        }

        return userDetails.getUser();
    }

    private Long getCurrentHotelIdOrThrow(User user) {
        if (user.getHotel() == null) {
            throw new AccessDeniedException(
                    "User is not assigned to a hotel"
            );
        }

        return user.getHotel().getId();
    }
}
