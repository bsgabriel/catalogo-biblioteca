CREATE TABLE livro_autor (
    livro_id INT NOT NULL,
    autor_id INT NOT NULL,
    PRIMARY KEY (livro_id, autor_id),
    CONSTRAINT fk_livro FOREIGN KEY (livro_id) REFERENCES livros(livro_id) ON DELETE CASCADE,
    CONSTRAINT fk_autor FOREIGN KEY (autor_id) REFERENCES autores(autor_id) ON DELETE CASCADE
);

COMMENT ON TABLE livro_autor IS 'Relação de um autores e seus livros.';
COMMENT ON COLUMN livro_autor.livro_id IS 'Identificador do livro que foi escrito por um ou mais autores.';
COMMENT ON COLUMN livro_autor.autor_id IS 'Identificador do autor que participou da escritura do livro.';
