-- V20260621120000__add_coluna_username.sql
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS username VARCHAR(100) UNIQUE;

-- Opcional: Criar um índice para otimizar a busca por username
CREATE INDEX IF NOT EXISTS idx_usuarios_username ON usuarios(username);
