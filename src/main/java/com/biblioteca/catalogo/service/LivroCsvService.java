package com.biblioteca.catalogo.service;

import com.biblioteca.catalogo.dto.AutorDto;
import com.biblioteca.catalogo.dto.DadosImportacaoCsvDto;
import com.biblioteca.catalogo.dto.EditoraDto;
import com.biblioteca.catalogo.dto.LivroDto;
import com.biblioteca.catalogo.exception.ImportacaoCsvException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
public class LivroCsvService {
    private static final String SEPARADOR_AUTORES = ";";
    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Lê um CSV e transforma em dados de importação
     *
     * @param arquivo Arquivo CSV para leitura
     * @return Um {@link DadosImportacaoCsvDto} com dados da importação
     */
    public DadosImportacaoCsvDto processarCSV(File arquivo) {
        DadosImportacaoCsvDto resultado = new DadosImportacaoCsvDto();

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            int numeroLinha = 0;
            boolean primeiraLinha = true;

            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }
                numeroLinha++;

                if (primeiraLinha && isLinhaCabecalho(linha)) {
                    primeiraLinha = false;
                    continue;
                }

                try {
                    resultado.getLivrosImportados().add(processarLinha(linha));
                } catch (ImportacaoCsvException e) {
                    log.error("Erro ao processar linha {}: {}", numeroLinha, e.getMessage());
                    resultado.getErros().add(String.format("Linha %d: %s", numeroLinha, e.getMessage()));
                }

                primeiraLinha = false;
            }

        } catch (IOException e) {
            log.error("Erro ao ler arquivo", e);
        }

        return resultado;
    }

    /**
     * Verifica se é a linha de cabeçalho
     *
     * @param linha a ser verificada
     * @return true se for cabeçalho
     */
    private boolean isLinhaCabecalho(String linha) {
        String dadosLinha = linha.toLowerCase();
        return dadosLinha.contains("isbn")
                || dadosLinha.contains("titulo")
                || dadosLinha.contains("editora")
                || dadosLinha.contains("data_publicacao")
                || dadosLinha.contains("autor");
    }

    /**
     * Processa a linha atual, gerando dados do livro
     *
     * @param linha Linha atual a ser processada
     * @return Um {@link LivroDto} com dados do livro gerados pela linha atual
     * @throws ImportacaoCsvException caso haja algum erro na geração do CSV
     */
    private LivroDto processarLinha(String linha) throws ImportacaoCsvException {
        List<String> campos = separaDadosLInha(linha);

        if (campos.size() < 5) {
            throw new ImportacaoCsvException("Campo(s) não encontrado(s). Certifique-se de informar os campos: 'ISBN, Título, Editora, Data de Publicação, Autor'");
        }

        if (campos.size() > 5) {
            throw new ImportacaoCsvException("Foram encontrados mais campos do que o necessário. Certifique-se de informar os apenas os campos: 'ISBN, Título, Editora, Data de Publicação, Autor'");
        }

        Long isbn = extrairISBN(campos.get(0).trim());
        String titulo = extrairCampoString(campos.get(1).trim(), "Título não encontrado");
        String editora = extrairCampoString(campos.get(2).trim(), "Editora não encontrada");
        LocalDate dataPublicacao = extrairDataPublicacao(campos.get(3));
        List<AutorDto> autores = extrairAutores(campos.get(4).trim());

        if (autores.isEmpty()) {
            throw new ImportacaoCsvException("Pelo menos um autor deve ser informado");
        }

        return LivroDto.builder()
                .isbn(isbn)
                .titulo(titulo)
                .autores(autores)
                .editora(EditoraDto.builder()
                        .nome(editora)
                        .build())
                .dataPublicacao(dataPublicacao)
                .build();
    }

    /**
     * Separa os dados da linha atual em uma lista de Strings
     *
     * @param linha Linha atual sendo processada, todos os dados concatenados
     * @return Uma lista com os dados separados
     */
    private List<String> separaDadosLInha(String linha) {
        List<String> campos = new ArrayList<>();
        StringBuilder campoAtual = new StringBuilder();
        boolean dentroAspas = false;
        boolean aspasEscapadas = false;

        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);

            if (aspasEscapadas) {
                campoAtual.append(c);
                aspasEscapadas = false;
                continue;
            }

            if (c == '"') {
                if (dentroAspas && i + 1 < linha.length() && linha.charAt(i + 1) == '"') {
                    aspasEscapadas = true;
                } else {
                    dentroAspas = !dentroAspas;
                }

                continue;
            }

            if (c == ',' && !dentroAspas) {
                campos.add(campoAtual.toString());
                campoAtual = new StringBuilder();
                continue;
            }

            campoAtual.append(c);
        }

        campos.add(campoAtual.toString());
        return campos;
    }

    /**
     * Tranforma a string com a data em um LocalDate
     *
     * @param strData String com a data. Formato aceito: 'dd/MM/yyyy'
     * @return Um {@link LocalDate} com a data
     * @throws ImportacaoCsvException Caso a data seja inválida
     */
    private LocalDate extrairDataPublicacao(String strData) throws ImportacaoCsvException {
        if (isBlank(strData)) {
            throw new ImportacaoCsvException("Data de publicação não informada");
        }

        LocalDate data;
        try {
            data = LocalDate.parse(strData, FORMATADOR_DATA);
        } catch (DateTimeParseException ignored) {
            data = null;
        }

        if (isNull(data)) {
            String msg = String.format("Formato de data inválido: '%s'. Use dd/MM/yyyy", strData);
            throw new ImportacaoCsvException(msg);
        }

        return data;
    }

    /**
     * Efetua o tratamento de campo de texto
     *
     * @param campo        Valor no arquivo CSV
     * @param mensagemErro Mensagem de erro caso o campo for inválido
     * @return O campo formatado em maiúsculo
     * @throws ImportacaoCsvException Caso o campo seja inválido
     */
    private String extrairCampoString(String campo, String mensagemErro) throws ImportacaoCsvException {
        if (isBlank(campo)) {
            throw new ImportacaoCsvException(mensagemErro);
        }

        return campo.toUpperCase();
    }

    /**
     * Transforma a String recebida em um Long, representando o ISBN
     *
     * @param strISBN String contendo o ISBN
     * @return O ISBN do livro convertido para {@link Long}
     * @throws ImportacaoCsvException Caso o campo seja inválido
     */
    private Long extrairISBN(String strISBN) throws ImportacaoCsvException {
        if (strISBN.isEmpty()) {
            throw new ImportacaoCsvException("ISBN não pode estar vazio");
        }

        try {
            return Long.parseLong(strISBN);
        } catch (Exception e) {
            String msg = String.format("ISBN '%s' não é um número válido", strISBN);
            throw new ImportacaoCsvException(msg);
        }
    }

    /**
     * Recebe os autores concatenados por ';' e transforma no DTO de autor
     *
     * @param campoAutor Autores concatenados por ";"
     * @return Lista {@link AutorDto}
     */
    private List<AutorDto> extrairAutores(String campoAutor) {
        List<AutorDto> autores = new ArrayList<>();

        if (campoAutor.isEmpty()) {
            return autores;
        }

        if (!campoAutor.contains(SEPARADOR_AUTORES)) {
            autores.add(AutorDto.builder()
                    .nome(campoAutor.toUpperCase())
                    .build());

            return autores;
        }

        String[] autoresSeparados = campoAutor.split(SEPARADOR_AUTORES);
        for (String autor : autoresSeparados) {
            autor = autor.trim();
            if (autor.isEmpty()) {
                continue;
            }

            autores.add(AutorDto.builder()
                    .nome(autor.toUpperCase())
                    .build());
        }

        return autores;
    }
}
