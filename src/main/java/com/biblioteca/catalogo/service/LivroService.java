package com.biblioteca.catalogo.service;

import com.biblioteca.catalogo.database.dao.LivroDAO;
import com.biblioteca.catalogo.dto.EditoraDto;
import com.biblioteca.catalogo.dto.LivroDto;
import com.biblioteca.catalogo.dto.openlibrary.AutorResponseDto;
import com.biblioteca.catalogo.dto.openlibrary.LivroResponseDto;
import com.biblioteca.catalogo.exception.ApiExecutionException;
import com.biblioteca.catalogo.exception.ConsultaLivroException;
import com.biblioteca.catalogo.factory.LivroFactory;
import com.biblioteca.catalogo.mapper.LivroMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
public class LivroService {

    private final LivroDAO livroDAO;
    private final LivroFactory livroFactory;
    private final AutorService autorService;
    private final EditoraService editoraService;
    private final OpenLibraryService openLibraryService;

    public LivroService() {
        this.livroFactory = new LivroFactory();
        this.openLibraryService = new OpenLibraryService();
        this.livroDAO = new LivroDAO();
        this.editoraService = new EditoraService();
        this.autorService = new AutorService();
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

        if (isNull(livro)) {
            throw new ConsultaLivroException("Livro não encontrado");
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

    /**
     * Cadastra/atualiza um livro.
     * <br>
     * Antes do processo, busca o ID do livro pelo ISBN, garantindo que:
     * <br>- Ao cadastrar um livro e já haver um livro com esse ISBN, o mesmo será atualizado
     * <br>- Os dados serão atualizados para o ISBN correto
     *
     * @param dadosLivros Um {@link LivroDto} com dados do livro para cadastrar/atualizar
     */
    public void salvar(LivroDto dadosLivros) {
        if (nonNull(dadosLivros.getIsbn())) {
            Long livroId = buscarIdExistentePorISBN(dadosLivros.getIsbn());
            if (nonNull(livroId)) {
                dadosLivros.setLivroId(livroId);
            }
        }

        EditoraDto editora = Optional.ofNullable(dadosLivros.getEditora())
                .map(e -> editoraService.buscarOuCriarPorNome(e.getNome()))
                .orElse(null);

        dadosLivros.setEditora(editora);
        dadosLivros.setAutores(dadosLivros.getAutores()
                .stream()
                .map(autor -> autorService.buscarOuCriarPorNome(autor.getNome()))
                .collect(Collectors.toList()));

        livroDAO.save(LivroMapper.dtoParaEntidade(dadosLivros));
    }

    /**
     * Busca o ID de um livro filtrando por ISBN
     *
     * @param isbn Identificador Único do livro
     * @return ID do livro na tabela livros
     */
    public Long buscarIdExistentePorISBN(Long isbn) {
        return livroDAO.buscarIdExistentePorISBN(isbn).orElse(null);
    }

}
