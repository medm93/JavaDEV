INSERT INTO user (email, password, index_number)
VALUES ('admin@javadev.pl', '{noop}admin', 'admin');

INSERT INTO role (role)
VALUES ('ROLE_ADMIN');

INSERT INTO role (role)
VALUES ('ROLE_USER');


INSERT INTO user_role (user_role.user_id, user_role.role_id)
VALUE ((SELECT u.id FROM user u WHERE u.email = 'admin@javadev.pl'),
       (SELECT r.id FROM role r WHERE r.role = 'ROLE_ADMIN'));