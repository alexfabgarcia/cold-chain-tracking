INSERT INTO sensor_type (name, unit)
VALUES ('Temperature', 'DEGREE_CELSIUS'),
       ('Humidity', 'RELATIVE_HUMIDITY'),
       ('Location', 'LATITUDE_LONGITUDE');

INSERT INTO carrier (first_name, surname, phone)
VALUES ('Alex', 'Garcia', '+5511999999999');

INSERT INTO product (name, category)
VALUES ('Pfizer–BioNTech COVID-19', 'VACCINE'),
       ('Oxford–AstraZeneca COVID-19', 'VACCINE'),
       ('CoronaVac COVID-19', 'VACCINE');

INSERT INTO product_sensor_type (product_id, sensor_type_id, minimum, maximum)
VALUES ((SELECT id FROM product WHERE name = 'CoronaVac COVID-19'),
        (SELECT id FROM sensor_type WHERE name = 'Temperature'), 2, 8);
