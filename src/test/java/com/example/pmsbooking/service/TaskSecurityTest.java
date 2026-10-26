package com.example.pmsbooking.service;

import com.example.pmsbooking.dto.task.TaskRequest;
import com.example.pmsbooking.entity.Hotel;
import com.example.pmsbooking.entity.Task;
import com.example.pmsbooking.entity.TaskStatus;
import com.example.pmsbooking.entity.User;
import com.example.pmsbooking.exception.AccessDeniedException;
import com.example.pmsbooking.repository.HotelRepository;
import com.example.pmsbooking.repository.TaskRepository;
import com.example.pmsbooking.repository.UserRepository;
import com.example.pmsbooking.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TaskSecurityTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    private Hotel hotelA;
    private Hotel hotelB;
    private User staffA;
    private Task taskB;

    /**
     * Готовит в базе тестовые отели, сотрудника и изолированную задачу.
     * Искусственно авторизует сотрудника Отеля А для проверки правил разграничения доступа.
     */
    @BeforeEach
    void setUp() {
        hotelA = hotelRepository.save(
                new Hotel(
                        "Security Test Hotel A",
                        "Test Address A",
                        "Hotel A for RBAC tests"
                )
        );

        hotelB = hotelRepository.save(
                new Hotel(
                        "Security Test Hotel B",
                        "Test Address B",
                        "Hotel B for RBAC tests"
                )
        );

        staffA = new User(
                "security-staff-a",
                "security-staff-a@example.com",
                "test-password",
                "STAFF"
        );
        staffA.setHotel(hotelA);
        staffA = userRepository.save(staffA);

        taskB = taskRepository.save(
                new Task(
                        hotelB,
                        null,
                        null,
                        null,
                        "Hotel B private task",
                        "This task must not be accessible to Hotel A",
                        null
                )
        );

        CustomUserDetails userDetails = new CustomUserDetails(staffA);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                )
        );
    }

    /**
     * Очищает контекст безопасности Spring Security после каждого теста, чтобы предотвратить
     * утечку данных авторизации в другие тесты.
     */
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Проверяет, что сотруднику запрещен доступ при попытке прочитать задачу, принадлежащую другому отелю.
     */
    @Test
    void shouldDenyReadingTaskFromAnotherHotel() {
        assertThrows(
                AccessDeniedException.class,
                () -> taskService.getTaskById(taskB.getId())
        );
    }

    /**
     * Проверяет, что сотруднику запрещено создавать задачу для другого отеля.
     */
    @Test
    void shouldDenyCreatingTaskForAnotherHotel() {
        TaskRequest request = new TaskRequest();
        request.setHotelId(hotelB.getId());
        request.setTitle("Unauthorized task");

        assertThrows(
                AccessDeniedException.class,
                () -> taskService.createTask(request)
        );
    }

    /**
     * Проверяет, что на задачу невозможно назначить сотрудника из другого отеля.
     */
    @Test
    void shouldDenyAssigningStaffFromAnotherHotel() {
        User staffB = new User(
                "security-staff-b",
                "security-staff-b@example.com",
                "test-password",
                "STAFF"
        );
        staffB.setHotel(hotelB);
        staffB = userRepository.save(staffB);

        TaskRequest request = new TaskRequest();
        request.setHotelId(hotelA.getId());
        request.setAssignedStaffId(staffB.getId());
        request.setTitle("Task with foreign staff");

        assertThrows(
                AccessDeniedException.class,
                () -> taskService.createTask(request)
        );
    }

    /**
     * Проверяет, что сотруднику запрещено изменять статус задачи, принадлежащей другому отелю.
     */
    @Test
    void shouldDenyChangingStatusOfTaskFromAnotherHotel() {
        assertThrows(
                AccessDeniedException.class,
                () -> taskService.updateTaskStatus(
                        taskB.getId(),
                        TaskStatus.IN_PROGRESS
                )
        );
    }

    /**
     * Проверяет, что сотруднику запрещено удалять задачу, принадлежащую другому отелю.
     */
    @Test
    void shouldDenyDeletingTaskFromAnotherHotel() {
        assertThrows(
                AccessDeniedException.class,
                () -> taskService.deleteTask(taskB.getId())
        );

        assertTrue(taskRepository.existsById(taskB.getId()));
    }

    /**
     * Проверяет, что сотруднику успешно разрешен доступ на чтение задачи, принадлежащей его собственному отелю.
     */
    @Test
    void shouldAllowReadingTaskFromOwnHotel() {
        Task ownTask = taskRepository.save(
                new Task(
                        hotelA,
                        null,
                        null,
                        null,
                        "Hotel A private task",
                        "This task belongs to Hotel A",
                        null
                )
        );

        assertEquals(
                ownTask.getId(),
                taskService.getTaskById(ownTask.getId()).getId()
        );
    }
}
