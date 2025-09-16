package com.biblioteca.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DadosImportacaoCsvDto {

    @Builder.Default
    private List<LivroDto> livrosImportados = new ArrayList<>();

    @Builder.Default
    private List<String> erros = new ArrayList<>();

    public int getTotalLinhas() {
        return livrosImportados.size() + erros.size();
    }

}
