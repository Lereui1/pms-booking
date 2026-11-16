package com.example.pmsbooking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange bookingExchange() {
        return new DirectExchange("booking.exchange", true, false);
    }

    @Bean
    public Queue bookingTaskQueue() {
        return QueueBuilder.durable("booking.task.queue")
                .deadLetterExchange("booking.dlx")
                .deadLetterRoutingKey("booking.task.dlq")
                .build();
    }

    @Bean
    public Queue bookingNotificationQueue() {
        return QueueBuilder.durable("booking.notification.queue")
                .deadLetterExchange("booking.dlx")
                .deadLetterRoutingKey("booking.notification.dlq")
                .build();
    }

    @Bean
    public Binding bookingTaskBinding() {
        return BindingBuilder
                .bind(bookingTaskQueue())
                .to(bookingExchange())
                .with("booking.created.task");
    }

    @Bean
    public Binding bookingNotificationBinding() {
        return BindingBuilder
                .bind(bookingNotificationQueue())
                .to(bookingExchange())
                .with("booking.created.notification");
    }

    @Bean
    public MessageConverter jacksonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public DirectExchange bookingDeadLetterExchange() {
        return new DirectExchange("booking.dlx", true, false);
    }

    @Bean
    public Queue bookingTaskDeadLetterQueue() {
        return new Queue("booking.task.dlq", true);
    }

    @Bean
    public Queue bookingNotificationDeadLetterQueue() {
        return new Queue("booking.notification.dlq", true);
    }

    @Bean
    public Binding bookingTaskDeadLetterBinding() {
        return BindingBuilder
                .bind(bookingTaskDeadLetterQueue())
                .to(bookingDeadLetterExchange())
                .with("booking.task.dlq");
    }

    @Bean
    public Binding bookingNotificationDeadLetterBinding() {
        return BindingBuilder
                .bind(bookingNotificationDeadLetterQueue())
                .to(bookingDeadLetterExchange())
                .with("booking.notification.dlq");
    }
}
