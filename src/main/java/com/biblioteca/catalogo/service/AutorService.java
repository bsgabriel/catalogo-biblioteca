package com.biblioteca.catalogo.service;

import com.biblioteca.catalogo.database.dao.AutorDAO;
import com.biblioteca.catalogo.dto.AutorDto;
import com.biblioteca.catalogo.entity.Autor;
import com.biblioteca.catalogo.mapper.AutorMapper;

public class AutorService {

    private final AutorDAO autorDAO;

    public AutorService() {
        autorDAO = new AutorDAO();
    }

    /**
     * Busca um autor pelo nome. Se não encontrar, cria um registro.
     *
     * @param nome do Autor
     * @return @{@link AutorDto} encontrado ou criado
     */
    public AutorDto buscarOuCriarPorNome(String nome) {
        Autor autor = autorDAO.buscarPorNome(nome)
                .orElseGet(() -> autorDAO.save(Autor.builder()
                        .nome(nome)
                        .build()));

        return AutorMapper.entidadeParaDto(autor);
    }

     public void deletarAutorPorID(Long id) {
        autorDAO.deleteById(id);
    }
}
