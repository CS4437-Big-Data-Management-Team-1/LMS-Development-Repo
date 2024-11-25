CREATE TABLE users (
    user_id VARCHAR(255) NOT NULL PRIMARY KEY,
    username VARCHAR(255),
    email VARCHAR(255),
    password_hash VARCHAR(255),
    is_admin BOOLEAN NOT NULL,
    balance FLOAT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    last_login TIMESTAMP
);
