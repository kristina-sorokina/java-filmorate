CREATE TABLE IF NOT EXISTS users (
    id int AUTO_INCREMENT PRIMARY KEY,
    email varchar(100) UNIQUE,
    login varchar(100) UNIQUE,
    name varchar(100),
    birthday date
);

CREATE TABLE IF NOT EXISTS friendship (
    active_user_id int,
    passive_user_id int,
    is_accepted boolean DEFAULT FALSE,
    PRIMARY KEY (active_user_id, passive_user_id),
    FOREIGN KEY (active_user_id) REFERENCES users(id),
    FOREIGN KEY (passive_user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS genres (
    id int AUTO_INCREMENT PRIMARY KEY,
    name varchar(50)
);

CREATE TABLE IF NOT EXISTS mpa (
    id int AUTO_INCREMENT PRIMARY KEY,
    name varchar(50)
);

CREATE TABLE IF NOT EXISTS films (
    id int AUTO_INCREMENT PRIMARY KEY,
    name varchar(100),
    description text,
    release_date date,
    duration int,
    mpa_id int,
    rate int,
    FOREIGN KEY (mpa_id) REFERENCES mpa(id)
);

CREATE TABLE IF NOT EXISTS likes (
    user_id int,
    film_id int,
    PRIMARY KEY (user_id, film_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (film_id) REFERENCES films(id)
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id int,
    genre_id int,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(id),
    FOREIGN KEY (genre_id) REFERENCES genres(id)
);