package com.biblioteca.catalogo.database.dao;

import com.biblioteca.catalogo.database.dto.Join;
import com.biblioteca.catalogo.entity.Livro;

import javax.persistence.criteria.JoinType;
import java.util.List;
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

    public List<Livro> buscarTodos() {
        return super.findAll(Join.builder()
                .coluna("autores")
                .tipo(JoinType.LEFT)
                .build());
    }
}