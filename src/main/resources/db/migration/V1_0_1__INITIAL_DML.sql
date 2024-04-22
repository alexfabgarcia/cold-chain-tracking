-- Passwords created via Spring CLI: spring encodepassword PASSWORD_VALUE
-- See https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html#authentication-password-storage-boot-cli
-- admin: adminpassword
-- alexgarcia: alexgarciapassword
INSERT INTO users (username, password)
VALUES ('admin', '{bcrypt}$2a$10$NqBD83TZJoC0xgkUloGdZegGNNOeb.JdFKuyng4ZVQRPdVUs0xgD6'),
       ('alexgarcia', '{bcrypt}$2a$10$TulpkgTTDt7yU1CxJWojtelssa92UcawTyryGjhxN6qoo5xoaqm2O');

INSERT INTO authorities (username, authority)
VALUES ('admin', 'ROLE_ADMIN'),
       ('alexgarcia', 'ROLE_CARRIER');

INSERT INTO carrier (first_name, surname, phone, user_id)
VALUES ('Alex', 'Garcia', '+5511999999999', 'alexgarcia');

INSERT INTO measurement_type (name, unit)
VALUES ('Temperature', 'DEGREE_CELSIUS'),
       ('Humidity', 'RELATIVE_HUMIDITY'),
       ('Location', 'LATITUDE_LONGITUDE');

INSERT INTO product (name, category)
VALUES ('Pfizer–BioNTech COVID-19', 'VACCINE'),
       ('Oxford–AstraZeneca COVID-19', 'VACCINE'),
       ('CoronaVac COVID-19', 'VACCINE');

INSERT INTO product_measurement_type (product_id, measurement_type_id, minimum, maximum)
VALUES ((SELECT id FROM product WHERE name = 'CoronaVac COVID-19'),
        (SELECT id FROM measurement_type WHERE name = 'Temperature'), 2, 8);
