-- Migration para criação das tabelas do módulo de contratos
CREATE TABLE IF NOT EXISTS contratos (
    id BIGSERIAL PRIMARY KEY,
    servidor_id BIGINT NOT NULL REFERENCES servidores(id),
    valor_solicitado DECIMAL(12,2) NOT NULL,
    taxa_juros_mes DECIMAL(5,2) NOT NULL,
    quantidade_parcelas INT NOT NULL,
    valor_parcela DECIMAL(12,2) NOT NULL,
    valor_total_financiado DECIMAL(12,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    data_solicitacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS parcelas (
    id BIGSERIAL PRIMARY KEY,
    contrato_id BIGINT NOT NULL REFERENCES contratos(id) ON DELETE CASCADE,
    numero_parcela INT NOT NULL,
    valor DECIMAL(12,2) NOT NULL,
    data_vencimento DATE NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS historico_contratos (
    id BIGSERIAL PRIMARY KEY,
    contrato_id BIGINT NOT NULL REFERENCES contratos(id) ON DELETE CASCADE,
    status_anterior VARCHAR(50),
    status_novo VARCHAR(50) NOT NULL,
    observacao TEXT,
    usuario_id BIGINT REFERENCES usuarios(id),
    data_evento TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
