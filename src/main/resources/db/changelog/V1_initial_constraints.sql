alter table if exists order_items
    add constraint FK88tn2oqcxl1034banqif9r70x foreign key (item_id) references items;

alter table if exists order_items
    add constraint FKbioxgbv59vetrxe0ejfubep1w foreign key (order_id) references orders;

alter table if exists orders
    add constraint FK_orders_customer foreign key (customer_id) references customers;

alter table if exists shipping_addresses
    add constraint FK_shipping_addresses_customer foreign key (customer_id) references customers;

alter table if exists customers
    add constraint fk_customer_user foreign key (user_id) references users;

alter table if exists users
    add constraint fk_user_customer foreign key (customer_id) references customers;