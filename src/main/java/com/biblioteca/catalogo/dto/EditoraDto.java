package com.biblioteca.catalogo.dto;

import com.biblioteca.catalogo.entity.Livro;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditoraDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "editora_id")
    private Long editoraId;

    private String nome;

    @OneToMany(mappedBy = "editora")
    private Set<Livro> livros = new HashSet<>();
}
