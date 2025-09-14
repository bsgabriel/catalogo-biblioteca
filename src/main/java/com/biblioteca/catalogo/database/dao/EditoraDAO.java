package com.biblioteca.catalogo.database.dao;

import com.biblioteca.catalogo.entity.Editora;

public class EditoraDAO extends GenericDAO<Editora, Long> {

    public EditoraDAO() {
        super(Editora.class);
    }

    /**
     * Busca uma editora por nome. Se não encontrar, cria e a retorna.
     *
     * @param nome da editora a ser buscada
     * @return {@link Editora} encontrada ou criada
     */
    public Editora buscarOuCriarPorNome(String nome) {
        String jpql = "SELECT e FROM Editora e WHERE e.nome = ?1";
        return executeSingleQuery(jpql, nome).orElseGet(() -> save(Editora.builder()
                .nome(nome)
                .build()));
    }
}
