package com.biblioteca.catalogo.service;

import com.biblioteca.catalogo.api.OpenLibraryApi;
import com.biblioteca.catalogo.dto.openlibrary.AutorResponseDto;
import com.biblioteca.catalogo.dto.openlibrary.KeyDto;
import com.biblioteca.catalogo.dto.openlibrary.LivroResponseDto;
import com.biblioteca.catalogo.exception.ApiExecutionException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OpenLibraryServiceTest {

    @Mock
    private OpenLibraryApi mockOpenLibraryApi;

    @Mock
    private Call<LivroResponseDto> mockLivroCall;

    @Mock
    private Call<AutorResponseDto> mockAutorCall;

    @InjectMocks
    private OpenLibraryService openLibraryService;

    @Test
    public void deveRetornarLivroValido_quandoIsbn_forValido() throws Exception {
        // Arrange
        String isbn = "1234567891234";
        LivroResponseDto livroEsperado = new LivroResponseDto();
        Response<LivroResponseDto> response = criarResponseLivroSucesso(livroEsperado);

        when(mockOpenLibraryApi.buscarLivro(isbn)).thenReturn(mockLivroCall);
        when(mockLivroCall.execute()).thenReturn(response);

        // Act
        LivroResponseDto resultado = openLibraryService.buscarLivro(isbn);

        // Assert
        assertNotNull(resultado);
        assertEquals(livroEsperado, resultado);
        verify(mockOpenLibraryApi).buscarLivro(isbn);
        verify(mockLivroCall).execute();
    }

    @Test
    public void deveRetornarNull_quandoLivro_NaoForEncontrado() throws Exception {
        // Arrange
        String isbn = "9999999999999";
        Response<LivroResponseDto> response = criarResponseLivroErro(404);
        when(mockOpenLibraryApi.buscarLivro(isbn)).thenReturn(mockLivroCall);
        when(mockLivroCall.execute()).thenReturn(response);

        // Act
        LivroResponseDto resultado = openLibraryService.buscarLivro(isbn);

        // Assoert
        assertNull(resultado);
        verify(mockOpenLibraryApi).buscarLivro(isbn);
        verify(mockLivroCall).execute();
    }

    @Test
    public void devePropagarExcecao_quandoOcorrerIoException_aoBuscarLivro() throws Exception {
        // Arrange
        String isbn = "1234567891234";
        IOException exceptionEsperada = new IOException("Erro");

        when(mockOpenLibraryApi.buscarLivro(isbn)).thenReturn(mockLivroCall);
        when(mockLivroCall.execute()).thenThrow(exceptionEsperada);

        // Act Assert
        ApiExecutionException exception = assertThrows(ApiExecutionException.class, () -> openLibraryService.buscarLivro(isbn));

        assertEquals("Ocorreu um erro ao buscar dados do livro", exception.getMessage());
        assertEquals(exceptionEsperada, exception.getCause());
        verify(mockOpenLibraryApi).buscarLivro(isbn);
        verify(mockLivroCall).execute();
    }

    @Test
    public void deveLancarExcecao_quandoCodigoDeErro_naoFor404() throws Exception {
        // Arrange
        String isbn = "1234567891234";
        Response<LivroResponseDto> response = criarResponseLivroErro(500);

        when(mockOpenLibraryApi.buscarLivro(isbn)).thenReturn(mockLivroCall);
        when(mockLivroCall.execute()).thenReturn(response);

        // Act Assert
        ApiExecutionException exception = assertThrows(ApiExecutionException.class, () -> openLibraryService.buscarLivro(isbn));

        assertEquals("Consulta retornou código 500", exception.getMessage());
        verify(mockOpenLibraryApi).buscarLivro(isbn);
        verify(mockLivroCall).execute();
    }

    @Test
    public void deveRetornarAutor_quandoKey_forValida() throws Exception {
        // Arrange
        KeyDto keyDto = KeyDto.builder()
                .key("/authors/AU1234")
                .build();
        String idAutor = "AU1234";

        AutorResponseDto autorEsperado = new AutorResponseDto();
        Response<AutorResponseDto> response = criarResponseAutorSucesso(autorEsperado);

        when(mockOpenLibraryApi.buscarAutor(idAutor)).thenReturn(mockAutorCall);
        when(mockAutorCall.execute()).thenReturn(response);

        // Act
        AutorResponseDto resultado = openLibraryService.buscarAutor(keyDto);

        // Assert
        assertNotNull(resultado);
        assertEquals(autorEsperado, resultado);
        verify(mockOpenLibraryApi).buscarAutor(idAutor);
        verify(mockAutorCall).execute();
    }

    @Test
    public void deveExtrairIdAutorCorretamente() throws Exception {
        // Arrange
        KeyDto keyDto = KeyDto.builder()
                .key("/authors/AU1234")
                .build();
        String idAutorEsperado = "AU1234";

        AutorResponseDto autorEsperado = new AutorResponseDto();
        Response<AutorResponseDto> response = criarResponseAutorSucesso(autorEsperado);

        when(mockOpenLibraryApi.buscarAutor(idAutorEsperado)).thenReturn(mockAutorCall);
        when(mockAutorCall.execute()).thenReturn(response);


        // Act
        openLibraryService.buscarAutor(keyDto);

        // Assert
        verify(mockOpenLibraryApi).buscarAutor(idAutorEsperado);
    }

    @Test
    public void devePropagarExcecao_quandoOcorreIoException() throws Exception {
        // Arrange
        KeyDto keyDto = KeyDto.builder()
                .key("/authors/AU1234")
                .build();

        IOException exceptionEsperada = new IOException("Erro");

        when(mockOpenLibraryApi.buscarAutor(anyString())).thenReturn(mockAutorCall);
        when(mockAutorCall.execute()).thenThrow(exceptionEsperada);

        // Act Assert
        ApiExecutionException exception = assertThrows(ApiExecutionException.class, () -> openLibraryService.buscarAutor(keyDto));

        assertEquals("Ocorreu um erro ao buscar dados do autor", exception.getMessage());
        assertEquals(exceptionEsperada, exception.getCause());
    }

    @Test
    public void deveLancarExcecao_quandoResposta_naoForBemSucedida() throws Exception {
        // Arrange
        KeyDto keyDto = KeyDto.builder()
                .key("/authors/AU1234")
                .build();

        Response<AutorResponseDto> response = criarResponseAutorErro(500);

        when(mockOpenLibraryApi.buscarAutor(anyString())).thenReturn(mockAutorCall);
        when(mockAutorCall.execute()).thenReturn(response);

        // Act Assert
        ApiExecutionException exception = assertThrows(ApiExecutionException.class, () -> openLibraryService.buscarAutor(keyDto));
        assertTrue(exception.getMessage().startsWith("500 - "));
    }

    @Test
    public void deveLancarExcecao_quandoResponseBody_forNull() throws Exception {
        // Arrange
        KeyDto keyDto = KeyDto.builder()
                .key("/authors/AU1234")
                .build();

        Response<AutorResponseDto> response = criarResponseAutorSucesso(null);

        when(mockOpenLibraryApi.buscarAutor(anyString())).thenReturn(mockAutorCall);
        when(mockAutorCall.execute()).thenReturn(response);

        // Act Assert
        ApiExecutionException exception = assertThrows(ApiExecutionException.class, () -> openLibraryService.buscarAutor(keyDto));
        assertEquals("Autor não encontrado", exception.getMessage());
    }

    private Response<LivroResponseDto> criarResponseLivroSucesso(LivroResponseDto livro) {
        return Response.success(livro);
    }

    private Response<LivroResponseDto> criarResponseLivroErro(int codigo) {
        return Response.error(codigo, ResponseBody.create("", MediaType.parse("")));
    }

    private Response<AutorResponseDto> criarResponseAutorSucesso(AutorResponseDto autor) {
        return Response.success(autor);
    }

    private Response<AutorResponseDto> criarResponseAutorErro(int codigo) {
        return Response.error(codigo, ResponseBody.create("", MediaType.parse("")));
    }
}