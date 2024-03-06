package br.com.dbc.vemser.ecososapi.ecosos.service;

import br.com.dbc.vemser.ecososapi.ecosos.dto.comentario.ComentarioCreateDTO;
import br.com.dbc.vemser.ecososapi.ecosos.dto.comentario.ComentarioDTO;
import br.com.dbc.vemser.ecososapi.ecosos.dto.comentario.ComentarioUpdateDTO;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Comentario;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Ocorrencia;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Usuario;
import br.com.dbc.vemser.ecososapi.ecosos.exceptions.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.ecososapi.ecosos.exceptions.PermissaoNegadaException;
import br.com.dbc.vemser.ecososapi.ecosos.repository.ComentarioRepository;
import br.com.dbc.vemser.ecososapi.ecosos.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ComentarioService - Test")
class ComentarioServiceTest {

    @InjectMocks
    private ComentarioService comentarioService;
    @Mock
    private ComentarioRepository comentarioRepository;
    @Mock
    private UsuarioComumService usuarioComumService;
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private OcorrenciaService ocorrenciaService;
    @Mock
    private ObjectMapper objectMapper;

    ComentarioCreateDTO comentarioCreateDTOMock;
    Comentario comentarioMock;

    ComentarioDTO comentarioDTOMock;

    ComentarioUpdateDTO comentarioUpdateDTOMock;
    Usuario usuarioExistente;
    Ocorrencia ocorrenciaExistente;

    @BeforeEach
    void setUp() {
        comentarioCreateDTOMock = new ComentarioCreateDTO();
        comentarioCreateDTOMock.setConteudo("Situação complicada, população desesperada!");
        comentarioCreateDTOMock.setIdOcorrencia(1);
        comentarioCreateDTOMock.setIdUsuario(1);

        usuarioExistente = new Usuario();
        usuarioExistente.setIdUsuario(1);

        ocorrenciaExistente = new Ocorrencia();
        ocorrenciaExistente.setIdOcorrencia(1);

        comentarioMock = new Comentario();
        comentarioMock.setIdComentario(1);
        comentarioMock.setIdUsuario(1);
        comentarioMock.setConteudo("Situação complicada, população desesperada!");
        comentarioMock.setCriadoEm(new Timestamp(System.currentTimeMillis()));
        comentarioMock.setIdOcorrencia(1);
        comentarioMock.setUsuario(usuarioExistente);
        comentarioMock.setOcorrencia(ocorrenciaExistente);

        comentarioDTOMock = new ComentarioDTO();
        comentarioDTOMock.setIdComentario(1);
        comentarioDTOMock.setConteudo("Situação complicada, população desesperada!");

        comentarioUpdateDTOMock = new ComentarioUpdateDTO();
        comentarioUpdateDTOMock.setConteudo("Situação complicada, população desesperada!");
    }

    @Test
    @DisplayName("Deveria criar comentário com sucesso")
    void deveriaCriarComentarioComSucesso() throws Exception {
        Optional<Usuario> usuarioOptional = Optional.of(usuarioExistente);
        when(usuarioRepository.findById(usuarioExistente.getIdUsuario())).thenReturn(usuarioOptional);
        when(comentarioService.converterDTO(comentarioCreateDTOMock)).thenReturn(comentarioMock);
        when(ocorrenciaService.getOcorrencia(ocorrenciaExistente.getIdOcorrencia())).thenReturn(ocorrenciaExistente);
        when(usuarioComumService.returnUsuario(usuarioExistente.getIdUsuario())).thenReturn(usuarioExistente);
        when(comentarioRepository.save(comentarioMock)).thenReturn(comentarioMock);
        when(comentarioService.retornarDTO(comentarioMock)).thenReturn(comentarioDTOMock);

        ComentarioDTO comentarioDTOCriado = comentarioService.adicionar(ocorrenciaExistente.getIdOcorrencia(), usuarioExistente.getIdUsuario(), comentarioCreateDTOMock);

        assertNotNull(comentarioDTOCriado);
        verify(comentarioRepository).save(comentarioMock);
        assertEquals(comentarioDTOMock, comentarioDTOCriado);
    }
    @Test
    @DisplayName("Deveria Retornar Exception Usuario Não Existe Ao Tentar Criar Comentário Com Id Inexistente")
    void deveriaRetornarExceptionUsuarioNaoExisteAoTentarCriarComentarioComIdInexistente() {
        when(usuarioRepository.findById(usuarioExistente.getIdUsuario())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> comentarioService.adicionar(ocorrenciaExistente.getIdOcorrencia(), usuarioExistente.getIdUsuario(), comentarioCreateDTOMock));
    }

    @Test
    @DisplayName("Deveria Atualizar Com Sucesso Com Usuario Admin")
    void deveriaAtualizarComSucessoComUsuarioAdmin() throws Exception {
        when(comentarioRepository.findById(comentarioMock.getIdComentario())).thenReturn(Optional.of(comentarioMock));
        when(comentarioRepository.save(comentarioMock)).thenReturn(comentarioMock);
        when(comentarioService.retornarDTO(comentarioMock)).thenReturn(comentarioDTOMock);
        ComentarioDTO comentarioDTOAtualizado = comentarioService.atualizarParaAdmin(comentarioMock.getIdComentario(), comentarioUpdateDTOMock);
        assertNotNull(comentarioDTOAtualizado);
        verify(comentarioRepository).save(comentarioMock);
        assertEquals(comentarioDTOMock, comentarioDTOAtualizado);
    }

    @Test
    @DisplayName("Deveria Atualizar Com Sucesso Com Usuario Comum")
    void deveriaAtualizarComSucessoComUsuarioComum() throws Exception {
        when(usuarioComumService.getIdLoggedUser()).thenReturn(usuarioExistente.getIdUsuario());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "usuario",
                "senha",
                AuthorityUtils.createAuthorityList("COMUM")
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(comentarioRepository.findById(comentarioMock.getIdComentario())).thenReturn(Optional.of(comentarioMock));
        when(comentarioRepository.save(comentarioMock)).thenReturn(comentarioMock);
        when(comentarioService.retornarDTO(comentarioMock)).thenReturn(comentarioDTOMock);
        ComentarioDTO comentarioDTOAtualizado = comentarioService.atualizarParaComum(comentarioMock.getIdComentario(), comentarioUpdateDTOMock);
        verify(comentarioRepository).save(comentarioMock);
        assertEquals(comentarioDTOMock, comentarioDTOAtualizado);
    }

    @Test
    @DisplayName("Deveria Retornar Exception Ao Tentar Atualizar Com Usuario Comum Comentario Alheio")
    void deveriaRetornarExceptionAoTentarAtualizarComUsuarioComumComentarioAlheio() {
        when(usuarioComumService.getIdLoggedUser()).thenReturn(2);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "usuario",
                "senha",
                AuthorityUtils.createAuthorityList("COMUM")
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(comentarioRepository.findById(comentarioMock.getIdComentario())).thenReturn(Optional.of(comentarioMock));
        assertThrows(PermissaoNegadaException.class, () -> comentarioService.atualizarParaComum(comentarioMock.getIdComentario(), comentarioUpdateDTOMock));
    }

    @Test
    @DisplayName("Deveria Listar Comentários")
    void deveriaPaginarComentarios() {
        List<Comentario> comentarioList = List.of(comentarioMock);
        Page<Comentario> comentarioPaginado = new PageImpl<>(comentarioList, PageRequest.of(0, 10), comentarioList.size());
        when(comentarioRepository.findAll(Pageable.unpaged())).thenReturn(comentarioPaginado);

        Page<Comentario> retorno = comentarioService.listar(Pageable.unpaged());

        assertEquals(comentarioPaginado, retorno);
    }

    @Test
    @DisplayName("Deveria Listar Comentários Por Id da Ocorrência")
    void deveriaRetornarComentarioPorIdOcorrencia() throws Exception {
        when(comentarioRepository.findByIdOcorrencia(ocorrenciaExistente.getIdOcorrencia())).thenReturn(List.of(comentarioMock));
        when(comentarioService.retornarDTO(comentarioMock)).thenReturn(comentarioDTOMock);
        List<ComentarioDTO> retorno = comentarioService.listarPorComentario(ocorrenciaExistente.getIdOcorrencia());
        assertEquals(List.of(comentarioDTOMock), retorno);
    }

    @Test
    @DisplayName("Deveria Deletar Com Sucesso Com Usuario Admin")
    void deveriaDeletarComSucessoComUsuarioAdmin() throws Exception {
        when(comentarioRepository.findById(anyInt())).thenReturn(Optional.of(comentarioMock));
        comentarioService.removerParaAdmin(anyInt());
        verify(comentarioRepository).delete(comentarioMock);
    }

    @Test
    @DisplayName("Deveria Deletar Com Sucesso Com Usuario Comum")
    void deveriaDeletarComSucessoComUsuarioComum() throws Exception {
        when(usuarioComumService.getIdLoggedUser()).thenReturn(usuarioExistente.getIdUsuario());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "usuario",
                "senha",
                AuthorityUtils.createAuthorityList("COMUM")
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(comentarioRepository.findById(anyInt())).thenReturn(Optional.of(comentarioMock));
        comentarioService.removerParaComum(anyInt());
        verify(comentarioRepository).delete(comentarioMock);
    }

    @Test
    @DisplayName("Deveria Retornar Exception Ao Tentar Deletar Com Usuario Comum Comentario Alheio")
    void deveriaRetornarExceptionAoTentarDeletarComUsuarioComumComentarioAlheio() {
        when(usuarioComumService.getIdLoggedUser()).thenReturn(2);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "usuario",
                "senha",
                AuthorityUtils.createAuthorityList("COMUM")
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(comentarioRepository.findById(comentarioMock.getIdComentario())).thenReturn(Optional.of(comentarioMock));
        assertThrows(PermissaoNegadaException.class, () -> comentarioService.removerParaComum(comentarioMock.getIdComentario()));
    }

    @Test
    void deveriaRetornarComentario() throws Exception {
        Optional<Comentario> comentarioOptional = Optional.of(comentarioMock);
        when(comentarioRepository.findById(comentarioMock.getIdComentario())).thenReturn(comentarioOptional);
        Comentario retorno = comentarioService.returnComentario(comentarioMock.getIdComentario());
        assertEquals(comentarioMock, retorno);
    }

    @Test
    void deveriaRetornarComentarioException() {
        when(comentarioRepository.findById(comentarioMock.getIdComentario())).thenReturn(Optional.empty());
        assertThrows(EntidadeNaoEncontradaException.class, () -> comentarioService.returnComentario(comentarioMock.getIdComentario()));
    }

    @Test
    void converterDTO() {
        when(objectMapper.convertValue(comentarioCreateDTOMock, Comentario.class)).thenReturn(comentarioMock);
        Comentario retorno = comentarioService.converterDTO(comentarioCreateDTOMock);
        assertEquals(comentarioMock, retorno);
    }

    @Test
    void retornarDTO() {
        when(objectMapper.convertValue(comentarioMock, ComentarioDTO.class)).thenReturn(comentarioDTOMock);
        ComentarioDTO retorno = comentarioService.retornarDTO(comentarioMock);
        assertEquals(comentarioDTOMock, retorno);
    }

    @Test
    void retornarUpdateDTO() {
        when(objectMapper.convertValue(comentarioMock, ComentarioUpdateDTO.class)).thenReturn(comentarioUpdateDTOMock);
        ComentarioUpdateDTO retorno = comentarioService.retornarUpdateDTO(comentarioMock);
        assertEquals(comentarioUpdateDTOMock, retorno);
    }
}