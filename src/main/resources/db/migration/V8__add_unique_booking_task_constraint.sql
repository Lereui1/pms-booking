ALTER TABLE tasks
    ADD CONSTRAINT uk_tasks_booking_id_title
        UNIQUE (booking_id, title);
