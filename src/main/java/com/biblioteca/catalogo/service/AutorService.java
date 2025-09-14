package com.biblioteca.catalogo.service;

import com.biblioteca.catalogo.database.dao.AutorDAO;
import com.biblioteca.catalogo.dto.AutorDto;
import com.biblioteca.catalogo.mapper.AutorMapper;

public class AutorService {

    private final AutorDAO autorDAO;

    public AutorService() {
        autorDAO = new AutorDAO();
    }

    public AutorDto buscarOuCriarPorNome(String nome) {
        return AutorMapper.entidadeParaDto(autorDAO.buscarOuCriarPorNome(nome));
    }

}
