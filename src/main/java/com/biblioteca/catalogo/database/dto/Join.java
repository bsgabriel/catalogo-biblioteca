package com.biblioteca.catalogo.database.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.criteria.JoinType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Join {

    private String coluna;

    @Builder.Default
    private JoinType tipo = JoinType.LEFT;
}
