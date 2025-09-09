package com.biblioteca.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LivroDto {

    private Long livroId;
    private String titulo;

    @Builder.Default
    private List<AutorDto> autores = new ArrayList<>();
    private LocalDate dataPublicacao;
    private Long isbn;
    private EditoraDto editora;
}
