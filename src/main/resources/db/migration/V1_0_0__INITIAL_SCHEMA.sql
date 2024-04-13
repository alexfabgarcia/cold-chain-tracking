CREATE TABLE raw_event
(
    id         UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    content    VARCHAR     NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE sensor_type
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
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (name, category)
);

CREATE TABLE product_sensor_type
(
    id             UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    product_id     UUID        NOT NULL REFERENCES product,
    sensor_type_id UUID        NOT NULL REFERENCES sensor_type,
    minimum        DECIMAL     NOT NULL,
    maximum        DECIMAL     NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (product_id, sensor_type_id)
);

CREATE TABLE device
(
    id              UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    external_id     VARCHAR     NOT NULL,
    network_server  VARCHAR     NOT NULL,
    name            VARCHAR,
    payload_pattern VARCHAR     NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (external_id, network_server)
);

CREATE TABLE device_measurement
(
    id             UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    device_id      VARCHAR     NOT NULL,
    sensor_type_id UUID        NOT NULL REFERENCES sensor_type,
    measured_at    TIMESTAMPTZ NOT NULL,
    value          VARCHAR     NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (device_id, sensor_type_id, measured_at)
);

CREATE INDEX device_measurement_measured_at_index ON device_measurement (created_at);

CREATE TABLE carrier
(
    id         UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    first_name VARCHAR     NOT NULL,
    surname    VARCHAR     NOT NULL,
    phone      VARCHAR     NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (phone)
);

CREATE TABLE freight
(
    id          UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    device_id   UUID        NOT NULL REFERENCES device,
    product_id  UUID        NOT NULL REFERENCES product,
    description VARCHAR,
    origin      JSONB       NOT NULL,
    destination JSONB       NOT NULL,
    status      VARCHAR     NOT NULL,
    carrier_id  UUID REFERENCES carrier,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (device_id, product_id, created_at)
);
