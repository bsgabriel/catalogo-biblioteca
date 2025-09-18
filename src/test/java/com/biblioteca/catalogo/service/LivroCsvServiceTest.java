package com.biblioteca.catalogo.service;

import com.biblioteca.catalogo.dto.DadosImportacaoCsvDto;
import com.biblioteca.catalogo.dto.LivroDto;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class LivroCsvServiceTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @InjectMocks
    private LivroCsvService livroCsvService;

    @Test
    public void quandoCsv_comCabecalhoEDadosValidos_deveRetornarLivrosImportados() throws Exception {
        // Arrange
        File arquivo = tempFolder.newFile("teste.csv");
        String conteudo = new StringBuilder()
                .append("ISBN,Titulo,Editora,Data_Publicacao,Autor")
                .append("\n")
                .append("1111111111111,Livro 1,Editora 1,15/08/2009,Autor 1")
                .append("\n")
                .append("2222222222222,Livro 2,Editora 2,10/01/2017,Autor 2;Autor 1")
                .toString();

        escreverArquivo(arquivo, conteudo);

        // Act
        DadosImportacaoCsvDto resultado = livroCsvService.processarCSV(arquivo);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.getLivrosImportados().size());
        assertTrue(resultado.getErros().isEmpty());

        LivroDto livro1 = resultado.getLivrosImportados().get(0);
        assertEquals(Long.valueOf(1111111111111L), livro1.getIsbn());
        assertEquals("LIVRO 1", livro1.getTitulo());
        assertEquals("EDITORA 1", livro1.getEditora().getNome());
        assertEquals(LocalDate.of(2009, 8, 15), livro1.getDataPublicacao());
        assertEquals(1, livro1.getAutores().size());
        assertEquals("AUTOR 1", livro1.getAutores().get(0).getNome());

        LivroDto livro2 = resultado.getLivrosImportados().get(1);
        assertEquals("LIVRO 2", livro2.getTitulo());
        assertEquals("EDITORA 2", livro2.getEditora().getNome());
        assertEquals(LocalDate.of(2017, 1, 10), livro2.getDataPublicacao());
        assertEquals(2, livro2.getAutores().size());
        assertEquals("AUTOR 2", livro2.getAutores().get(0).getNome());
        assertEquals("AUTOR 1", livro2.getAutores().get(1).getNome());
    }

    @Test
    public void quandoCsv_semCabecalho_deveProcessarTodasLinhas() throws Exception {
        // Arrange
        File arquivo = tempFolder.newFile("teste.csv");
        String conteudo = new StringBuilder()
                .append("1111111111111,Livro 1,Editora 1,15/08/2009,Autor 1")
                .append("\n")
                .append("2222222222222,Livro 2,Editora 2,10/01/2017,Autor 2;Autor 1")
                .toString();

        escreverArquivo(arquivo, conteudo);

        // Act
        DadosImportacaoCsvDto resultado = livroCsvService.processarCSV(arquivo);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.getLivrosImportados().size());
        assertTrue(resultado.getErros().isEmpty());

        LivroDto livro1 = resultado.getLivrosImportados().get(0);
        assertEquals(Long.valueOf(1111111111111L), livro1.getIsbn());
        assertEquals("LIVRO 1", livro1.getTitulo());
        assertEquals("EDITORA 1", livro1.getEditora().getNome());
        assertEquals(LocalDate.of(2009, 8, 15), livro1.getDataPublicacao());
        assertEquals(1, livro1.getAutores().size());
        assertEquals("AUTOR 1", livro1.getAutores().get(0).getNome());

        LivroDto livro2 = resultado.getLivrosImportados().get(1);
        assertEquals("LIVRO 2", livro2.getTitulo());
        assertEquals("EDITORA 2", livro2.getEditora().getNome());
        assertEquals(LocalDate.of(2017, 1, 10), livro2.getDataPublicacao());
        assertEquals(2, livro2.getAutores().size());
        assertEquals("AUTOR 2", livro2.getAutores().get(0).getNome());
        assertEquals("AUTOR 1", livro2.getAutores().get(1).getNome());
    }

    @Test
    public void quandoCsv_possuirLinhaComErro_deveAdicionarErroNaLista() throws Exception {
        // Arrange
        File arquivo = tempFolder.newFile("teste.csv");
        String conteudo = new StringBuilder()
                .append("ISBN,Titulo,Editora,Data_Publicacao,Autor")
                .append("\n")
                .append("1111111111111,livro 1,editora 1,15/08/2009,autor") // livro correto
                .append("\n")
                .append("2222222222222,editora 3,10/01/2017,autor 3;autor 4") // possui campo a menos
                .append("\n")
                .append("3333333333333,livro 3,editora 1,10/01/2017,autor 2;autor1,campo extra") // possui campo a mais
                .append("\n")
                .append("invalido,livro 2,editora 2,10/01/2017,autor") // isbn invalido
                .append("\n")
                .append("4444444444444,,editora 2,10/01/2017,autor") // sem titulo
                .append("\n")
                .append("5555555555555,livro 1,,10/01/2017,autor") // sem editora
                .append("\n")
                .append("6666666666666,livro 1,editora 2,,autor") // sem data
                .append("\n")
                .append("7777777777777,livro 1,editora 2,101/10/2025,autor") // data invalida
                .append("\n")
                .append("1111111111111,livro 1,editora 1,15/08/2009,autor") // livro correto
                .append("\n")
                .append("8888888888888,livro 1,editora 2,2025-10-01,autor") // data invalida
                .append("\n")
                .append("9999999999999,livro 1,editora 2,10/01/2017,") // sem autores
                .toString();

        escreverArquivo(arquivo, conteudo);

        // Act
        DadosImportacaoCsvDto resultado = livroCsvService.processarCSV(arquivo);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.getLivrosImportados().size());
        assertEquals(9, resultado.getErros().size());

        assertTrue(resultado.getErros().get(0).contains("Linha 3"));
        assertTrue(resultado.getErros().get(0).contains("Campo(s) não encontrado(s)."));

        assertTrue(resultado.getErros().get(1).contains("Linha 4"));
        assertTrue(resultado.getErros().get(1).contains("Foram encontrados mais campos do que o necessário"));

        assertTrue(resultado.getErros().get(2).contains("Linha 5"));
        assertTrue(resultado.getErros().get(2).contains("não é um número válido"));

        assertTrue(resultado.getErros().get(3).contains("Linha 6"));
        assertTrue(resultado.getErros().get(3).contains("Título não encontrado"));

        assertTrue(resultado.getErros().get(4).contains("Linha 7"));
        assertTrue(resultado.getErros().get(4).contains("Editora não encontrada"));

        assertTrue(resultado.getErros().get(5).contains("Linha 8"));
        assertTrue(resultado.getErros().get(5).contains("Data de publicação não informada"));

        assertTrue(resultado.getErros().get(6).contains("Linha 9"));
        assertTrue(resultado.getErros().get(6).contains("Formato de data inválido"));

        assertTrue(resultado.getErros().get(7).contains("Linha 11"));
        assertTrue(resultado.getErros().get(7).contains("Formato de data inválido"));

        assertTrue(resultado.getErros().get(8).contains("Linha 12"));
        assertTrue(resultado.getErros().get(8).contains("Pelo menos um autor"));
    }

    @Test
    public void quandoCsv_possuirLinhasVazias_deveIgnorarLinhasVazias() throws Exception {
        // Arrange
        File arquivo = tempFolder.newFile("teste.csv");
        String conteudo = new StringBuilder()
                .append("ISBN,Titulo,Editora,Data_Publicacao,Autor")
                .append("\n").append(" ").append("\n")
                .append("1111111111111,livro 1,editora 1,15/08/2009,autor 1")
                .append("\n").append("   ").append("\n")
                .append("2222222222222,livro 2,editora 2,10/01/2017,autor 2")
                .toString();

        escreverArquivo(arquivo, conteudo);

        // Act
        DadosImportacaoCsvDto resultado = livroCsvService.processarCSV(arquivo);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.getLivrosImportados().size());
        assertTrue(resultado.getErros().isEmpty());
    }

    @Test
    public void quandoCsv_forCamposComAspasEVirgulas_deveProcessarCorretamente() throws Exception {
        // Arrange
        File arquivo = tempFolder.newFile("teste.csv");
        String conteudo = new StringBuilder()
                .append("ISBN,Titulo,Editora,Data_Publicacao,Autor")
                .append("\n")
                .append("1111111111111,\"Livro 1: Algum subtitulo\",\"editora\",15/08/2009,\"autor\"")
                .toString();

        escreverArquivo(arquivo, conteudo);

        // Act
        DadosImportacaoCsvDto resultado = livroCsvService.processarCSV(arquivo);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getLivrosImportados().size());
        assertTrue(resultado.getErros().isEmpty());

        LivroDto livro = resultado.getLivrosImportados().get(0);
        assertEquals("LIVRO 1: ALGUM SUBTITULO", livro.getTitulo());
        assertEquals("EDITORA", livro.getEditora().getNome());
        assertEquals("AUTOR", livro.getAutores().get(0).getNome());
    }

    private void escreverArquivo(File arquivo, String conteudo) throws IOException {
        try (FileWriter writer = new FileWriter(arquivo)) {
            writer.write(conteudo);
        }
    }
}