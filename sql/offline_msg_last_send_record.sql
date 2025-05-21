CREATE TABLE offline_msg_last_send_record
(
    receive_user_id         BIGINT NOT NULL PRIMARY KEY,
    last_message_id BIGINT,
    updated_at      DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;