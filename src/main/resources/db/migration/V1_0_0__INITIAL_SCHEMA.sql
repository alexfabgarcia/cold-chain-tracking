CREATE TABLE raw_event
(
    id         UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    content    VARCHAR     NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE measurement_type
(
    id         UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    name       VARCHAR     NOT NULL,
    unit       VARCHAR     NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (name)
);

CREATE TABLE device_measurement
(
    id                  UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    device_id           VARCHAR     NOT NULL,
    measurement_type_id UUID        NOT NULL,
    measured_at         TIMESTAMPTZ NOT NULL,
    value               VARCHAR     NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (device_id, measurement_type_id, measured_at)
);

ALTER TABLE device_measurement
    ADD FOREIGN KEY (measurement_type_id) REFERENCES measurement_type (id);

CREATE INDEX device_measurement_measured_at_index ON device_measurement (created_at);

CREATE TABLE carrier
(
    id         UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    first_name VARCHAR     NOT NULL,
    surname    VARCHAR     NOT NULL,
    phone      VARCHAR     NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (phone)
);

INSERT INTO measurement_type (name, unit)
VALUES ('Temperature', 'DEGREE_CELSIUS'),
       ('Humidity', 'RELATIVE_HUMIDITY'),
       ('Location', 'LATITUDE_LONGITUDE');

INSERT INTO carrier (first_name, surname, phone)
VALUES ('Alex Fabiano', 'Garcia', '+31649923823'),
       ('Wanderley Lopes', 'de Souza', '+5516999999999');
