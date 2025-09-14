package com.biblioteca.catalogo.database.dao;

import com.biblioteca.catalogo.entity.Livro;

public class LivroDAO extends GenericDAO<Livro, Long>  {

    public LivroDAO() {
        super(Livro.class);
    }

}