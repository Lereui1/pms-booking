package com.example.pmsbooking.messaging;

import com.example.pmsbooking.event.BookingCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
public class BookingNotificationConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(BookingNotificationConsumer.class);

    @RabbitListener(
            queues = "booking.notification.queue",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleBookingCreated(BookingCreatedEvent event) {

        log.info(
                "New booking notification: bookingId={}, userId={}, hotelId={}, roomId={}, checkIn={}, checkOut={}",
                event.bookingId(),
                event.userId(),
                event.hotelId(),
                event.roomId(),
                event.checkIn(),
                event.checkOut()
        );
    }
}
