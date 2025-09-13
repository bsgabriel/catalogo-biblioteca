package com.biblioteca.catalogo.factory;

import com.biblioteca.catalogo.dto.AutorDto;
import com.biblioteca.catalogo.dto.EditoraDto;
import com.biblioteca.catalogo.dto.LivroDto;
import com.biblioteca.catalogo.dto.openlibrary.AutorResponseDto;
import com.biblioteca.catalogo.dto.openlibrary.LivroResponseDto;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class LivroFactory {

    public LivroDto montarLivroDto(String isbn, LivroResponseDto livro, List<AutorResponseDto> autores) {
        EditoraDto editora = null;
        if (!livro.getPublishers().isEmpty()) {
            editora = EditoraDto.builder()
                    .nome(livro.getPublishers().get(0))
                    .build();
        }

        return LivroDto.builder()
                .isbn(Long.valueOf(isbn))
                .editora(editora)
                .autores(autores.stream()
                        .map(autor -> AutorDto.builder()
                                .nome(autor.getName())
                                .build())
                        .collect(Collectors.toList()))
                .titulo(livro.getTitle())
                .dataPublicacao(livro.getPublishDate())
                .build();
    }
}
