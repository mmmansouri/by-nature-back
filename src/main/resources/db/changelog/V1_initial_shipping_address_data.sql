-- Insert sample shipping addresses for testing
INSERT INTO shipping_addresses (id, customer_id, label, first_name, last_name, phone_number, email, street_number, street, city, region, postal_code, country, created_at, updated_at)
VALUES
    ('3a6d41c8-e2e5-4e7f-b5c1-d1e9f4a0c2b7', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Home', 'John', 'Doe', '+33612345678', 'john.doe@example.com', '123', 'Rue de Paris', 'Paris', 'Île-de-France', '75001', 'France', NOW(), NOW()),

    ('cd6e82a0-7b5c-4d3e-9f2a-8b1e0c7d9e6a', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Work', 'Marie', 'Dubois', '+33698765432', 'marie.dubois@example.com', '45', 'Avenue Victor Hugo', 'Lyon', 'Auvergne-Rhône-Alpes', '69002', 'France', NOW(), NOW()),

    ('f8e9d2c1-b7a6-5c4d-3e2f-1a0b9c8d7e6a', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Vacation Home', 'Pierre', 'Martin', '+33612345678', 'pierre.martin@example.com', '78', 'Boulevard Saint-Michel', 'Marseille', 'Provence-Alpes-Côte Azur', '13001', 'France', NOW(), NOW());