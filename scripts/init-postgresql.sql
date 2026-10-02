-- Execute no pgAdmin (Query Tool)
-- 1) Conecte no servidor PostgreSQL (banco "postgres")
-- 2) Rode o bloco CREATE DATABASE (sozinho)
-- 3) Conecte no banco "coffeshop" e rode o restante

-- Criar o banco (rode isolado; no pgAdmin: botão direito no Databases > Query Tool)
CREATE DATABASE coffeshop
    WITH OWNER = postgres
         ENCODING = 'UTF8'
         TEMPLATE = template0;

-- Conecte em coffeshop antes de continuar -------------------------------

-- Se a tabela já existir com BIGSERIAL, rode antes:
-- DROP TABLE IF EXISTS usuarios;

CREATE TABLE IF NOT EXISTS usuarios (
    id              UUID PRIMARY KEY,
    nome            VARCHAR(255) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    data_criacao    TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_usuarios_email ON usuarios (email);

CREATE TABLE IF NOT EXISTS lojas (
    id              UUID PRIMARY KEY,
    nome            VARCHAR(255) NOT NULL,
    endereco        VARCHAR(255) NOT NULL,
    telefone        VARCHAR(255) NOT NULL,
    usuario_id      UUID NOT NULL,
    data_criacao    TIMESTAMP NOT NULL,
    CONSTRAINT fk_lojas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);
