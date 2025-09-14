package com.biblioteca.catalogo.database.dao;

import com.biblioteca.catalogo.entity.Autor;

public class AutorDAO extends GenericDAO<Autor, Long> {

    public AutorDAO() {
        super(Autor.class);
    }

    /**
     * Busca um autor por nome. Se não encontrar, cria e a retorna.
     *
     * @param nome do autor a ser buscado
     * @return {@link Autor} encontrado ou criado
     */
    public Autor buscarOuCriarPorNome(String nome) {
        String jpql = "SELECT a FROM Autor a WHERE a.nome = ?1";
        return executeSingleQuery(jpql, nome).orElseGet(() -> save(Autor.builder()
                .nome(nome)
                .build()));
    }
}
