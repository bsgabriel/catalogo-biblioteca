package com.biblioteca.catalogo.api;

import com.biblioteca.catalogo.dto.openlibrary.AutorResponseDto;
import com.biblioteca.catalogo.dto.openlibrary.LivroResponseDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OpenLibraryApi {

    @GET("/isbn/{isbn}.json")
    Call<LivroResponseDto> buscarLivro(@Path("isbn") String isbn);

    @GET("/author/{idAutor}.json")
    Call<AutorResponseDto> buscarAutor(@Path("idAutor") String idAutor);
}
