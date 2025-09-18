package com.biblioteca.catalogo.service;

import com.biblioteca.catalogo.database.dao.EditoraDAO;
import com.biblioteca.catalogo.dto.EditoraDto;
import com.biblioteca.catalogo.entity.Editora;
import com.biblioteca.catalogo.mapper.EditoraMapper;

public class EditoraService {

    private final EditoraDAO editoraDAO;

    public EditoraService() {
        this.editoraDAO = new EditoraDAO();
    }

    /**
     * Busca uma editora pelo nome. Se não encontrar, cria um registro.
     * @param nome da Editora
     * @return @{@link EditoraDto} encontrada ou criada
     */
    public EditoraDto buscarOuCriarPorNome(String nome) {
        Editora editora = editoraDAO.buscarPorNome(nome)
                .orElseGet(() -> editoraDAO.save(Editora.builder()
                        .nome(nome)
                        .build()));

        return EditoraMapper.entidadeParaDto(editora);
    }

    public void deletarEditoraPorID(Long editoraId) {
        editoraDAO.deleteById(editoraId);
    }
}
