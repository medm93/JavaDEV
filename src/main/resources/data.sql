INSERT INTO user (email, password, index_number)
VALUES ('admin@javadev.pl', '{noop}admin', 'admin');

INSERT INTO role (role)
VALUES ('ROLE_ADMIN');

INSERT INTO role (role)
VALUES ('ROLE_USER');

INSERT INTO user_role (user_id, role_id)
VALUES (1, 1);