CREATE TABLE offline_msg (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             message_id BIGINT NOT NULL,
                             receive_user_id BIGINT NOT NULL,
                             content VARCHAR(2000),
                             message_type INT,
                             status TINYINT,  -- 0: Pending, 1: Acked
                             created_at DATETIME,
                             properties VARCHAR(2000),  -- 使用 JSON 格式存储 Map
                             INDEX idx_receive_user_id (receive_user_id)
);