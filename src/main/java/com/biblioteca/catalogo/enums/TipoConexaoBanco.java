package com.biblioteca.catalogo.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoConexaoBanco {

    POSTGRES("biblioteca-postgres", "/liquibase.properties"),
    H2_MEMORIA("biblioteca-memoria", "/liquibase-memoria.properties"),
    H2_ARQUIVO("biblioteca-arquivo", "/liquibase-arquivo.properties");

    private final String unidadePersistencia;
    private final String pathLiquibaseProperties;
}
