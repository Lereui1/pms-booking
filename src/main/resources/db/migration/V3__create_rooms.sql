CREATE TABLE rooms (
                       id BIGSERIAL PRIMARY KEY,
                       hotel_id BIGINT NOT NULL,
                       room_number VARCHAR(20) NOT NULL,
                       type VARCHAR(20) NOT NULL,
                       price_per_night NUMERIC(10, 2) NOT NULL,
                       version BIGINT NOT NULL DEFAULT 0,

                       CONSTRAINT fk_rooms_hotel
                           FOREIGN KEY (hotel_id)
                               REFERENCES hotels(id)
);
