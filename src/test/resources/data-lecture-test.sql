INSERT INTO user (first_Name, last_Name, email, password)
VALUES ('Clint', 'Barton', 'hawkeye@marvel.com', '{noop}zaq1@WSX');

INSERT INTO user (first_Name, last_Name, email, password)
VALUES ('Steven', 'Rogers', 'capitan.ameryka@marvel.com', '{noop}zaq1@WSX');

INSERT INTO lecture(id, title, description, lecturer, completed)
VALUES (1, 'Java 8', 'The basics of language', 'Tony Stark', true);

INSERT INTO lecture(id, title, description, lecturer, completed)
VALUES (2, 'Spring', 'The basics of framework', 'Bruce Banner', false);

INSERT INTO user_lecture(user_id, lecture_id)
VALUES ((SELECT id FROM user WHERE email='hawkeye@marvel.com'), 1);

INSERT INTO user_lecture(user_id, lecture_id)
VALUES ((SELECT id FROM user WHERE email='capitan.ameryka@marvel.com'), 1);

