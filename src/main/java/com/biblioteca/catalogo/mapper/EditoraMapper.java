package com.biblioteca.catalogo.mapper;

import com.biblioteca.catalogo.dto.EditoraDto;
import com.biblioteca.catalogo.entity.Editora;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class EditoraMapper {

    public static EditoraDto entidadeParaDto(Editora entidade) {
        return EditoraDto.builder()
                .editoraId(entidade.getEditoraId())
                .nome(entidade.getNome())
                .build();
    }

    public static Editora dtoParaEntidade(EditoraDto dto) {
        return Editora.builder()
                .editoraId(dto.getEditoraId())
                .nome(dto.getNome())
                .build();
    }

}
