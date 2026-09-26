CREATE TABLE tasks (
                       id BIGSERIAL PRIMARY KEY,

                       hotel_id BIGINT NOT NULL,
                       booking_id BIGINT,

                       title VARCHAR(255) NOT NULL,
                       description TEXT,

                       status VARCHAR(20) NOT NULL,

                       created_at TIMESTAMP NOT NULL,

                       CONSTRAINT fk_tasks_hotel
                           FOREIGN KEY (hotel_id)
                               REFERENCES hotels(id),

                       CONSTRAINT fk_tasks_booking
                           FOREIGN KEY (booking_id)
                               REFERENCES bookings(id)
);
