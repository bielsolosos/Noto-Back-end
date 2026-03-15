-- V3: Migrate users and roles tables from BIGSERIAL/BIGINT to UUID primary keys

-- Drop dependent tables first
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;

-- Recreate roles with UUID primary key
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(20) UNIQUE NOT NULL,
    description VARCHAR(100)
);

-- Recreate users with UUID primary key
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Recreate join table with UUID foreign keys
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_is_active ON users(is_active);

-- Re-insert default roles
INSERT INTO roles (name, description) VALUES
    ('ROLE_USER', 'Usuário comum'),
    ('ROLE_ADMIN', 'Administrador do sistema');

-- Re-insert default admin user
-- Login: admin / Senha: admin123
INSERT INTO users (username, email, password, is_active, created_at, updated_at)
VALUES ('admin', 'admin@noto.local', '$2a$10$JDGon1B.Q4qXJL1zZFkMdOfoxER7tFf/a8ZZWEVV2bLbW5vggNUBu', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Assign all roles to admin
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE u.username = 'admin';

