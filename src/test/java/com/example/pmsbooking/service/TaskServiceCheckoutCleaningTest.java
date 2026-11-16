package com.example.pmsbooking.service;

import com.example.pmsbooking.repository.*;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceCheckoutCleaningTest {

    private TaskRepository taskRepository;
    private HotelRepository hotelRepository;
    private RoomRepository roomRepository;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private AutomaticTaskCreator automaticTaskCreator;
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
        automaticTaskCreator = mock(AutomaticTaskCreator.class);

        taskService = new TaskService(
                taskRepository,
                hotelRepository,
                roomRepository,
                bookingRepository,
                userRepository,
                automaticTaskCreator
        );
    }

    /**
     * Проверяет, что TaskService возвращает false,
     * если AutomaticTaskCreator не создал автоматическую задачу.
     */
    @Test
    void shouldReturnFalseWhenAutomaticTaskCreatorReturnsFalse() {
        when(automaticTaskCreator.createCheckoutCleaningTask(1L))
                .thenReturn(false);

        boolean result = taskService.createCheckoutCleaningTask(1L);

        assertFalse(result);

        verify(automaticTaskCreator).createCheckoutCleaningTask(1L);
        verifyNoInteractions(taskRepository);
    }

    /**
     * Проверяет, что TaskService возвращает true,
     * если AutomaticTaskCreator успешно создал задачу на уборку.
     */
    @Test
    void shouldCreateCleaningTaskWhenBookingIsConfirmedAndTaskDoesNotExist() {
        when(automaticTaskCreator.createCheckoutCleaningTask(1L))
                .thenReturn(true);

        boolean result = taskService.createCheckoutCleaningTask(1L);

        assertTrue(result);

        verify(automaticTaskCreator).createCheckoutCleaningTask(1L);
        verifyNoInteractions(taskRepository);
    }

    /**
     * Проверяет, что при нарушении уникального ограничения
     * uk_tasks_booking_id_title метод возвращает false,
     * поскольку автоматическая задача уже существует.
     */
    @Test
    void shouldReturnFalseWhenDuplicateAutomaticTaskConstraintIsViolated() {
        SQLException sqlException = new SQLException("Duplicate key", "23505");

        ConstraintViolationException constraintException =
                new ConstraintViolationException(
                        "Duplicate automatic task",
                        sqlException,
                        "insert into tasks",
                        "uk_tasks_booking_id_title"
                );

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "Could not save automatic task",
                        constraintException
                );

        when(automaticTaskCreator.createCheckoutCleaningTask(1L))
                .thenThrow(exception);

        boolean result = taskService.createCheckoutCleaningTask(1L);

        assertFalse(result);

        verify(automaticTaskCreator).createCheckoutCleaningTask(1L);
        verifyNoInteractions(taskRepository);
    }

    /**
     * Проверяет, что DataIntegrityViolationException
     * пробрасывается дальше, если нарушено ограничение,
     * отличное от uk_tasks_booking_id_title.
     */
    @Test
    void shouldRethrowDataIntegrityViolationWhenConstraintIsDifferent() {
        SQLException sqlException = new SQLException("Duplicate key", "23505");

        ConstraintViolationException constraintException =
                new ConstraintViolationException(
                        "Another constraint violation",
                        sqlException,
                        "insert into tasks",
                        "uk_some_other_constraint"
                );

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "Could not save task",
                        constraintException
                );

        when(automaticTaskCreator.createCheckoutCleaningTask(1L))
                .thenThrow(exception);

        DataIntegrityViolationException thrownException =
                assertThrows(
                        DataIntegrityViolationException.class,
                        () -> taskService.createCheckoutCleaningTask(1L)
                );

        assertSame(exception, thrownException);

        verify(automaticTaskCreator).createCheckoutCleaningTask(1L);
    }
}
