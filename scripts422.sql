-- Таблица Person
CREATE TABLE person (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        age SMALLINT NOT NULL CHECK (age >= 0),
                        has_driver_license BOOLEAN NOT NULL
);

-- Таблица Car
CREATE TABLE car (
                     id SERIAL PRIMARY KEY,
                     make VARCHAR(50) NOT NULL,
                     model VARCHAR(50) NOT NULL,
                     price NUMERIC(10, 2) NOT NULL CHECK (price >= 0)
);

-- Связующая таблица person_car
CREATE TABLE person_car (
                            person_id INT NOT NULL,
                            car_id INT NOT NULL,
                            PRIMARY KEY (person_id, car_id),

    -- Внешний ключ на person
                            CONSTRAINT fk_person_car_person
                                FOREIGN KEY (person_id)
                                    REFERENCES person (id)
                                    ON DELETE CASCADE,

    -- Внешний ключ на car
                            CONSTRAINT fk_person_car_car
                                FOREIGN KEY (car_id)
                                    REFERENCES car (id)
                                    ON DELETE RESTRICT
);
