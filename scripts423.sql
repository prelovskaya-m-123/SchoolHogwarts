-- 1. Получение информации обо всех студентах с названиями факультетов
SELECT
    s.name AS student_name,
    s.age AS student_age,
    f.name AS faculty_name
FROM student s
         JOIN faculty f ON s.faculty_id = f.id
ORDER BY s.id;

-- 2. Получение только тех студентов, у которых есть аватарки
SELECT
    s.name AS student_name,
    s.age AS student_age
FROM student s
         INNER JOIN avatar a ON a.student_id = s.id
ORDER BY s.id;

