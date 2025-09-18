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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    public void quandoBuscarLivroApi_forIsbnValido_deveRetornarLivroDto() throws Exception {
        // Arrange
        String isbn = "1234567891234";
        LivroResponseDto livroResponse = new LivroResponseDto();
        KeyDto autorKey = new KeyDto();
        autorKey.setKey("/authors/AU1234");
        livroResponse.setAuthors(Arrays.asList(autorKey));

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
    public void quandoBuscarLivroApi_forErroNoBuscarAutor_deveIgnorarAutorComErro() throws Exception {
        // Arrange
        String isbn = "1234567891234";
        LivroResponseDto livroResponse = new LivroResponseDto();

        KeyDto autorKey1 = new KeyDto();
        autorKey1.setKey("/authors/AU1234");
        KeyDto autorKey2 = new KeyDto();
        autorKey2.setKey("/authors/OL23920A");
        livroResponse.setAuthors(Arrays.asList(autorKey1, autorKey2));

        AutorResponseDto autorResponse = new AutorResponseDto();
        LivroDto livroEsperado = new LivroDto();

        when(mockOpenLibraryService.buscarLivro(isbn)).thenReturn(livroResponse);
        when(mockOpenLibraryService.buscarAutor(autorKey1)).thenReturn(autorResponse);
        when(mockOpenLibraryService.buscarAutor(autorKey2)).thenThrow(new ApiExecutionException("Erro autor"));
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

    // Testes para salvar

    @Test
    public void quandoSalvar_forLivroComIsbnExistente_deveDefinirLivroId() throws Exception {
        // Arrange
        LivroDto livroDto = new LivroDto();
        livroDto.setIsbn(1234567891234L);

        EditoraDto editora = new EditoraDto();
        editora.setNome("Editora Teste");
        livroDto.setEditora(editora);

        AutorDto autor = new AutorDto();
        autor.setNome("Autor Teste");
        livroDto.setAutores(Arrays.asList(autor));

        Long livroIdExistente = 123L;
        EditoraDto editoraSalva = new EditoraDto();
        AutorDto autorSalvo = new AutorDto();

        when(mockLivroDAO.buscarIdExistentePorISBN(1234567891234L)).thenReturn(Optional.of(livroIdExistente));
        when(mockEditoraService.buscarOuCriarPorNome("Editora Teste")).thenReturn(editoraSalva);
        when(mockAutorService.buscarOuCriarPorNome("Autor Teste")).thenReturn(autorSalvo);

        // Act
        livroService.salvar(livroDto);

        // Assert
        assertEquals(livroIdExistente, livroDto.getLivroId());
        verify(mockLivroDAO).buscarIdExistentePorISBN(1234567891234L);
        verify(mockEditoraService).buscarOuCriarPorNome("Editora Teste");
        verify(mockAutorService).buscarOuCriarPorNome("Autor Teste");
        verify(mockLivroDAO).save(any());
    }

    @Test
    public void quandoSalvar_forLivroSemIsbn_naoDeveBuscarIdExistente() throws Exception {
        // Arrange
        LivroDto livroDto = new LivroDto();
        livroDto.setIsbn(null);

        AutorDto autor = new AutorDto();
        autor.setNome("Autor Teste");
        livroDto.setAutores(Arrays.asList(autor));

        AutorDto autorSalvo = new AutorDto();
        when(mockAutorService.buscarOuCriarPorNome("Autor Teste")).thenReturn(autorSalvo);

        // Act
        livroService.salvar(livroDto);

        // Assert
        verify(mockLivroDAO, never()).buscarIdExistentePorISBN(anyLong());
        verify(mockAutorService).buscarOuCriarPorNome("Autor Teste");
        verify(mockLivroDAO).save(any());
    }

    @Test
    public void quandoSalvar_forLivroSemEditora_deveDefinirEditoraComoNull() throws Exception {
        // Arrange
        LivroDto livroDto = new LivroDto();
        livroDto.setEditora(null);

        AutorDto autor = new AutorDto();
        autor.setNome("Autor Teste");
        livroDto.setAutores(Arrays.asList(autor));

        AutorDto autorSalvo = new AutorDto();
        when(mockAutorService.buscarOuCriarPorNome("Autor Teste")).thenReturn(autorSalvo);

        // Act
        livroService.salvar(livroDto);

        // Assert
        assertNull(livroDto.getEditora());
        verify(mockEditoraService, never()).buscarOuCriarPorNome(anyString());
        verify(mockLivroDAO).save(any());
    }

    // Testes para buscarIdExistentePorISBN

    @Test
    public void quandoBuscarIdExistentePorISBN_forIsbnExistente_deveRetornarId() throws Exception {
        // Arrange
        Long isbn = 1234567891234L;
        Long idExistente = 123L;

        when(mockLivroDAO.buscarIdExistentePorISBN(isbn)).thenReturn(Optional.of(idExistente));

        // Act
        Long resultado = livroService.buscarIdExistentePorISBN(isbn);

        // Assert
        assertEquals(idExistente, resultado);
        verify(mockLivroDAO).buscarIdExistentePorISBN(isbn);
    }

    @Test
    public void quandoBuscarIdExistentePorISBN_forIsbnNaoExistente_deveRetornarNull() throws Exception {
        // Arrange
        Long isbn = 9999999999999L;

        when(mockLivroDAO.buscarIdExistentePorISBN(isbn)).thenReturn(Optional.empty());

        // Act
        Long resultado = livroService.buscarIdExistentePorISBN(isbn);

        // Assert
        assertNull(resultado);
        verify(mockLivroDAO).buscarIdExistentePorISBN(isbn);
    }

    // Testes para carregarDadosCSV

    @Test
    public void quandoCarregarDadosCSV_forArquivoValido_deveRetornarDadosProcessados() throws Exception {
        // Arrange
        File arquivo = new File("teste.csv");
        DadosImportacaoCsvDto dadosEsperados = new DadosImportacaoCsvDto();

        when(mockLivroCsvService.processarCSV(arquivo)).thenReturn(dadosEsperados);

        // Act
        DadosImportacaoCsvDto resultado = livroService.carregarDadosCSV(arquivo);

        // Assert
        assertEquals(dadosEsperados, resultado);
        verify(mockLivroCsvService).processarCSV(arquivo);
    }

    // Testes para buscarTodos

    @Test
    public void quandoBuscarTodos_forLivrosExistentes_deveRetornarListaLivroDto() throws Exception {
        // Arrange
        Livro livro1 = new Livro();
        livro1.setLivroId(1L);
        Livro livro2 = new Livro();
        livro2.setLivroId(2L);

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
    public void quandoBuscarTodos_forListaVazia_deveRetornarListaVazia() throws Exception {
        // Arrange
        when(mockLivroDAO.buscarTodos()).thenReturn(new ArrayList<>());

        // Act
        List<LivroDto> resultado = livroService.buscarTodos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(mockLivroDAO).buscarTodos();
    }

    // Testes para deletarLivro

    @Test
    public void quandoDeletarLivro_forEditoraEAutorEmUso_naoDeveDeletarEditoraNemAutor() throws Exception {
        // Arrange
        EditoraDto editora = new EditoraDto();
        editora.setEditoraId(1L);

        AutorDto autor = new AutorDto();
        autor.setAutorId(2L);

        LivroDto livroDto = new LivroDto();
        livroDto.setEditora(editora);
        livroDto.setAutores(Arrays.asList(autor));

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
    public void quandoDeletarLivro_forEditoraEAutorNaoEmUso_deveDeletarEditoraEAutor() throws Exception {
        // Arrange
        EditoraDto editora = new EditoraDto();
        editora.setEditoraId(1L);

        AutorDto autor = new AutorDto();
        autor.setAutorId(2L);

        LivroDto livroDto = new LivroDto();
        livroDto.setEditora(editora);
        livroDto.setAutores(Arrays.asList(autor));

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
    public void quandoDeletarLivro_forMultiplosAutores_deveProcessarTodos() throws Exception {
        // Arrange
        EditoraDto editora = new EditoraDto();
        editora.setEditoraId(1L);

        AutorDto autor1 = new AutorDto();
        autor1.setAutorId(2L);
        AutorDto autor2 = new AutorDto();
        autor2.setAutorId(3L);

        LivroDto livroDto = new LivroDto();
        livroDto.setEditora(editora);
        livroDto.setAutores(Arrays.asList(autor1, autor2));

        when(mockLivroDAO.editoraEmUso(1L)).thenReturn(true);
        when(mockLivroDAO.autorEmUso(2L)).thenReturn(false);
        when(mockLivroDAO.autorEmUso(3L)).thenReturn(true);

        // Act
        livroService.deletarLivro(livroDto);

        // Assert
        verify(mockLivroDAO).delete(any());
        verify(mockLivroDAO).autorEmUso(2L);
        verify(mockLivroDAO).autorEmUso(3L);
        verify(mockAutorService).deletarAutorPorID(2L);
        verify(mockAutorService, never()).deletarAutorPorID(3L);
    }

    // Testes para buscarLivro (método de busca geral)

    @Test
    public void quandoBuscarLivro_forTermoNumerico_deveBuscarPorId() throws Exception {
        // Arrange
        String termo = "123";
        Livro livro = new Livro();
        livro.setLivroId(123L);

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
    public void quandoBuscarLivro_forIsbn10Digitos_deveBuscarPorIsbn() throws Exception {
        // Arrange
        String termo = "1234567890";
        Livro livro = new Livro();
        livro.setLivroId(1L);

        when(mockLivroDAO.findById(1234567890L)).thenReturn(Optional.empty());
        when(mockLivroDAO.buscarPorIsbn(1234567890L)).thenReturn(livro);
        when(mockLivroDAO.buscarTextoGeral(termo)).thenReturn(new ArrayList<>());

        // Act
        List<LivroDto> resultado = livroService.buscarLivro(termo);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(mockLivroDAO).buscarPorIsbn(1234567890L);
        verify(mockLivroDAO).buscarTextoGeral(termo);
    }

    @Test
    public void quandoBuscarLivro_forIsbn13Digitos_deveBuscarPorIsbn() throws Exception {
        // Arrange
        String termo = "1234567891234";
        Livro livro = new Livro();
        livro.setLivroId(1L);

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
    public void quandoBuscarLivro_forTextoGeral_deveBuscarPorTexto() throws Exception {
        // Arrange
        String termo = "Java Programming";
        Livro livro1 = new Livro();
        livro1.setLivroId(1L);
        Livro livro2 = new Livro();
        livro2.setLivroId(2L);

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
    public void quandoBuscarLivro_forIsbnComHifens_deveRemoverCaracteresEBuscar() throws Exception {
        // Arrange
        String termo = "978-0-13-468599-1";
        Livro livro = new Livro();
        livro.setLivroId(1L);

        when(mockLivroDAO.findById(1234567891234L)).thenReturn(Optional.empty());
        when(mockLivroDAO.buscarPorIsbn(1234567891234L)).thenReturn(livro);
        when(mockLivroDAO.buscarTextoGeral(termo)).thenReturn(new ArrayList<>());

        // Act
        List<LivroDto> resultado = livroService.buscarLivro(termo);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(mockLivroDAO).buscarPorIsbn(1234567891234L);
    }

    @Test
    public void quandoBuscarLivro_forMultiplosCriterios_deveRemoverDuplicatas() throws Exception {
        // Arrange
        String termo = "123";
        Livro livro = new Livro();
        livro.setLivroId(123L);

        // Mesmo livro encontrado por ID e por busca de texto
        when(mockLivroDAO.findById(123L)).thenReturn(Optional.of(livro));
        when(mockLivroDAO.buscarTextoGeral(termo)).thenReturn(Arrays.asList(livro));

        // Act
        List<LivroDto> resultado = livroService.buscarLivro(termo);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size()); // Deve remover duplicata
        verify(mockLivroDAO).findById(123L);
        verify(mockLivroDAO).buscarTextoGeral(termo);
    }

    @Test
    public void quandoBuscarLivro_forTermoVazio_deveRetornarListaVazia() throws Exception {
        // Arrange
        String termo = "";
        when(mockLivroDAO.buscarTextoGeral(termo)).thenReturn(new ArrayList<>());

        // Act
        List<LivroDto> resultado = livroService.buscarLivro(termo);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(mockLivroDAO).buscarTextoGeral(termo);
    }
}