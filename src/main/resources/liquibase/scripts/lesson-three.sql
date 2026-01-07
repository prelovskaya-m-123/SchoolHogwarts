-- liquibase formatted sql

-- changeset mprelovskaya:1

-- Индекс для поиска по имени студента
CREATE INDEX student_name_index
    ON student (name);

-- Составной индекс для поиска по названию и цвету факультета
CREATE INDEX faculty_name_color_index
    ON faculty (name, color);
