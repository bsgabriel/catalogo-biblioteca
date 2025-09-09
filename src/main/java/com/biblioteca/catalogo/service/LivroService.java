package com.biblioteca.catalogo.service;

import com.biblioteca.catalogo.dto.AutorDto;
import com.biblioteca.catalogo.dto.EditoraDto;
import com.biblioteca.catalogo.dto.LivroDto;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class LivroService {
    public List<LivroDto> buscarTodos() {
        try {
            Thread.sleep(new Random().nextInt(5) * 1000);
        } catch (InterruptedException e) {
            log.error("Erro ao aguardar", e);
        }
        List<LivroDto> list = new ArrayList<>();
        for(long i = 0; i < 30; i++){
            list.add( LivroDto.builder()
                    .livroId(i + 1)
                    .titulo("Senhos dos anéis")
                    .isbn(1234569L)
                    .dataPublicacao(LocalDate.of(2020, 10, 13))
                    .editora(EditoraDto.builder()
                            .editoraId(1234L)
                            .nome("HarperCollins Brasil")
                            .build())
                    .autores(Stream.of(AutorDto.builder()
                                    .autorId(1234L)
                                    .nome("JRR Tolkien")
                                    .build())
                            .collect(Collectors.toList()))
                    .build());
        }

        return list;
    }

    public List<LivroDto> buscarPorTermo(String termo) {
        return new ArrayList<>();
    }

    public void deletar(Long livroId) {
    }

    public List<LivroDto> importarArquivo(String caminho) {
        return new ArrayList<>();
    }

    public LivroDto salvar(LivroDto livro) {
        return null;
    }

}
