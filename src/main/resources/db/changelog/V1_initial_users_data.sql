-- Admin user
INSERT INTO users (id, email, password, active, role, last_login_at, created_at, updated_at)
VALUES ('a47ac10b-58cc-4372-a567-0e02b2c3d401', 'admin@bynature.com',
        '$2a$12$eW.hxQPcAm7J/YZP0zvlCOQRNrn8KRdP7/0DXYYfAjMEYb1fafCKG', -- hashed 'admin123'
        true, 'ADMIN', NOW(), NOW(), NOW());

-- Customer user linked to John Doe
INSERT INTO users (id, email, password, customer_id, active, role, last_login_at, created_at, updated_at)
VALUES ('b47ac10b-58cc-4372-a567-0e02b2c3d402', 'john.doe@example.com',
        '$2a$12$LG7h7rZA.8YzRQQ6x1degu9JZv9MQQcBJQhtO1wQY7wAMJz5tGZ0e', -- hashed 'password123'
        'f47ac10b-58cc-4372-a567-0e02b2c3d479', true, 'CUSTOMER', NOW(), NOW(),
        NOW());


-- Customer free user
INSERT INTO users (id, email, password, customer_id, active, role, last_login_at, created_at, updated_at)
VALUES ('b48ac10b-58cc-4372-a567-0e02b2c3d402', 'free.doe@example.com',
        '$2a$12$LG7h7rZA.8YzRQQ6x1degu9JZv9MQQcBJQhtO1wQY7wAMJz5tGZ0e', -- hashed 'password123'
        null, true, 'CUSTOMER', NOW(), NOW(),
        NOW());