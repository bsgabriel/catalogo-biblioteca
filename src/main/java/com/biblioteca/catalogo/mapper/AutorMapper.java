package com.biblioteca.catalogo.mapper;

import com.biblioteca.catalogo.dto.AutorDto;
import com.biblioteca.catalogo.entity.Autor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class AutorMapper {

    public static AutorDto entidadeParaDto(Autor autor) {
        return AutorDto.builder()
                .autorId(autor.getAutorId())
                .nome(autor.getNome())
                .build();
    }

    public static Autor dtoParaEntidade(AutorDto dto) {
        return Autor.builder()
                .autorId(dto.getAutorId())
                .nome(dto.getNome())
                .build();
    }

}
