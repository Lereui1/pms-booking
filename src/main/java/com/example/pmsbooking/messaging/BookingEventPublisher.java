package com.example.pmsbooking.messaging;

import com.example.pmsbooking.event.BookingCreatedEvent;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Profile("!test")
@Component
public class BookingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public BookingEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookingCreated(BookingCreatedEvent event) {

        CorrelationData taskCorrelation =
                new CorrelationData("booking-task-" + event.bookingId());

        rabbitTemplate.convertAndSend(
                "booking.exchange",
                "booking.created.task",
                event,
                taskCorrelation
        );

        CorrelationData notificationCorrelation =
                new CorrelationData("booking-notification-" + event.bookingId());

        rabbitTemplate.convertAndSend(
                "booking.exchange",
                "booking.created.notification",
                event,
                notificationCorrelation
        );
    }
}
