package com.biblioteca.catalogo.mapper;

import com.biblioteca.catalogo.dto.AutorDto;
import com.biblioteca.catalogo.dto.EditoraDto;
import com.biblioteca.catalogo.dto.LivroDto;
import com.biblioteca.catalogo.entity.Livro;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class LivroMapper {

    public static Livro dtoParaEntidade(LivroDto dto) {
        return Livro.builder()
                .livroId(dto.getLivroId())
                .titulo(dto.getTitulo())
                .isbn(dto.getIsbn())
                .editora(Optional.ofNullable(dto.getEditora())
                        .map(EditoraMapper::dtoParaEntidade)
                        .orElse(null))
                .autores(dto.getAutores()
                        .stream()
                        .map(AutorMapper::dtoParaEntidade)
                        .collect(Collectors.toList()))
                .build();
    }

    public static LivroDto arrayToLivroDto(Object[] dadosLivro) {
        int idx = 0;

        return LivroDto
                .builder()
                .livroId((Long) dadosLivro[idx++])
                .titulo((String) dadosLivro[idx++])
                .autores((List<AutorDto>) dadosLivro[idx++])
                .dataPublicacao((LocalDate) dadosLivro[idx++])
                .isbn((Long) dadosLivro[idx++])
                .editora((EditoraDto) dadosLivro[idx++])
                .build();
    }

}
