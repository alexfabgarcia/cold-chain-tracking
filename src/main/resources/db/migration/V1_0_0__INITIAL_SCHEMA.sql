CREATE TABLE raw_event
(
    id         UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    content    VARCHAR     NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE users
(
    username VARCHAR PRIMARY KEY NOT NULL,
    password VARCHAR             NOT NULL,
    enabled  BOOLEAN             NOT NULL DEFAULT true
);

CREATE TABLE authorities
(
    username  VARCHAR NOT NULL REFERENCES users,
    authority VARCHAR NOT NULL,
    UNIQUE (username, authority)
);

CREATE TABLE measurement_type
(
    id         UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    name       VARCHAR     NOT NULL,
    unit       VARCHAR     NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
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

CREATE TABLE product_measurement_type
(
    id                  UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    product_id          UUID        NOT NULL REFERENCES product,
    measurement_type_id UUID        NOT NULL REFERENCES measurement_type,
    minimum             DECIMAL     NOT NULL,
    maximum             DECIMAL     NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (product_id, measurement_type_id)
);

CREATE TABLE device
(
    id              UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    external_id     VARCHAR     NOT NULL,
    network_server  VARCHAR     NOT NULL,
    eui             VARCHAR,
    name            VARCHAR,
    payload_decoder VARCHAR,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (external_id, network_server)
);

CREATE INDEX device_eui_index ON device (eui);

CREATE TABLE device_measurement
(
    id                  UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    device_id           UUID        NOT NULL REFERENCES device,
    measurement_type_id UUID REFERENCES measurement_type,
    measured_at         TIMESTAMPTZ NOT NULL,
    value               VARCHAR     NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (device_id, measurement_type_id, measured_at)
);

CREATE INDEX device_measurement_measured_at_index ON device_measurement (created_at);

CREATE TABLE carrier
(
    id         UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    first_name VARCHAR     NOT NULL,
    surname    VARCHAR     NOT NULL,
    phone      VARCHAR     NOT NULL,
    user_id    VARCHAR REFERENCES users,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (phone)
);

CREATE TABLE freight
(
    id          UUID PRIMARY KEY     DEFAULT uuid_generate_v1(),
    device_id   UUID        NOT NULL REFERENCES device,
    product_id  UUID        NOT NULL REFERENCES product,
    carrier_id  UUID        NOT NULL REFERENCES carrier,
    origin      JSONB       NOT NULL,
    destination JSONB       NOT NULL,
    violated    BOOLEAN     NOT NULL DEFAULT false,
    description VARCHAR,
    started_at  TIMESTAMPTZ,
    finished_at TIMESTAMPTZ,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (device_id, product_id, created_at)
);
