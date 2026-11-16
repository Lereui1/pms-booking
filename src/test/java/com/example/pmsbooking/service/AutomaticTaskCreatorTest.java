
package com.example.pmsbooking.service;

import com.example.pmsbooking.entity.Booking;
import com.example.pmsbooking.entity.BookingStatus;
import com.example.pmsbooking.entity.Hotel;
import com.example.pmsbooking.entity.Room;
import com.example.pmsbooking.entity.Task;
import com.example.pmsbooking.entity.TaskStatus;
import com.example.pmsbooking.entity.User;
import com.example.pmsbooking.repository.BookingRepository;
import com.example.pmsbooking.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutomaticTaskCreatorTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private AutomaticTaskCreator automaticTaskCreator;

    private Booking booking;
    private Room room;
    private Hotel hotel;

    private final Long bookingId = 1L;

    private final LocalDate checkIn = LocalDate.of(2026, 12, 10);
    private final LocalDate checkOut = LocalDate.of(2026, 12, 15);

    @BeforeEach
    void setUp() {
        hotel = mock(Hotel.class);
        room = mock(Room.class);

        User user = mock(User.class);

        booking = new Booking(
                user,
                room,
                checkIn,
                checkOut,
                BookingStatus.CONFIRMED,
                LocalDateTime.now()
        );

        ReflectionTestUtils.setField(booking, "id", bookingId);

        lenient().when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        lenient().when(room.getHotel()).thenReturn(hotel);
        lenient().when(room.getRoomNumber()).thenReturn("101");
    }

    /**
     * Проверяет, что задача на подготовку номера не создается,
     * если статус бронирования не CONFIRMED.
     * Гарантирует, что метод возвращает false
     * и задача не сохраняется в репозитории.
     */
    @Test
    void shouldNotCreatePreparationTaskWhenBookingIsNotConfirmed() {
        booking = new Booking(
                mock(User.class),
                room,
                checkIn,
                checkOut,
                BookingStatus.PENDING,
                LocalDateTime.now()
        );

        ReflectionTestUtils.setField(booking, "id", bookingId);

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        boolean result =
                automaticTaskCreator.createBookingPreparationTask(bookingId);

        assertFalse(result);

        verifyNoInteractions(taskRepository);
    }

    /**
     * Проверяет, что задача на подготовку номера не создается,
     * если такая задача уже существует для бронирования.
     * Гарантирует, что метод возвращает false
     * и повторное сохранение задачи не выполняется.
     */
    @Test
    void shouldNotCreatePreparationTaskWhenTaskAlreadyExists() {
        when(taskRepository.existsByBookingIdAndTitle(
                eq(bookingId),
                eq("Подготовить номер к заселению")
        )).thenReturn(true);

        boolean result =
                automaticTaskCreator.createBookingPreparationTask(bookingId);

        assertFalse(result);

        verify(taskRepository).existsByBookingIdAndTitle(
                bookingId,
                "Подготовить номер к заселению"
        );

        verify(taskRepository, never()).saveAndFlush(any(Task.class));
    }

    /**
     * Проверяет успешное создание задачи на подготовку номера,
     * если бронирование имеет статус CONFIRMED
     * и аналогичная задача еще не существует.
     * Гарантирует, что задача сохраняется с корректными данными:
     * отелем, номером, бронированием, описанием и датой выполнения.
     */
    @Test
    void shouldCreatePreparationTaskWhenBookingIsConfirmedAndTaskDoesNotExist() {
        when(taskRepository.existsByBookingIdAndTitle(
                eq(bookingId),
                eq("Подготовить номер к заселению")
        )).thenReturn(false);

        boolean result =
                automaticTaskCreator.createBookingPreparationTask(bookingId);

        assertTrue(result);

        verify(taskRepository).existsByBookingIdAndTitle(
                bookingId,
                "Подготовить номер к заселению"
        );

        ArgumentCaptor<Task> taskCaptor =
                ArgumentCaptor.forClass(Task.class);

        verify(taskRepository).saveAndFlush(taskCaptor.capture());

        Task createdTask = taskCaptor.getValue();

        assertEquals(hotel, createdTask.getHotel());
        assertEquals(room, createdTask.getRoom());
        assertEquals(booking, createdTask.getBooking());
        assertNull(createdTask.getAssignedStaff());
        assertEquals("Подготовить номер к заселению", createdTask.getTitle());
        assertEquals("Проверить готовность номера №101 к заселению",
                createdTask.getDescription());
        assertEquals(checkIn, createdTask.getDueDate());
        assertEquals(TaskStatus.BACKLOG, createdTask.getStatus());
        assertNotNull(createdTask.getCreatedAt());
    }

    /**
     * Проверяет, что задача на уборку не создается,
     * если статус бронирования не CONFIRMED.
     * Гарантирует, что метод возвращает false
     * и задача не сохраняется в репозитории.
     */
    @Test
    void shouldNotCreateCleaningTaskWhenBookingIsNotConfirmed() {
        booking = new Booking(
                mock(User.class),
                room,
                checkIn,
                checkOut,
                BookingStatus.PENDING,
                LocalDateTime.now()
        );

        ReflectionTestUtils.setField(booking, "id", bookingId);

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        boolean result =
                automaticTaskCreator.createCheckoutCleaningTask(bookingId);

        assertFalse(result);

        verifyNoInteractions(taskRepository);
    }

    /**
     * Проверяет, что задача на уборку не создается,
     * если такая задача уже существует для бронирования.
     * Гарантирует, что метод возвращает false
     * и повторное сохранение задачи не выполняется.
     */
    @Test
    void shouldNotCreateCleaningTaskWhenTaskAlreadyExists() {
        when(taskRepository.existsByBookingIdAndTitle(
                eq(bookingId),
                eq("Уборка номера после выезда")
        )).thenReturn(true);

        boolean result =
                automaticTaskCreator.createCheckoutCleaningTask(bookingId);

        assertFalse(result);

        verify(taskRepository).existsByBookingIdAndTitle(
                bookingId,
                "Уборка номера после выезда"
        );

        verify(taskRepository, never()).saveAndFlush(any(Task.class));
    }

    /**
     * Проверяет успешное создание задачи на уборку,
     * если бронирование имеет статус CONFIRMED
     * и аналогичная задача еще не существует.
     * Гарантирует, что задача сохраняется с корректными данными:
     * отелем, номером, бронированием, описанием и датой выполнения.
     */
    @Test
    void shouldCreateCleaningTaskWhenBookingIsConfirmedAndTaskDoesNotExist() {
        when(taskRepository.existsByBookingIdAndTitle(
                eq(bookingId),
                eq("Уборка номера после выезда")
        )).thenReturn(false);

        boolean result =
                automaticTaskCreator.createCheckoutCleaningTask(bookingId);

        assertTrue(result);

        verify(taskRepository).existsByBookingIdAndTitle(
                bookingId,
                "Уборка номера после выезда"
        );

        ArgumentCaptor<Task> taskCaptor =
                ArgumentCaptor.forClass(Task.class);

        verify(taskRepository).saveAndFlush(taskCaptor.capture());

        Task createdTask = taskCaptor.getValue();

        assertEquals(hotel, createdTask.getHotel());
        assertEquals(room, createdTask.getRoom());
        assertEquals(booking, createdTask.getBooking());
        assertNull(createdTask.getAssignedStaff());
        assertEquals("Уборка номера после выезда", createdTask.getTitle());
        assertEquals("Провести уборку номера №101",
                createdTask.getDescription());
        assertEquals(checkOut, createdTask.getDueDate());
        assertEquals(TaskStatus.BACKLOG, createdTask.getStatus());
        assertNotNull(createdTask.getCreatedAt());
    }
}
