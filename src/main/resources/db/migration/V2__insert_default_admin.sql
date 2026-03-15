-- Inserir usuário administrador padrão
-- Login: admin
-- Senha: admin123
-- Hash BCrypt gerado para 'admin123': $2a$10$JDGon1B.Q4qXJL1zZFkMdOfoxER7tFf/a8ZZWEVV2bLbW5vggNUBu
INSERT INTO users (username, email, password, is_active, created_at, updated_at)
VALUES ('admin', 'admin@noto.local', '$2a$10$JDGon1B.Q4qXJL1zZFkMdOfoxER7tFf/a8ZZWEVV2bLbW5vggNUBu', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Atribuir todas as roles (ROLE_USER e ROLE_ADMIN) ao usuário admin
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE u.username = 'admin';

