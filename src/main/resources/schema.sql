-- Drop tables if they exist
DROP TABLE IF EXISTS attempts;
DROP TABLE IF EXISTS games;
DROP TABLE IF EXISTS words;
DROP TABLE IF EXISTS themes;

-- Create themes table
CREATE TABLE themes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create words table
CREATE TABLE words (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(20) NOT NULL,
    theme_id BIGINT NOT NULL,
    hint VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (theme_id) REFERENCES themes(id),
    UNIQUE(word, theme_id)
);

-- Create games table
CREATE TABLE games (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    theme_id BIGINT NOT NULL,
    word_id BIGINT NOT NULL,
    target_word VARCHAR(20) NOT NULL,
    max_attempts INT DEFAULT 6,
    current_attempts INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'IN_PROGRESS',
    won BOOLEAN DEFAULT FALSE,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP,
    FOREIGN KEY (theme_id) REFERENCES themes(id),
    FOREIGN KEY (word_id) REFERENCES words(id)
);

-- Create attempts table
CREATE TABLE attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_id BIGINT NOT NULL,
    guessed_word VARCHAR(20) NOT NULL,
    feedback VARCHAR(100),
    attempt_number INT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (game_id) REFERENCES games(id)
);
