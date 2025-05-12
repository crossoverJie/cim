CREATE TABLE offline_msg_last_send_record
(
    user_id         BIGINT NOT NULL PRIMARY KEY,
    last_message_id VARCHAR(255),
    updated_at      DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;