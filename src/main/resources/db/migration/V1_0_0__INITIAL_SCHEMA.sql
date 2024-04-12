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

CREATE TABLE product
(
    id         UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    name       VARCHAR     NOT NULL,
    category   VARCHAR     NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (name, category)
);

CREATE TABLE product_measurement_type
(
    id                  UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    product_id          UUID        NOT NULL,
    measurement_type_id UUID        NOT NULL,
    minimum             DECIMAL     NOT NULL,
    maximum             DECIMAL     NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (product_id, measurement_type_id)
);

ALTER TABLE product_measurement_type
    ADD FOREIGN KEY (product_id) REFERENCES product (id);
ALTER TABLE product_measurement_type
    ADD FOREIGN KEY (measurement_type_id) REFERENCES measurement_type (id);

CREATE TABLE device
(
    id              UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    external_id     VARCHAR     NOT NULL,
    version         BIGINT      NOT NULL,
    network_server  VARCHAR     NOT NULL,
    name            VARCHAR,
    payload_pattern VARCHAR     NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (external_id, network_server)
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

CREATE TABLE freight
(
    id          UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    device_id   UUID        NOT NULL,
    product_id  UUID        NOT NULL,
    description VARCHAR,
    origin      JSONB       NOT NULL,
    destination JSONB       NOT NULL,
    status      VARCHAR     NOT NULL,
    carrier_id  UUID,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (device_id, product_id, created_at)
);

ALTER TABLE freight
    ADD FOREIGN KEY (device_id) REFERENCES device (id);
ALTER TABLE freight
    ADD FOREIGN KEY (product_id) REFERENCES product (id);
ALTER TABLE freight
    ADD FOREIGN KEY (carrier_id) REFERENCES carrier (id);

---
INSERT INTO measurement_type (name, unit)
VALUES ('Temperature', 'DEGREE_CELSIUS'),
       ('Humidity', 'RELATIVE_HUMIDITY'),
       ('Location', 'LATITUDE_LONGITUDE');

INSERT INTO carrier (first_name, surname, phone)
VALUES ('Alex Fabiano', 'Garcia', '+31649923823'),
       ('Wanderley Lopes', 'de Souza', '+5516999999999');

INSERT INTO product (name, category)
VALUES ('Pfizer–BioNTech COVID-19', 'Vaccine'),
       ('Oxford–AstraZeneca COVID-19', 'Vaccine'),
       ('CoronaVac COVID-19', 'Vaccine');

INSERT INTO product_measurement_type (product_id, measurement_type_id, minimum, maximum)
VALUES ((SELECT id FROM product WHERE name = 'CoronaVac COVID-19'),
        (SELECT id FROM measurement_type WHERE name = 'Temperature'), 2, 8);
