-- SCHEMA - CODEWORDLE (corregido)
DROP TABLE IF EXISTS attempt;
DROP TABLE IF EXISTS game;
DROP TABLE IF EXISTS words;

CREATE TABLE words (
    id_word INT AUTO_INCREMENT PRIMARY KEY,
    topic VARCHAR(50) NOT NULL,
    word VARCHAR(50) NOT NULL
);

CREATE TABLE game (
    id_game INT AUTO_INCREMENT PRIMARY KEY,
    topic VARCHAR(50) NOT NULL,
    state VARCHAR(20) DEFAULT 'IN_PROGRESS',
    hidden_word VARCHAR(50) NOT NULL,
    attempts INT DEFAULT 0
);

CREATE TABLE attempt (
    id_attempt INT AUTO_INCREMENT PRIMARY KEY,
    id_game INT NOT NULL,
    attempt VARCHAR(50) NOT NULL,
    result VARCHAR(200),
    FOREIGN KEY (id_game) REFERENCES game(id_game)
);
