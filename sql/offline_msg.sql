CREATE TABLE offline_msg (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             message_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             content BLOB,
                             message_type INT,
                             status TINYINT,  -- 0: Pending, 1: Acked
                             created_at DATETIME,
                             properties JSON,  -- 使用 JSON 格式存储 Map
                             INDEX idx_user_id (user_id)
);