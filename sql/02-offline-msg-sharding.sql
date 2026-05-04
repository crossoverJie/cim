CREATE TABLE offline_msg_0
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id      BIGINT       NOT NULL,
    receive_user_id BIGINT       NOT NULL,
    content         VARCHAR(2000),
    message_type    INT,
    status          TINYINT,
    created_at      DATETIME,
    properties      VARCHAR(2000),
    INDEX idx_receive_user_id (receive_user_id),
    INDEX idx_message_id (message_id),
    INDEX idx_created_at (created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE offline_msg_1
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id      BIGINT       NOT NULL,
    receive_user_id BIGINT       NOT NULL,
    content         VARCHAR(2000),
    message_type    INT,
    status          TINYINT,
    created_at      DATETIME,
    properties      VARCHAR(2000),
    INDEX idx_receive_user_id (receive_user_id),
    INDEX idx_message_id (message_id),
    INDEX idx_created_at (created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE offline_msg_2
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id      BIGINT       NOT NULL,
    receive_user_id BIGINT       NOT NULL,
    content         VARCHAR(2000),
    message_type    INT,
    status          TINYINT,
    created_at      DATETIME,
    properties      VARCHAR(2000),
    INDEX idx_receive_user_id (receive_user_id),
    INDEX idx_message_id (message_id),
    INDEX idx_created_at (created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE offline_msg_3
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id      BIGINT       NOT NULL,
    receive_user_id BIGINT       NOT NULL,
    content         VARCHAR(2000),
    message_type    INT,
    status          TINYINT,
    created_at      DATETIME,
    properties      VARCHAR(2000),
    INDEX idx_receive_user_id (receive_user_id),
    INDEX idx_message_id (message_id),
    INDEX idx_created_at (created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE offline_msg_4
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id      BIGINT       NOT NULL,
    receive_user_id BIGINT       NOT NULL,
    content         VARCHAR(2000),
    message_type    INT,
    status          TINYINT,
    created_at      DATETIME,
    properties      VARCHAR(2000),
    INDEX idx_receive_user_id (receive_user_id),
    INDEX idx_message_id (message_id),
    INDEX idx_created_at (created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE offline_msg_5
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id      BIGINT       NOT NULL,
    receive_user_id BIGINT       NOT NULL,
    content         VARCHAR(2000),
    message_type    INT,
    status          TINYINT,
    created_at      DATETIME,
    properties      VARCHAR(2000),
    INDEX idx_receive_user_id (receive_user_id),
    INDEX idx_message_id (message_id),
    INDEX idx_created_at (created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE offline_msg_6
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id      BIGINT       NOT NULL,
    receive_user_id BIGINT       NOT NULL,
    content         VARCHAR(2000),
    message_type    INT,
    status          TINYINT,
    created_at      DATETIME,
    properties      VARCHAR(2000),
    INDEX idx_receive_user_id (receive_user_id),
    INDEX idx_message_id (message_id),
    INDEX idx_created_at (created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE offline_msg_7
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id      BIGINT       NOT NULL,
    receive_user_id BIGINT       NOT NULL,
    content         VARCHAR(2000),
    message_type    INT,
    status          TINYINT,
    created_at      DATETIME,
    properties      VARCHAR(2000),
    INDEX idx_receive_user_id (receive_user_id),
    INDEX idx_message_id (message_id),
    INDEX idx_created_at (created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
