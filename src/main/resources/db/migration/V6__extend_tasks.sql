ALTER TABLE tasks
    ADD COLUMN room_id BIGINT,
    ADD COLUMN assigned_staff_id BIGINT,
    ADD COLUMN due_date DATE;

ALTER TABLE tasks
    ADD CONSTRAINT fk_tasks_room
        FOREIGN KEY (room_id) REFERENCES rooms(id),
    ADD CONSTRAINT fk_tasks_assigned_staff
        FOREIGN KEY (assigned_staff_id) REFERENCES users(id);
