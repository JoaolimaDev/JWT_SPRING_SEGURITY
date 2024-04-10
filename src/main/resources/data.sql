CREATE TABLE IF NOT EXISTS tb_role (
    role_id BIGINT PRIMARY KEY,
    name VARCHAR(255)
);

INSERT INTO tb_role (role_id, name) VALUES (1, 'admin') ON CONFLICT DO NOTHING;
INSERT INTO tb_role (role_id, name) VALUES (2, 'basic') ON CONFLICT DO NOTHING;
