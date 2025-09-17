package com.biblioteca.catalogo.service;

import com.biblioteca.catalogo.database.dao.EditoraDAO;
import com.biblioteca.catalogo.dto.EditoraDto;
import com.biblioteca.catalogo.mapper.EditoraMapper;

public class EditoraService {

    private final EditoraDAO editoraDAO;

    public EditoraService() {
        this.editoraDAO = new EditoraDAO();
    }

    public EditoraDto buscarOuCriarPorNome(String nome) {
        return EditoraMapper.entidadeParaDto(editoraDAO.buscarOuCriarPorNome(nome));
    }

    public void deletarEditoraPorID(Long editoraId) {
        editoraDAO.deleteById(editoraId);
    }
}
