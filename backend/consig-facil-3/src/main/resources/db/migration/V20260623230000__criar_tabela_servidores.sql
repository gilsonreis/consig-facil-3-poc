CREATE TABLE IF NOT EXISTS servidores (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    matricula VARCHAR(50) UNIQUE NOT NULL,
    margem_consignavel DECIMAL(10,2) DEFAULT 0.00,
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_servidores_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE INDEX IF NOT EXISTS idx_servidores_usuario_id ON servidores(usuario_id);
