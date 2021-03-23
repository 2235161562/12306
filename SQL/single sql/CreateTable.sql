-- -----------------------------------------------------
-- Table Station
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Station
(
    station_id   CHAR(3)     NOT NULL,
    station_name VARCHAR(35) NOT NULL,
    city_name    VARCHAR(20) NOT NULL,

    PRIMARY KEY (station_id),

    UNIQUE (station_id),
    UNIQUE (station_name)
);


-- -----------------------------------------------------
-- Table train
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS train
(
    train_name        VARCHAR(7) NOT NULL,
    start_station     CHAR(3)    NOT NULL,
    end_station       CHAR(3)    NOT NULL,
    count_station     INT        NOT NULL,

    runtime           INT        NOT NULL,
    create_data       char(10)   NOT NULL,

    first_seat_count  INT,
    second_seat_count INT,
    third_seat_count  INT,

    PRIMARY KEY (train_name),

    UNIQUE (train_name),

    foreign key (start_station) REFERENCES station (station_id),
    foreign key (end_station) REFERENCES station (station_id)
);


-- -----------------------------------------------------
-- Table dock
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS dock
(
    train_name        VARCHAR(6) NOT NULL,
    dock_id           INT        NOT NULL,

    station_id        CHAR(3)    NOT NULL,
    arrive_time       VARCHAR(6),
    leave_time        VARCHAR(6),

    first_seat_price  INT,
    second_seat_price INT,
    third_seat_price  INT,

    PRIMARY KEY (train_name, dock_id),

    foreign key (train_name) REFERENCES train (train_name)
);


-- -----------------------------------------------------
-- Table people
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS people
(
    people_id     serial      NOT NULL,

    user_phone    VARCHAR(15) NOT NULL,
    ID_card_num   CHAR(18)    NOT NULL,
    user_name     VARCHAR(25) NOT NULL,

    administrator boolean     NOT NULL,

    PRIMARY KEY (people_id),

    UNIQUE (ID_card_num)
);


-- -----------------------------------------------------
-- Table Ticket
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Ticket
(
    ticket_id    serial         NOT NULL,
    seat_id       VARCHAR(4)  NOT NULL,
    user_id       INT         NOT NULL,

    train_name        VARCHAR(7) NOT NULL,
    from_dock     INT         NOT NULL,
    to_dock       INT         NOT NULL,

    creat_data    VARCHAR(15) NOT NULL,

    ticket_status CHAR(2)     NOT NULL,

    PRIMARY KEY (ticket_id),

    foreign key (train_name,from_dock) REFERENCES dock (train_name,dock_id),
    foreign key (train_name,to_dock) REFERENCES dock (train_name,dock_id),
    foreign key (user_id) REFERENCES people (people_id)
);