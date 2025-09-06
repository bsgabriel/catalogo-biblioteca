CREATE TABLE editoras (
    editora_id SERIAL,
    nome VARCHAR(255) NOT NULL,
    CONSTRAINT pk_editoras PRIMARY KEY (editora_id)
);

COMMENT ON TABLE editoras IS 'Registro de editoras.';
COMMENT ON COLUMN editoras.editora_id IS 'Identificador único da editora.';
COMMENT ON COLUMN editoras.nome IS 'Nome da editora.';
