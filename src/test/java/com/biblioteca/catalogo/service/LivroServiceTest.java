package com.biblioteca.catalogo.service;

import com.biblioteca.catalogo.database.dao.LivroDAO;
import com.biblioteca.catalogo.dto.AutorDto;
import com.biblioteca.catalogo.dto.DadosImportacaoCsvDto;
import com.biblioteca.catalogo.dto.EditoraDto;
import com.biblioteca.catalogo.dto.LivroDto;
import com.biblioteca.catalogo.dto.openlibrary.AutorResponseDto;
import com.biblioteca.catalogo.dto.openlibrary.KeyDto;
import com.biblioteca.catalogo.dto.openlibrary.LivroResponseDto;
import com.biblioteca.catalogo.entity.Livro;
import com.biblioteca.catalogo.exception.ApiExecutionException;
import com.biblioteca.catalogo.exception.ConsultaLivroException;
import com.biblioteca.catalogo.factory.LivroFactory;
import org.apache.commons.collections4.ListUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LivroServiceTest {

    @Mock
    private LivroDAO mockLivroDAO;

    @Mock
    private LivroFactory mockLivroFactory;

    @Mock
    private AutorService mockAutorService;

    @Mock
    private EditoraService mockEditoraService;

    @Mock
    private LivroCsvService mockLivroCsvService;

    @Mock
    private OpenLibraryService mockOpenLibraryService;

    @InjectMocks
    private LivroService livroService;

    @Test
    public void quandoBuscarLivroApi_comIsbnValido_deveRetornarLivroDto() throws Exception {
        // Arrange
        String isbn = "1234567891234";
        LivroResponseDto livroResponse = new LivroResponseDto();
        KeyDto autorKey = KeyDto.builder()
                .key("/authors/AU1234")
                .build();

        livroResponse.setAuthors(Collections.singletonList(autorKey));

        AutorResponseDto autorResponse = new AutorResponseDto();
        LivroDto livroEsperado = new LivroDto();

        when(mockOpenLibraryService.buscarLivro(isbn)).thenReturn(livroResponse);
        when(mockOpenLibraryService.buscarAutor(autorKey)).thenReturn(autorResponse);
        when(mockLivroFactory.montarLivroDto(eq(isbn), eq(livroResponse), any(List.class))).thenReturn(livroEsperado);

        // Act
        LivroDto resultado = livroService.buscarLivroApi(isbn);

        // Assert
        assertNotNull(resultado);
        assertEquals(livroEsperado, resultado);
        verify(mockOpenLibraryService).buscarLivro(isbn);
        verify(mockOpenLibraryService).buscarAutor(autorKey);
        verify(mockLivroFactory).montarLivroDto(eq(isbn), eq(livroResponse), any(List.class));
    }

    @Test
    public void quandoBuscarLivroApi_forApiExecutionException_deveLancarConsultaLivroException() throws Exception {
        // Arrange
        String isbn = "1234567891234";
        ApiExecutionException apiException = new ApiExecutionException("Erro na API");

        when(mockOpenLibraryService.buscarLivro(isbn)).thenThrow(apiException);

        // Act & Assert
        try {
            livroService.buscarLivroApi(isbn);
            fail("Deveria ter lançado ConsultaLivroException");
        } catch (ConsultaLivroException exception) {
            assertEquals("Ocorreu um erro ao buscar dados para o livro " + isbn, exception.getMessage());
            assertEquals(apiException, exception.getCause());
        }

        verify(mockOpenLibraryService).buscarLivro(isbn);
        verify(mockLivroFactory, never()).montarLivroDto(anyString(), any(), any());
    }

    @Test
    public void quandoBuscarLivroApi_forLivroNull_deveLancarConsultaLivroException() throws Exception {
        // Arrange
        String isbn = "9999999999999";

        when(mockOpenLibraryService.buscarLivro(isbn)).thenReturn(null);

        // Act & Assert
        try {
            livroService.buscarLivroApi(isbn);
            fail("Deveria ter lançado ConsultaLivroException");
        } catch (ConsultaLivroException exception) {
            assertEquals("Livro não encontrado", exception.getMessage());
        }

        verify(mockOpenLibraryService).buscarLivro(isbn);
        verify(mockLivroFactory, never()).montarLivroDto(anyString(), any(), any());
    }

    @Test
    public void quandoBuscarLivroApi_derErroAoBuscarAutor_deveIgnorarAutorComErro() throws Exception {
        // Arrange
        String isbn = "1234567891234";
        LivroResponseDto livroResponse = new LivroResponseDto();

        KeyDto autorKey1 = KeyDto.builder()
                .key("/authors/AU1234")
                .build();

        KeyDto autorKey2 = KeyDto.builder()
                .key("/authors/AU5678")
                .build();

        livroResponse.setAuthors(Arrays.asList(autorKey1, autorKey2));

        AutorResponseDto autorResponse = new AutorResponseDto();
        LivroDto livroEsperado = new LivroDto();

        when(mockOpenLibraryService.buscarLivro(isbn)).thenReturn(livroResponse);
        when(mockOpenLibraryService.buscarAutor(autorKey1)).thenReturn(autorResponse);
        when(mockOpenLibraryService.buscarAutor(autorKey2)).thenThrow(new ApiExecutionException("Erro"));
        when(mockLivroFactory.montarLivroDto(eq(isbn), eq(livroResponse), any(List.class))).thenReturn(livroEsperado);

        // Act
        LivroDto resultado = livroService.buscarLivroApi(isbn);

        // Assert
        assertNotNull(resultado);
        verify(mockOpenLibraryService).buscarLivro(isbn);
        verify(mockOpenLibraryService).buscarAutor(autorKey1);
        verify(mockOpenLibraryService).buscarAutor(autorKey2);
        verify(mockLivroFactory).montarLivroDto(eq(isbn), eq(livroResponse), any(List.class));
    }

    @Test
    public void quandoSalvarLivro_eExistirIsbn_deveFazerAtualizacao() {
        // Arrange
        LivroDto livroDto = LivroDto.builder()
                .isbn(1234567891234L)
                .editora(EditoraDto.builder()
                        .nome("Editora")
                        .build())
                .autores(Collections.singletonList(AutorDto.builder()
                        .nome("Autor")
                        .build()))
                .build();

        Long livroIdExistente = 123L;
        EditoraDto editoraSalva = EditoraDto.builder().build();
        AutorDto autorSalvo = AutorDto.builder().build();

        when(mockLivroDAO.buscarIdExistentePorISBN(1234567891234L)).thenReturn(Optional.of(livroIdExistente));
        when(mockEditoraService.buscarOuCriarPorNome("Editora")).thenReturn(editoraSalva);
        when(mockAutorService.buscarOuCriarPorNome("Autor")).thenReturn(autorSalvo);

        // Act
        livroService.salvar(livroDto);

        // Assert
        assertEquals(livroIdExistente, livroDto.getLivroId());
        verify(mockLivroDAO).buscarIdExistentePorISBN(1234567891234L);
        verify(mockEditoraService).buscarOuCriarPorNome("Editora");
        verify(mockAutorService).buscarOuCriarPorNome("Autor");
        verify(mockLivroDAO).save(any());
    }

    @Test
    public void quandoCarregarCsv_comArquivoValido_deveRetornarDados() {
        // Arrange
        File arquivo = new File("teste.csv");
        DadosImportacaoCsvDto dadosEsperados = DadosImportacaoCsvDto.builder().build();

        when(mockLivroCsvService.processarCSV(arquivo)).thenReturn(dadosEsperados);

        // Act
        DadosImportacaoCsvDto resultado = livroService.carregarDadosCSV(arquivo);

        // Assert
        assertEquals(dadosEsperados, resultado);
        verify(mockLivroCsvService).processarCSV(arquivo);
    }

    @Test
    public void quandoBuscarTodos_eExistirDados_deveRetornarListaPreenchida() {
        // Arrange
        Livro livro1 = Livro.builder()
                .livroId(1L)
                .build();
        Livro livro2 = Livro.builder()
                .livroId(2L)
                .build();

        List<Livro> livrosEntidade = Arrays.asList(livro1, livro2);

        when(mockLivroDAO.buscarTodos()).thenReturn(livrosEntidade);

        // Act
        List<LivroDto> resultado = livroService.buscarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(mockLivroDAO).buscarTodos();
    }

    @Test
    public void quandoBuscarTodos_eNaoExistirDados_deveRetornarListaVazia() {
        // Arrange
        when(mockLivroDAO.buscarTodos()).thenReturn(new ArrayList<>());

        // Act
        List<LivroDto> resultado = livroService.buscarTodos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(mockLivroDAO).buscarTodos();
    }

    @Test
    public void quandoDeletarLivro_comEditoraEAutorEmUso_naoDeveDeletarEditoraNemAutor() {
        // Arrange
        LivroDto livroDto = LivroDto.builder()
                .editora(EditoraDto.builder()
                        .editoraId(1L)
                        .build())
                .autores(Collections.singletonList(AutorDto.builder()
                        .autorId(2L)
                        .build()))
                .build();

        when(mockLivroDAO.editoraEmUso(1L)).thenReturn(true);
        when(mockLivroDAO.autorEmUso(2L)).thenReturn(true);

        // Act
        livroService.deletarLivro(livroDto);

        // Assert
        verify(mockLivroDAO).delete(any());
        verify(mockLivroDAO).editoraEmUso(1L);
        verify(mockLivroDAO).autorEmUso(2L);
        verify(mockEditoraService, never()).deletarEditoraPorID(anyLong());
        verify(mockAutorService, never()).deletarAutorPorID(anyLong());
    }

    @Test
    public void quandoDeletarLivro_comEditoraEAutorNaoUsados_deveDeletarEditoraEAutor() {
        // Arrange
        LivroDto livroDto = LivroDto.builder()
                .editora(EditoraDto.builder()
                        .editoraId(1L)
                        .build())
                .autores(Collections.singletonList(AutorDto.builder()
                        .autorId(2L)
                        .build()))
                .build();

        when(mockLivroDAO.editoraEmUso(1L)).thenReturn(false);
        when(mockLivroDAO.autorEmUso(2L)).thenReturn(false);

        // Act
        livroService.deletarLivro(livroDto);

        // Assert
        verify(mockLivroDAO).delete(any());
        verify(mockLivroDAO).editoraEmUso(1L);
        verify(mockLivroDAO).autorEmUso(2L);
        verify(mockEditoraService).deletarEditoraPorID(1L);
        verify(mockAutorService).deletarAutorPorID(2L);
    }

    @Test
    public void quandoDeletarLivro_forMultiplosAutores_deveProcessarTodos() {
        // Arrange
        AutorDto autor1 = AutorDto.builder()
                .autorId(1L)
                .build();

        AutorDto autor2 = AutorDto.builder()
                .autorId(2L)
                .build();

        LivroDto livroDto = LivroDto.builder()
                .editora(EditoraDto.builder()
                        .editoraId(1L)
                        .build())
                .autores(Arrays.asList(autor1, autor2))
                .build();

        when(mockLivroDAO.editoraEmUso(1L)).thenReturn(true);
        when(mockLivroDAO.autorEmUso(1L)).thenReturn(false);
        when(mockLivroDAO.autorEmUso(2L)).thenReturn(true);

        // Act
        livroService.deletarLivro(livroDto);

        // Assert
        verify(mockLivroDAO).delete(any());
        verify(mockLivroDAO).autorEmUso(1L);
        verify(mockLivroDAO).autorEmUso(2L);
        verify(mockAutorService).deletarAutorPorID(1L);
        verify(mockAutorService, never()).deletarAutorPorID(2L);
    }

    @Test
    public void quandoBuscarLivro_porTermoNumerico_deveBuscarPorId() {
        // Arrange
        String termo = "123";
        Livro livro = Livro.builder()
                .livroId(123L)
                .build();

        when(mockLivroDAO.findById(123L)).thenReturn(Optional.of(livro));
        when(mockLivroDAO.buscarTextoGeral(termo)).thenReturn(new ArrayList<>());

        // Act
        List<LivroDto> resultado = livroService.buscarLivro(termo);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(mockLivroDAO).findById(123L);
        verify(mockLivroDAO).buscarTextoGeral(termo);
    }

    @Test
    public void quandoBuscarLivro_porIsbn_deveBuscar() {
        // Arrange
        String termo = "1234567891234";
        Livro livro = Livro.builder()
                .livroId(1L)
                .build();

        when(mockLivroDAO.findById(1234567891234L)).thenReturn(Optional.empty());
        when(mockLivroDAO.buscarPorIsbn(1234567891234L)).thenReturn(livro);
        when(mockLivroDAO.buscarTextoGeral(termo)).thenReturn(new ArrayList<>());

        // Act
        List<LivroDto> resultado = livroService.buscarLivro(termo);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(mockLivroDAO).buscarPorIsbn(1234567891234L);
        verify(mockLivroDAO).buscarTextoGeral(termo);
    }

    @Test
    public void quandoBuscarLivro_porTexto_deveBuscarPorTexto() {
        // Arrange
        String termo = "Teste";
        Livro livro1 = Livro.builder()
                .livroId(1L)
                .build();

        Livro livro2 = Livro.builder()
                .livroId(2L)
                .build();

        List<Livro> livrosEncontrados = Arrays.asList(livro1, livro2);
        when(mockLivroDAO.buscarTextoGeral(termo)).thenReturn(livrosEncontrados);

        // Act
        List<LivroDto> resultado = livroService.buscarLivro(termo);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(mockLivroDAO).buscarTextoGeral(termo);
        verify(mockLivroDAO, never()).findById(anyLong());
        verify(mockLivroDAO, never()).buscarPorIsbn(anyLong());
    }

    @Test
    public void quandoBuscarLivro_comMultiplosCriterios_deveTrazerSoUm() {
        // Arrange
        String termo = "123";
        Livro livro = Livro.builder()
                .livroId(123L)
                .build();

        // Mesmo livro encontrado por ID e por busca de texto
        when(mockLivroDAO.findById(123L)).thenReturn(Optional.of(livro));
        when(mockLivroDAO.buscarTextoGeral(termo)).thenReturn(Collections.singletonList(livro));

        // Act
        List<LivroDto> resultado = livroService.buscarLivro(termo);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(mockLivroDAO).findById(123L);
        verify(mockLivroDAO).buscarTextoGeral(termo);
    }

}