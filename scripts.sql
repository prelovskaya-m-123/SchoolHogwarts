SELECT * FROM student WHERE age BETWEEN 10 AND 18;

SELECT name FROM student;

SELECT * FROM student WHERE name LIKE '%О%' OR name LIKE '%о%';

SELECT * FROM student WHERE age < id;

SELECT * FROM student ORDER BY age ASC;

-- Количество всех студентов
SELECT COUNT(*) FROM student;

-- Средний возраст студентов
SELECT AVG(age) FROM student;

-- Последние 5 студентов (по id)
SELECT * FROM student ORDER BY id DESC LIMIT 5;
