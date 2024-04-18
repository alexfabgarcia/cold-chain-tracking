CREATE TABLE users
(
    username VARCHAR PRIMARY KEY NOT NULL,
    password VARCHAR             NOT NULL,
    enabled  INT                 NOT NULL DEFAULT 1
);

CREATE TABLE authorities
(
    username  VARCHAR NOT NULL REFERENCES users,
    authority VARCHAR NOT NULL,
    UNIQUE (username, authority)
);

-- Passwords created via Spring CLI: spring encodepassword PASSWORD_VALUE
-- See https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html#authentication-password-storage-boot-cli
-- admin: adminpassword
-- alexgarcia: alexgarciapassword
INSERT INTO users (username, password)
VALUES ('admin', '{bcrypt}$2a$10$NqBD83TZJoC0xgkUloGdZegGNNOeb.JdFKuyng4ZVQRPdVUs0xgD6'),
       ('alexgarcia', '{bcrypt}$2a$10$TulpkgTTDt7yU1CxJWojtelssa92UcawTyryGjhxN6qoo5xoaqm2O');

INSERT INTO authorities (username, authority)
VALUES ('admin', 'ADMIN'),
       ('admin', 'CARRIER'),
       ('alexgarcia', 'CARRIER');

ALTER TABLE carrier
    ADD user_id VARCHAR REFERENCES users;

UPDATE carrier
SET user_id = 'alexgarcia'
WHERE first_name = 'Alex'
  and surname = 'Garcia';
