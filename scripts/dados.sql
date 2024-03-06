INSERT INTO ENDERECO (ID_ENDERECO, ESTADO, CIDADE, BAIRRO, CEP) VALUES (
                                                                           SEQ_ENDERECO.nextval, 'SC', 'Urussanga', 'Centro', '88840-000');

INSERT INTO ENDERECO (ID_ENDERECO, ESTADO, CIDADE, BAIRRO, CEP) VALUES (
                                                                           SEQ_ENDERECO.nextval, 'PR', 'Curitiba', 'Centro', '58413-000');

INSERT INTO ENDERECO (ID_ENDERECO, ESTADO, CIDADE, BAIRRO, CEP) VALUES (
                                                                           SEQ_ENDERECO.nextval, 'RS', 'Porto Alegre', 'Bairro 2', '69851-000');

INSERT INTO USUARIO (ID_USUARIO, ID_ENDERECO, NOME,
                     TELEFONE, EMAIL, SENHA,
                     NIVEL_ACESSO)
VALUES (SEQ_USUARIO.nextval, 1, 'João Paulo Sigieski Bonetti', '83996883901', 'joaopaulobonetti2004@gmail.com',
        '123456', 'A');

INSERT INTO USUARIO (ID_USUARIO, ID_ENDERECO, NOME,
                     TELEFONE, EMAIL, SENHA,
                     NIVEL_ACESSO)
VALUES (SEQ_USUARIO.nextval, 2, 'Julia Sigieski Bonetti', '83965142691', 'julia@gmail.com',
        '123456', 'C');

INSERT INTO USUARIO (ID_USUARIO, ID_ENDERECO, NOME,
                     TELEFONE, EMAIL, SENHA,
                     NIVEL_ACESSO)
VALUES (SEQ_USUARIO.nextval, 3, 'Amaral', '98563214587', 'amaral@gmail.com',
        '654321', 'C');

INSERT INTO OCORRENCIA (ID_OCORRENCIA, ID_ENDERECO, ID_USUARIO, NOME, GRAVIDADE,
                        DESCRICAO, TIPO)
VALUES (SEQ_OCORRENCIA.nextval, 1, 1, 'Ocorrencia 1', 'A', 'Descrição', 'Queimada');

INSERT INTO OCORRENCIA (ID_OCORRENCIA, ID_ENDERECO, ID_USUARIO, NOME, GRAVIDADE,
                        DESCRICAO, TIPO)
VALUES (SEQ_OCORRENCIA.nextval, 2, 2, 'Ocorrencia 2', 'M', 'Descrição 2', 'Enchente');

INSERT INTO OCORRENCIA (ID_OCORRENCIA, ID_ENDERECO, ID_USUARIO, NOME, GRAVIDADE,
                        DESCRICAO, TIPO)
VALUES (SEQ_OCORRENCIA.nextval, 3, 3, 'Ocorrencia 1', 'B', 'Descrição 3', 'Deslizamento');

INSERT INTO NOTIFICACAO (ID_NOTIFICACAO, ID_OCORRENCIA, ID_USUARIO, CORPO) VALUES (
                                                                                      SEQ_NOTIFICACAO.nextval, 1 , 1, 'Notificacao');

INSERT INTO NOTIFICACAO (ID_NOTIFICACAO, ID_OCORRENCIA, ID_USUARIO, CORPO) VALUES (
                                                                                      SEQ_NOTIFICACAO.nextval, 2 , 2, 'Notificacao 2');

INSERT INTO NOTIFICACAO (ID_NOTIFICACAO, ID_OCORRENCIA, ID_USUARIO, CORPO) VALUES (
                                                                                      SEQ_NOTIFICACAO.nextval, 3 , 3, 'Notificacao 3');

INSERT INTO COMENTARIO (ID_COMENTARIO, ID_USUARIO, CONTEUDO) VALUES (
                                                                        SEQ_COMENTARIO.nextval, 1, 'Conteudo');

INSERT INTO COMENTARIO (ID_COMENTARIO, ID_USUARIO, CONTEUDO) VALUES (
                                                                        SEQ_COMENTARIO.nextval, 2, 'Conteudo 2');

INSERT INTO COMENTARIO (ID_COMENTARIO, ID_USUARIO, CONTEUDO) VALUES (
                                                                        SEQ_COMENTARIO.nextval, 3, 'Conteudo 3');

INSERT INTO COMENTARIO_OCORRENCIA (ID_COMENTARIO, ID_OCORRENCIA) VALUES (1, 1);

INSERT INTO COMENTARIO_OCORRENCIA (ID_COMENTARIO, ID_OCORRENCIA) VALUES (2, 2);

INSERT INTO COMENTARIO_OCORRENCIA (ID_COMENTARIO, ID_OCORRENCIA) VALUES (3, 3);

INSERT INTO REACAO (ID_USUARIO, ID_OCORRENCIA, TIPO_REACAO) VALUES (
                                                                       1, 1, 'D');

INSERT INTO REACAO (ID_USUARIO, ID_OCORRENCIA, TIPO_REACAO) VALUES (
                                                                       2, 2, 'L');

INSERT INTO REACAO (ID_USUARIO, ID_OCORRENCIA, TIPO_REACAO) VALUES (
                                                                       3, 3, 'L');