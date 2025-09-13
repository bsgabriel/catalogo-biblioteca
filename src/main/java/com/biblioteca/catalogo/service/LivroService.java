package com.biblioteca.catalogo.service;

import com.biblioteca.catalogo.dto.LivroDto;
import com.biblioteca.catalogo.dto.openlibrary.AutorResponseDto;
import com.biblioteca.catalogo.dto.openlibrary.LivroResponseDto;
import com.biblioteca.catalogo.exception.ApiExecutionException;
import com.biblioteca.catalogo.exception.ConsultaLivroException;
import com.biblioteca.catalogo.factory.LivroFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class LivroService {

    private final LivroFactory livroFactory;
    private final OpenLibraryService openLibraryService;

    public LivroService() {
        this.livroFactory = new LivroFactory();
        this.openLibraryService = new OpenLibraryService();
    }

    /**
     * Busca um livro na Open Library usando ISBN
     *
     * @param isbn Identificador Universal do livro a ser buscado
     * @return Um {@link LivroDto} com dados do livro
     * @throws ConsultaLivroException Caso ocorra algum problema ao buscar os dados do livro
     */
    public LivroDto buscarLivroApi(String isbn) throws ConsultaLivroException {
        LivroResponseDto livro = null;
        try {
            livro = openLibraryService.buscarLivro(isbn);
        } catch (ApiExecutionException e) {
            String msg = String.format("Ocorreu um erro ao buscar dados para o livro %s", isbn);
            throw new ConsultaLivroException(msg, e);
        }

        List<AutorResponseDto> autores = livro.getAuthors()
                .stream()
                .map(autor -> {
                    try {
                        return openLibraryService.buscarAutor(autor);
                    } catch (ApiExecutionException e) {
                        String msg = String.format("Ocorreu um erro ao buscar dados para o autor: %s", autor.getKey());
                        log.error(msg, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return livroFactory.montarLivroDto(isbn, livro, autores);
    }
}
