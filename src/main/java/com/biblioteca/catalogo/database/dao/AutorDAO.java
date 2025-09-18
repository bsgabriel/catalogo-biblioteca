package com.biblioteca.catalogo.database.dao;

import com.biblioteca.catalogo.entity.Autor;

import java.util.Optional;

public class AutorDAO extends GenericDAO<Autor, Long> {

    public AutorDAO() {
        super(Autor.class);
    }

    /**
     * Busca um autor por nome.
     *
     * @param nome do autor a ser buscado
     * @return {@link Optional<Autor>} encontrado
     */
    public Optional<Autor> buscarPorNome(String nome) {
        String jpql = "SELECT a FROM Autor a WHERE a.nome = ?1";
        return executeSingleQuery(jpql, nome);
    }
}
