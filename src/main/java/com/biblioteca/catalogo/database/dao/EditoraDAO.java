package com.biblioteca.catalogo.database.dao;

import com.biblioteca.catalogo.entity.Editora;

import java.util.Optional;

public class EditoraDAO extends GenericDAO<Editora, Long> {

    public EditoraDAO() {
        super(Editora.class);
    }

    /**
     * Busca uma editora por nome.
     *
     * @param nome da editora a ser buscada
     * @return Um {@link Optional<Editora>}
     */
    public Optional<Editora> buscarPorNome(String nome) {
        String jpql = "SELECT e FROM Editora e WHERE e.nome = ?1";
        return executeSingleQuery(jpql, nome);
    }
}
