CREATE TABLE bookings (
                          id BIGSERIAL PRIMARY KEY,

                          user_id BIGINT NOT NULL,
                          room_id BIGINT NOT NULL,

                          check_in DATE NOT NULL,
                          check_out DATE NOT NULL,

                          status VARCHAR(20) NOT NULL,

                          created_at TIMESTAMP NOT NULL,

                          CONSTRAINT fk_bookings_user
                              FOREIGN KEY (user_id)
                                  REFERENCES users(id),

                          CONSTRAINT fk_bookings_room
                              FOREIGN KEY (room_id)
                                  REFERENCES rooms(id)
);
