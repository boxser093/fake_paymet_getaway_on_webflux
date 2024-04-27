CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE merchants
(
    id          INT primary key not null generated always as IDENTITY (INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 99999 CACHE 1),
    merchant_id VARCHAR(64)     not null unique,
    create_at   TIMESTAMP,
    update_at   TIMESTAMP,
    key         VARCHAR(2048)   not null
);

CREATE TABLE accounts
(
    id          INT primary key not null generated always as IDENTITY (INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 99999 CACHE 1),
    merchant_id INT references merchants (id) ON UPDATE CASCADE,
    balance     DECIMAL,
    currency    VARCHAR(10),
    create_at   TIMESTAMP,
    update_at   TIMESTAMP
);

CREATE TABLE customers
(
    id         INT primary key not null generated always as IDENTITY (INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 99999 CACHE 1),
    first_name VARCHAR(64) unique,
    last_name  VARCHAR(255) unique,
    country    VARCHAR(10),
    create_at  TIMESTAMP,
    update_at  TIMESTAMP
);

CREATE TABLE customer_cards
(
    id          INT primary key not null generated always as IDENTITY (INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 99999 CACHE 1),
    customer_id INT references customers (id) ON UPDATE CASCADE,
    cart_number VARCHAR(64) unique,
    exp_date    VARCHAR(10),
    cvv         VARCHAR(10),
    balance     DECIMAL,
    currency    VARCHAR(10),
    create_at   TIMESTAMP,
    update_at   TIMESTAMP
);

CREATE TABLE transactions
(
    id                 uuid primary key default uuid_generate_v4(),
    create_at          TIMESTAMP,
    update_at          TIMESTAMP,
    transaction_type   VARCHAR(10),
    transaction_status VARCHAR(20),
    payment_method     VARCHAR(24),
    amount             DECIMAL,
    currency           VARCHAR(10),
    customer_card_id   INT references customer_cards (id) ON UPDATE CASCADE,
    account_id         INT references accounts (id) ON UPDATE CASCADE,
    notification_url   VARCHAR(128)
);

CREATE TABLE webhooks
(
    id               INT primary key not null generated always as IDENTITY (INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 99999 CACHE 1),
    create_At        TIMESTAMP,
    update_At        TIMESTAMP,
    transaction_id   uuid references transactions (id) ON UPDATE CASCADE,
    account_id       INT references accounts (id) ON UPDATE CASCADE,
    body_request     text,
    request_url      text,
    response_status  text,
    body_response    text,
    transaction_type text,
    try_number       INT
);
