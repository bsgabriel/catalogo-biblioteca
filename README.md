# Catálogo Biblioteca

Sistema de catálogo para gerenciamento de livros, feito com Java 8.

## Tecnologias

### UI
- Swing
- JGoodies
- LGoodDatePicker

### Banco de Dados
- Hibernate
- JPA
- PostgreSQL:
- H2: 
- Liquibase

### Utilidades
- Lombok

### Logging
- Slf4j
- Logback

### Integração com API
- Retrofit
- Jackson
- Jackson-datatype

### Testes Unitários
- JUnit
- Mockito

### Gerenciador de Dependências
- Maven

## Instalação e Uso

### Configuração do banco (caso for usar com postgres)

Para o postgres, a aplicação usa o schema `biblioteca`. Também deve ser criado o usuário do liquibase: `biblioteca_liquibase`. Isso pode ser feito através do seguinte script:

```sql
CREATE USER biblioteca_liquibase WITH PASSWORD 'biblioteca_liquibase' NOSUPERUSER NOCREATEDB NOCREATEROLE INHERIT LOGIN;
CREATE SCHEMA biblioteca AUTHORIZATION biblioteca_liquibase;
GRANT ALL ON SCHEMA biblioteca TO biblioteca_liquibase;
```

### Build

A aplicação usa um maven wrapper, então não há necessidade de instalar o Maven. Para gerar um jar com todas as dependências, execute:

```bash
git clone https://github.com/bsgabriel/catalogo-biblioteca.git
cd catalogo-biblioteca

./mvnw clean package
```

### Execução

Para rodar a aplicação usando H2 em memória (padrão), basta rodar:
```bash
java -jar target/biblioteca-catalogo.jar
```

Para rodar usando Postgres:
```bash
java -Ddatabase.type=postgres -jar target/biblioteca-catalogo.jar
```

Para rodar usando H2 com persistência em arquivos:
```bash
java -Ddatabase.type=arquivo -jar target/biblioteca-catalogo.jar
```

### Arquivos de exemplo

Deixei arquivos de exemplo para os 3 cenários:
- Apenas imports válidos: [csv_exemplos/validos.csv](csv_exemplos/validos.csv)
- Apenas imports inválidos: [csv_exemplos/invalidos.csv](csv_exemplos/invalidos.csv)
- Imports válidos e inválidos: [csv_exemplos/validos_e_invalidos.csv](csv_exemplos/validos_e_invalidos.csv)