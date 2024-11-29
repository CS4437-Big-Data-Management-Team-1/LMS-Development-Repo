CREATE TABLE teams (
    team_id INT PRIMARY KEY,
    team_name VARCHAR(255),
    abbreviation VARCHAR(10)
);

CREATE TABLE matches (
    match_id INT PRIMARY KEY,
    home_team_id INT,
    home_team_name VARCHAR(255),
    away_team_id INT,
    away_team_name VARCHAR(255),
    game_date TIMESTAMP,
    result VARCHAR(255)
);
