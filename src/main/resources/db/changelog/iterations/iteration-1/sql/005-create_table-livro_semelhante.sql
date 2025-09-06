CREATE TABLE livro_semelhante (
    livro_id INT NOT NULL,
    semelhante_id INT NOT NULL,
    PRIMARY KEY (livro_id, semelhante_id),
    CONSTRAINT fk_livro_self FOREIGN KEY (livro_id) REFERENCES livros(livro_id) ON DELETE CASCADE,
    CONSTRAINT fk_semelhante FOREIGN KEY (semelhante_id) REFERENCES livros(livro_id) ON DELETE CASCADE
);

COMMENT ON TABLE livro_semelhante IS 'Relação de livros semelhantes à um livro.';
COMMENT ON COLUMN livro_semelhante.livro_id IS 'Identificador de um livro.';
COMMENT ON COLUMN livro_semelhante.semelhante_id IS 'Identificador de um livro semelhante ao livro desejado.';