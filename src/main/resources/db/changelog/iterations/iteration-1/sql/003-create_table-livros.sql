CREATE TABLE livros (
    livro_id SERIAL,
    titulo VARCHAR(255),
    data_publicacao DATE,
    isbn numeric(13) UNIQUE,
    editora_id INT,
    CONSTRAINT pk_livros PRIMARY KEY (livro_id),
    CONSTRAINT fk_editora FOREIGN KEY (editora_id) REFERENCES editoras(editora_id)
);

COMMENT ON TABLE livros IS 'Registro de livros.';
COMMENT ON COLUMN livros.livro_id IS 'Identificador único do livro.';
COMMENT ON COLUMN livros.titulo IS 'Título do livro.';
COMMENT ON COLUMN livros.data_publicacao IS 'Data em que o livro foi publicado.';
COMMENT ON COLUMN livros.isbn IS 'Número de Livro Padrão Internacional (International Standard Book Number), número único de 13 dígitos utilizado para identificar livros tanto a nivel nacional quanto internacional.';
COMMENT ON COLUMN livros.editora_id IS 'ID da editora que publicou o livro.';