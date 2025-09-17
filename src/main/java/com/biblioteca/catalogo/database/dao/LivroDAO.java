package com.biblioteca.catalogo.database.dao;

import com.biblioteca.catalogo.entity.Autor;
import com.biblioteca.catalogo.entity.Editora;
import com.biblioteca.catalogo.entity.Livro;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
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
        return super.findAll("autores");
    }

    public Optional<Livro> findById(Long id) {
        return super.findById(id, "autores");
    }

    public boolean editoraEmUso(Long editoraId) {
        String jpql = "SELECT count(l) FROM Livro l WHERE l.editora.editoraId = ?1";
        return executeSingleQueryForType(Long.class, jpql, editoraId).orElse(0L) > 0;
    }

    public boolean autorEmUso(Long autorId) {
        String jpql = new StringBuilder()
                .append(" SELECT ")
                .append("    count(l) ")
                .append(" FROM ")
                .append("    Livro l ")
                .append(" JOIN ")
                .append("    l.autores a ")
                .append(" WHERE ")
                .append("    a.autorId = ?1 ")
                .toString();

        return executeSingleQueryForType(Long.class, jpql, autorId).orElse(0L) > 0;
    }
}