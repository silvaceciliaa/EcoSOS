CREATE TABLE ENDERECO (
    ID_ENDERECO NUMBER(38,0) NOT NULL,
    ESTADO CHAR(2) CONSTRAINT check_tipo_estado CHECK (ESTADO IN ('SC', 'RS', 'PR')) NOT NULL,
    CIDADE VARCHAR2(50) NOT NULL,
    BAIRRO VARCHAR2(50) NOT NULL,
    CEP VARCHAR2(9) UNIQUE NOT NULL,
    CRIADO_EM TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    ATUALIZADO_EM TIMESTAMP,
    PRIMARY KEY (ID_ENDERECO)
);

CREATE TABLE USUARIO (
    ID_USUARIO NUMBER(38, 0) NOT NULL,
    ID_ENDERECO NUMBER(38, 0) NOT NULL,
    NOME VARCHAR2(100) NOT NULL,
    TELEFONE VARCHAR2(11) UNIQUE NOT NULL,
    EMAIL VARCHAR2(100) UNIQUE NOT NULL,
    SENHA VARCHAR2(200) NOT NULL,
    NIVEL_ACESSO CHAR(1) CONSTRAINT check_acesso CHECK (NIVEL_ACESSO IN ('A', 'C')) NOT NULL,
    CRIADO_EM TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    ATUALIZADO_EM TIMESTAMP,
    PRIMARY KEY (ID_USUARIO),
    FOREIGN KEY (ID_ENDERECO) REFERENCES ENDERECO (ID_ENDERECO)
);

CREATE TABLE COMENTARIO (
    ID_COMENTARIO NUMBER(38, 0) NOT NULL,
    ID_USUARIO NUMBER(38, 0) NOT NULL,
    CONTEUDO VARCHAR2(500) NOT NULL,
    CRIADO_EM TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    ATUALIZADO_EM TIMESTAMP,
    CONSTRAINT PK_COMENTARIO PRIMARY KEY (ID_COMENTARIO),
    CONSTRAINT FK_USUARIO FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO(ID_USUARIO)
);

CREATE TABLE OCORRENCIA (
    ID_OCORRENCIA NUMBER(38, 0) NOT NULL,
    ID_ENDERECO NUMBER(38, 0) NOT NULL,
    ID_USUARIO NUMBER(38, 0) NOT NULL,
    NOME VARCHAR2(50) NOT NULL,
    GRAVIDADE VARCHAR2(50) NOT NULL,
    DESCRICAO VARCHAR2(255) NOT NULL,
    TIPO VARCHAR2(20) NOT NULL,
    STATUS CHAR(1) CHECK(STATUS IN('0','1')) NOT NULL,
    CRIADO_EM TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    ATUALIZADO_EM DEFAULT,
    CONSTRAINT PK_OCORRENCIA PRIMARY KEY (ID_OCORRENCIA),
    FOREIGN KEY (ID_ENDERECO) REFERENCES ENDERECO (ID_ENDERECO),
    FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO (ID_USUARIO)
);

CREATE TABLE COMENTARIO_OCORRENCIA (
    ID_COMENTARIO NUMBER(38, 0) NOT NULL,
    ID_OCORRENCIA NUMBER(38, 0) NOT NULL,
    CONSTRAINT PK_OCORRENCIA_USUARIO PRIMARY KEY (ID_COMENTARIO, ID_OCORRENCIA),
    CONSTRAINT FK_COMENTARIO FOREIGN KEY (ID_COMENTARIO) REFERENCES COMENTARIO (ID_COMENTARIO),
    CONSTRAINT FK_OCORRENCIA FOREIGN KEY (ID_OCORRENCIA) REFERENCES OCORRENCIA (ID_OCORRENCIA)
);

CREATE TABLE REACAO (
    ID_USUARIO NUMBER(38, 0) NOT NULL,
    ID_OCORRENCIA NUMBER(38, 0) NOT NULL,
    CRIADO_EM TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    TIPO_REACAO CHAR(1) CONSTRAINT check_tipo_reacao CHECK (TIPO_REACAO IN ('D', 'L')),
    ATUALIZADO_EM TIMESTAMP,
    CONSTRAINT PK_USUARIO_REACAO PRIMARY KEY (ID_USUARIO, ID_OCORRENCIA),
    FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO (ID_USUARIO),
    FOREIGN KEY (ID_OCORRENCIA) REFERENCES OCORRENCIA (ID_OCORRENCIA)
);

CREATE TABLE NOTIFICACAO (
    ID_NOTIFICACAO NUMBER(38, 0) NOT NULL,
    ID_OCORRENCIA NUMBER(38, 0) NOT NULL,
    ID_USUARIO NUMBER (38, 0) NOT NULL,
    CORPO VARCHAR2 (500) NOT NULL,
    PRIMARY KEY (ID_NOTIFICACAO),
    FOREIGN KEY (ID_OCORRENCIA) REFERENCES OCORRENCIA (ID_OCORRENCIA),
    FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO (ID_USUARIO)
);

CREATE SEQUENCE SEQ_COMENTARIO
    START WITH 1
    INCREMENT BY 1
    NOCACHE NOCYCLE;

CREATE SEQUENCE SEQ_ENDERECO
    START WITH 1
    INCREMENT BY 1
    NOCACHE NOCYCLE;

CREATE SEQUENCE SEQ_OCORRENCIA
    START WITH 1
    INCREMENT BY 1
    NOCACHE NOCYCLE;

CREATE SEQUENCE SEQ_USUARIO
    START WITH 1
    INCREMENT BY 1
    NOCACHE NOCYCLE;

CREATE SEQUENCE SEQ_NOTIFICACAO
    START WITH 1
    INCREMENT BY 1
    NOCACHE NOCYCLE;



