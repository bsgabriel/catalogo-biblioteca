CREATE TABLE autores (
    autor_id SERIAL,
    nome VARCHAR(255) NOT NULL,
    CONSTRAINT pk_autores PRIMARY KEY (autor_id)
);

COMMENT ON TABLE autores IS 'Registro de autores.';
COMMENT ON COLUMN autores.autor_id IS 'Identificador único do autor.';
COMMENT ON COLUMN autores.nome IS 'Nome do autor.';