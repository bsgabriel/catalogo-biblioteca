package com.biblioteca.catalogo.service;

import com.biblioteca.catalogo.api.OpenLibraryApi;
import com.biblioteca.catalogo.dto.openlibrary.AutorResponseDto;
import com.biblioteca.catalogo.dto.openlibrary.KeyDto;
import com.biblioteca.catalogo.dto.openlibrary.LivroResponseDto;
import com.biblioteca.catalogo.exception.ApiExecutionException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.time.LocalDate;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
public class OpenLibraryService {

    private final OpenLibraryApi openLibraryApi;

    public OpenLibraryService() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.addHandler(new DeserializationProblemHandler() {
            @Override
            public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType, String valueToConvert, String failureMsg) throws IOException {
                if (targetType != LocalDate.class || isBlank(valueToConvert)) {
                    return super.handleWeirdStringValue(ctxt, targetType, valueToConvert, failureMsg);
                }
                log.warn("A data '{}' é inválida para o campo '{}'. Atributo será ignorado.", valueToConvert, ctxt.getParser().currentName());
                return null;
            }
        });
        this.openLibraryApi = new Retrofit.Builder()
                .baseUrl("https://openlibrary.org")
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build()
                .create(OpenLibraryApi.class);
    }

    /**
     * Busca dados básicos do livro por ISBN
     */
    public LivroResponseDto buscarLivro(String isbn) throws ApiExecutionException {
        Response<LivroResponseDto> response = null;
        try {
            response = openLibraryApi.buscarLivro(isbn).execute();
        } catch (IOException e) {
            throw new ApiExecutionException("Ocorreu um erro ao buscar dados do livro", e);
        }

        if (!response.isSuccessful()) {
            if (response.code() == 404) {
                return null;
            }

            String msg = String.format("Consulta retornou código %d", response.code());
            throw new ApiExecutionException(msg);
        }

        return response.body();
    }

    public AutorResponseDto buscarAutor(KeyDto autor) throws ApiExecutionException {
        String[] arr = autor.getKey().split("/");
        String idAutor = arr[arr.length - 1];
        Response<AutorResponseDto> response = null;
        try {
            response = openLibraryApi.buscarAutor(idAutor).execute();
        } catch (IOException e) {
            throw new ApiExecutionException("Ocorreu um erro ao buscar dados do autor", e);
        }

        if (!response.isSuccessful()) {
            String msg = String.format("%d - %s", response.code(), response.message());
            throw new ApiExecutionException(msg);
        }

        if (isNull(response.body())) {
            throw new ApiExecutionException("Autor não encontrado");
        }

        return response.body();
    }

}
