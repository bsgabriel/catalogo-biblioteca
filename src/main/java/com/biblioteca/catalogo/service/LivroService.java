package com.biblioteca.catalogo.service;

import com.biblioteca.catalogo.database.dao.LivroDAO;
import com.biblioteca.catalogo.dto.AutorDto;
import com.biblioteca.catalogo.dto.DadosImportacaoCsvDto;
import com.biblioteca.catalogo.dto.EditoraDto;
import com.biblioteca.catalogo.dto.LivroDto;
import com.biblioteca.catalogo.dto.openlibrary.AutorResponseDto;
import com.biblioteca.catalogo.dto.openlibrary.LivroResponseDto;
import com.biblioteca.catalogo.entity.Livro;
import com.biblioteca.catalogo.exception.ApiExecutionException;
import com.biblioteca.catalogo.exception.ConsultaLivroException;
import com.biblioteca.catalogo.factory.LivroFactory;
import com.biblioteca.catalogo.mapper.LivroMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
public class LivroService {

    private final LivroDAO livroDAO;
    private final LivroFactory livroFactory;
    private final AutorService autorService;
    private final EditoraService editoraService;
    private final LivroCsvService livroCsvService;
    private final OpenLibraryService openLibraryService;

    public LivroService() {
        this.livroFactory = new LivroFactory();
        this.openLibraryService = new OpenLibraryService();
        this.livroDAO = new LivroDAO();
        this.editoraService = new EditoraService();
        this.autorService = new AutorService();
        this.livroCsvService = new LivroCsvService();
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

    /**
     * Realiza a importação de dados de um CSV
     *
     * @param file Arquivo CSV com dados para processar.
     * @return Um {@link DadosImportacaoCsvDto} com dados sobre a importação
     */
    public DadosImportacaoCsvDto carregarDadosCSV(File file) {
        return livroCsvService.processarCSV(file);
    }

    /**
     * Busca todos os livros no banco
     *
     * @return Lista com todos os livros cadastrados
     */
    public List<LivroDto> buscarTodos() {
        return livroDAO.buscarTodos()
                .stream()
                .map(LivroMapper::entidadeParaDto)
                .collect(Collectors.toList());
    }

    /**
     * Remove um livro. Após a exclusão, remove os registros que ficaram órfãos.
     */
    public void deletarLivro(LivroDto livroDto) {
        livroDAO.delete(LivroMapper.dtoParaEntidade(livroDto));

        Long editoraId = livroDto.getEditora().getEditoraId();

        if (!livroDAO.editoraEmUso(editoraId)) {
            editoraService.deletarEditoraPorID(editoraId);
        }

        livroDto.getAutores().
                stream()
                .map(AutorDto::getAutorId)
                .forEach(id -> {
                    if (!livroDAO.autorEmUso(id)) {
                        autorService.deletarAutorPorID(id);
                    }
                });
    }

    /**
     * Busca inteligente por termo, efetuando as seguintes validações e buscas:
     * <br>- Se o termo informado for um número (termo.matches("\\d+")), busca por ID.
     * <br>- Remove hífens e valida o ISBN. Se for um ISBN válido, faz a busca por ISBN.
     * <br>- Busca pelo título, autores e editora.
     * <br>
     * Para cada resultado das buscas, o livro é adicionado num Map, tendo como chave, o seu ID. Desse modo, não vai acontecer de um livro aparecer repetidamente.
     *
     * @param termo Termo a ser buscado, podendo ser o código do livro, ISBN, título, nome de um autor ou o nome da editora.
     * @return Lista com os livros que batem com a busca
     */
    public List<LivroDto> buscarLivro(String termo) {
        Map<Long, Livro> livros = new HashMap<>();

        // se for apenas número filtra por id
        if (termo.matches("\\d+")) {
            Livro livro = livroDAO.findById(Long.valueOf(termo)).orElse(null);
            if (nonNull(livro)) {
                livros.put(livro.getLivroId(), livro);
            }
        }

        // se for um ISBN valido, filtra por ele
        String apenasNumeros = termo.replaceAll("[^0-9]", "");
        if (apenasNumeros.length() == 10 || apenasNumeros.length() == 13) {
            Livro livro = livroDAO.buscarPorIsbn(Long.valueOf(termo.replaceAll("[^0-9]", "")));
            if (nonNull(livro)) {
                livros.put(livro.getLivroId(), livro);
            }
        }

        // busca por campos de texto (título, editora e autores)
        livroDAO.buscarTextoGeral(termo).forEach(livro -> livros.put(livro.getLivroId(), livro));

        return livros.values()
                .stream()
                .map(LivroMapper::entidadeParaDto)
                .collect(Collectors.toList());
    }

}
