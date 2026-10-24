ALTER TABLE users
    ADD COLUMN hotel_id BIGINT;

ALTER TABLE users
    ADD CONSTRAINT fk_users_hotel
        FOREIGN KEY (hotel_id)
            REFERENCES hotels(id);
