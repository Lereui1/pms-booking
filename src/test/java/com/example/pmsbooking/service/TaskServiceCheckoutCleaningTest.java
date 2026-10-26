package com.example.pmsbooking.service;

import com.example.pmsbooking.entity.*;
import com.example.pmsbooking.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceCheckoutCleaningTest {

    private TaskRepository taskRepository;
    private HotelRepository hotelRepository;
    private RoomRepository roomRepository;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;

    private TaskService taskService;

    /**
     * Инициализирует заглушки для всех необходимых репозиториев и внедряет
     * их в новый объект класса TaskService перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        hotelRepository = mock(HotelRepository.class);
        roomRepository = mock(RoomRepository.class);
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);

        taskService = new TaskService(
                taskRepository,
                hotelRepository,
                roomRepository,
                bookingRepository,
                userRepository
        );
    }

    /**
     * Проверяет, что задача на уборку не создается, если статус бронирования не CONFIRMED.
     * Гарантирует, что метод возвращает false и не обращается к репозиторию задач.
     */
    @Test
    void shouldNotCreateTaskWhenBookingIsNotConfirmed() {
        Booking booking = mock(Booking.class);

        when(booking.getStatus()).thenReturn(BookingStatus.CANCELLED);

        boolean result = taskService.createCheckoutCleaningTask(booking);

        assertFalse(result);

        verify(taskRepository, never()).save(any(Task.class));

        verify(taskRepository, never())
                .existsByBookingIdAndTitle(any(), anyString());
    }

    /**
     * Проверяет защиту от дубликатов: задача на уборку не должна создаваться повторно для одного бронирования.
     * Гарантирует, что метод возвращает false и отменяет сохранение, если такая задача уже есть в базе.
     */
    @Test
    void shouldNotCreateDuplicateCleaningTask() {
        Booking booking = mock(Booking.class);

        when(booking.getStatus()).thenReturn(BookingStatus.CONFIRMED);
        when(booking.getId()).thenReturn(1L);

        when(taskRepository.existsByBookingIdAndTitle(
                1L,
                "Уборка номера после выезда"
        )).thenReturn(true);

        boolean result = taskService.createCheckoutCleaningTask(booking);

        assertFalse(result);

        verify(taskRepository).existsByBookingIdAndTitle(
                1L,
                "Уборка номера после выезда"
        );

        verify(taskRepository, never()).save(any(Task.class));
    }

    /**
     * Проверяет успешное создание задачи на уборку, когда бронирование CONFIRMED
     * и такая задача еще не существует в базе.
     */
    @Test
    void shouldCreateCleaningTaskWhenBookingIsConfirmedAndTaskDoesNotExist() {
        Booking booking = mock(Booking.class);
        Room room = mock(Room.class);
        Hotel hotel = mock(Hotel.class);

        LocalDate checkOut = LocalDate.of(2026, 10, 25);

        when(booking.getStatus()).thenReturn(BookingStatus.CONFIRMED);
        when(booking.getId()).thenReturn(1L);
        when(booking.getRoom()).thenReturn(room);
        when(booking.getCheckOut()).thenReturn(checkOut);

        when(room.getHotel()).thenReturn(hotel);
        when(room.getRoomNumber()).thenReturn("101");

        when(taskRepository.existsByBookingIdAndTitle(
                1L,
                "Уборка номера после выезда"
        )).thenReturn(false);

        boolean result = taskService.createCheckoutCleaningTask(booking);

        assertTrue(result);

        verify(taskRepository).save(argThat(task ->
                           task.getHotel() == hotel
                        && task.getRoom() == room
                        && task.getBooking() == booking
                        && task.getAssignedStaff() == null
                        && task.getTitle().equals(
                        "Уборка номера после выезда"
                )
                        && task.getDescription().equals(
                        "Провести уборку номера №101"
                )
                        && task.getDueDate().equals(checkOut)
                        && task.getStatus() == TaskStatus.BACKLOG
        ));
    }
}
