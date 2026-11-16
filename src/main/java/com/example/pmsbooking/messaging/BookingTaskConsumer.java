package com.example.pmsbooking.messaging;

import com.example.pmsbooking.event.BookingCreatedEvent;
import com.example.pmsbooking.service.TaskService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
public class BookingTaskConsumer {

    private final TaskService taskService;

    public BookingTaskConsumer(TaskService taskService) {
        this.taskService = taskService;
    }

    @RabbitListener(
            queues = "booking.task.queue",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleBookingCreated(BookingCreatedEvent event) {
        taskService.createBookingPreparationTask(event.bookingId());
    }
}
