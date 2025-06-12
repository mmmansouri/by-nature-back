create table users
(
    id            uuid         not null,
    email         varchar(255) not null unique,
    password      varchar(255) not null,
    customer_id   uuid unique,
    active        boolean      not null,
    role          varchar(50)  not null,
    last_login_at timestamp(6),
    created_at    timestamp(6) not null,
    updated_at    timestamp(6) not null,
    primary key (id)
);

create table items
(
    id          uuid         not null,
    name        varchar(255) not null,
    price       float(53)    not null,
    description varchar(255) not null,
    image_url   varchar(255) not null,
    created_at  timestamp(6) not null,
    updated_at  timestamp(6) not null,
    primary key (id)
);

CREATE TYPE order_status AS ENUM (
    'CREATED',
    'PAYMENT_INTEND_CREATED',
    'PAYMENT_PROCESSING',
    'PAYMENT_CONFIRMED',
    'PAYMENT_FAILED',
    'SHIPPED',
    'DELIVERED',
    'CANCELLED'
    );

create table order_items
(
    item_id  uuid    not null,
    order_id uuid    not null,
    quantity integer not null,
    primary key (item_id, order_id)
);
create table orders
(
    id            uuid         not null,
    total         float(53)    not null,
    customer_id   uuid         not null,
    city          varchar(255) not null,
    country       varchar(255) not null,
    email         varchar(255) not null,
    first_name    varchar(255) not null,
    last_name     varchar(255) not null,
    phone_number  varchar(255) not null,
    postal_code   varchar(255) not null,
    region        varchar(255) not null,
    status        order_status not null,
    payment_intent_id varchar(255),
    street        varchar(255) not null,
    street_number varchar(255) not null,
    created_at    timestamp(6) not null,
    updated_at    timestamp(6) not null,
    primary key (id)
);
create table shipping_addresses
(
    id            uuid         not null,
    customer_id   uuid         not null,
    label    varchar(255) not null,
    first_name    varchar(255) not null,
    last_name     varchar(255) not null,
    email         varchar(255) not null,
    phone_number  varchar(255) not null,
    street        varchar(255) not null,
    street_number varchar(255) not null,
    city          varchar(255) not null,
    country       varchar(255) not null,
    postal_code   varchar(255) not null,
    region        varchar(255) not null,
    created_at    timestamp(6) not null,
    updated_at    timestamp(6) not null,
    primary key (id)
);

create table customers
(
    id            uuid         not null,
    user_id       uuid unique not null,
    first_name    varchar(255) not null,
    last_name     varchar(255) not null,
    email         varchar(255) not null,
    phone_number  varchar(255) not null,
    street_number varchar(255),
    street        varchar(255),
    city          varchar(255),
    region        varchar(255),
    postal_code   varchar(255),
    country       varchar(255),
    created_at    timestamp(6) not null,
    updated_at    timestamp(6) not null,
    primary key (id)
);



