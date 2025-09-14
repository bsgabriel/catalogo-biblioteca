package com.biblioteca.catalogo.database.dao;

import com.biblioteca.catalogo.entity.Livro;

import java.util.Optional;

public class LivroDAO extends GenericDAO<Livro, Long> {

    public LivroDAO() {
        super(Livro.class);
    }

    /**
     * Busca o ID de um livro cadastrado pelo seu ISBN
     *
     * @param isbn Identificador Unico do livro
     * @return ID do livro
     */
    public Optional<Long> buscarIdExistentePorISBN(Long isbn) {
        String jpql = "SELECT l.livroId FROM Livro l WHERE l.isbn = ?1";
        return executeSingleQueryForType(Long.class, jpql, isbn);
    }

}