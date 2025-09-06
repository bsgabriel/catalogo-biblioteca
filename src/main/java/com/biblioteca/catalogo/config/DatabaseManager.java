package com.biblioteca.catalogo.config;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

@Slf4j
@NoArgsConstructor
public class DatabaseManager {

    private static DatabaseManager instance;
    private static boolean bancoInicializado;

    private Properties dbProperties;

    @Getter
    private EntityManagerFactory entityManagerFactory;

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void inicializarBanco() {
        if (bancoInicializado) {
            log.warn("Banco já foi inicializado, processo será ignorado");
            return;
        }

        try {
            carregarProperties();
            executarLiquibase();
            iniciarHibernate();
            log.info("Inicialização concluída com sucesso");
            bancoInicializado = true;
        } catch (Exception e) {
            // TODO: criar exception personalizada
            throw new RuntimeException("Erro ao iniciar o banco de dados", e);
        }
    }

    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    private void carregarProperties() throws Exception {
        dbProperties = new Properties();
        try (InputStream arquivo = getClass().getResourceAsStream("/liquibase.properties")) {
            dbProperties.load(arquivo);
        }
    }

    private void executarLiquibase() throws Exception {
        Connection conn = DriverManager.getConnection(
                dbProperties.getProperty("url"),
                dbProperties.getProperty("username"),
                dbProperties.getProperty("password")
        );

        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(conn));

        String defaultSchema = dbProperties.getProperty("defaultSchemaName");
        database.setDefaultSchemaName(defaultSchema);

        Liquibase liquibase = new Liquibase(
                dbProperties.getProperty("changeLogFile"),
                new ClassLoaderResourceAccessor(),
                database
        );

        liquibase.update(new Contexts(), new LabelExpression());
        conn.close();
    }

    private void iniciarHibernate() {
        entityManagerFactory = Persistence.createEntityManagerFactory("biblioteca");
    }

    @Getter
    private static class DadosBanco {
        private String url;
        private String usuario;
        private String senha;
        private String defaultSchema;
        private String arquivoChangelog;
    }
}