package com.biblioteca.catalogo.database.config;

import com.biblioteca.catalogo.enums.TipoConexaoBanco;
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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@NoArgsConstructor
public class DatabaseManager {

    private static DatabaseManager instance;
    private static boolean bancoInicializado;

    private Properties dbProperties;
    private TipoConexaoBanco tipoBanco;

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

        this.tipoBanco = getTipoBanco();

        try {
            log.info("Inicializando banco: {}", tipoBanco.name());
            carregarProperties();
            executarLiquibase();
            iniciarHibernate();
            log.info("Inicialização concluída com sucesso para: {}", tipoBanco.name());
            bancoInicializado = true;
        } catch (Exception e) {
            log.error("Erro ao inicializar banco {}", tipoBanco.name());
            throw new RuntimeException("Erro ao iniciar o banco de dados", e);
        }
    }

    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    private void carregarProperties() throws Exception {
        dbProperties = new Properties();
        String arquivoProperties = tipoBanco.getPathLiquibaseProperties();

        try (InputStream arquivo = getClass().getResourceAsStream(arquivoProperties)) {
            if (arquivo == null) {
                throw new RuntimeException("Arquivo não encontrado: " + arquivoProperties);
            }
            dbProperties.load(arquivo);
            log.debug("Properties carregadas de: {}", arquivoProperties);
        }
    }

    private void executarLiquibase() throws Exception {
        String url = dbProperties.getProperty("url");
        String username = dbProperties.getProperty("username");
        String password = dbProperties.getProperty("password");

        log.debug("Conectando no banco: {}", url);

        Connection conn = DriverManager.getConnection(url, username, password);

        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(conn));

        String defaultSchema = dbProperties.getProperty("defaultSchemaName");
        if (isNotBlank(defaultSchema)) {
            database.setDefaultSchemaName(defaultSchema);
            log.debug("Schema padrão definido: {}", defaultSchema);
        }

        Liquibase liquibase = new Liquibase(
                dbProperties.getProperty("changeLogFile"),
                new ClassLoaderResourceAccessor(),
                database
        );

        liquibase.update(new Contexts(), new LabelExpression());
        conn.close();
    }

    private void iniciarHibernate() {
        log.debug("Iniciando EntityManagerFactory para: {}", tipoBanco.getUnidadePersistencia());
        entityManagerFactory = Persistence.createEntityManagerFactory(tipoBanco.getUnidadePersistencia());
    }

    private TipoConexaoBanco getTipoBanco() {
        String tipoDb = System.getProperty("database.type");

        if ("memoria".equals(tipoDb)) {
            return TipoConexaoBanco.H2_MEMORIA;
        }

        if ("arquivo".equals(tipoDb)) {
            return TipoConexaoBanco.H2_ARQUIVO;
        }

        return TipoConexaoBanco.POSTGRES;
    }
}