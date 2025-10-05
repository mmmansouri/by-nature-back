-- Insert Angular frontend client
INSERT INTO app_clients (id, app_client_id, app_client_secret, active, allowed_origin)
VALUES
    ('a2c68ee4-9c3f-4c9a-8d7b-f1a8e6357e42', 'bynature-front',
     '$2a$10$j2RjZM4Lb7pyP/4IVDY...H5SXCwRSH68SgsWidNzAzQ3ZxlS2jU2', -- hashed version of 'client-secret-123'
     true, 'http://localhost:4200');