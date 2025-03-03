INSERT INTO users (id, email, password, first_name, last_name)
VALUES (3, 'testUser1@testmail.com', '$2a$10$jQuBxj/mnQTEY60FgHeYiu8SahQYuj0O8shQtDMIAOAC1kYH3/2/6', 'Test1', 'Name1');
INSERT INTO users_role_types (user_id, role_type_id)
VALUES (3, 1);
INSERT INTO users_role_types (user_id, role_type_id)
VALUES (3, 2);

INSERT INTO users (id, email, password, first_name, last_name)
VALUES (4, 'testUser2@testmail.com', '$2a$10$jQuBxj/mnQTEY60FgHeYiu8SahQYuj0O8shQtDMIAOAC1kYH3/2/6', 'Test2', 'Name2');
INSERT INTO users_role_types (user_id, role_type_id)
VALUES (4, 1);