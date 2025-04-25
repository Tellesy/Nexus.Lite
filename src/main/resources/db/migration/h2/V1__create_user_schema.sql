-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- User roles mapping
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- System preferences table
CREATE TABLE IF NOT EXISTS system_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    preference_key VARCHAR(100) NOT NULL UNIQUE,
    preference_value TEXT,
    description VARCHAR(255)
);

-- Default roles
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (name) VALUES ('ROLE_USER');

-- Default admin user (username: admin, password: admin - BCrypt encoded)
INSERT INTO users (username, password, email, enabled)
VALUES ('admin', '$2a$10$ZCbwIRjhpL2UrjiyU09BuOWiLKUbAJVTtwvbIi/Xtfwu9T9E7yR1S', 'admin@example.com', TRUE);

-- Assign admin role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';

-- Default system preferences
INSERT INTO system_preferences (preference_key, preference_value, description)
VALUES ('db.type', 'h2', 'Database type (h2 or mysql)');

INSERT INTO system_preferences (preference_key, preference_value, description)
VALUES ('theme.mode', 'light', 'UI theme mode (light or dark)');

INSERT INTO system_preferences (preference_key, preference_value, description)
VALUES ('theme.primary-color', '#1976d2', 'Primary theme color');

INSERT INTO system_preferences (preference_key, preference_value, description)
VALUES ('theme.secondary-color', '#FFC107', 'Secondary theme color');
