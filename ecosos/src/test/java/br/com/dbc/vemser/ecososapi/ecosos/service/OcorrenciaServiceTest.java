package br.com.dbc.vemser.ecososapi.ecosos.service;

import br.com.dbc.vemser.ecososapi.ecosos.dto.ocorrencia.OcorrenciaCreateDTO;
import br.com.dbc.vemser.ecososapi.ecosos.dto.ocorrencia.OcorrenciaDTO;
import br.com.dbc.vemser.ecososapi.ecosos.dto.ocorrencia.OcorrenciaUpdateDTO;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Endereco;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Ocorrencia;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Usuario;
import br.com.dbc.vemser.ecososapi.ecosos.enums.TipoOcorrencia;
import br.com.dbc.vemser.ecososapi.ecosos.enums.TipoRisco;
import br.com.dbc.vemser.ecososapi.ecosos.exceptions.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.ecososapi.ecosos.exceptions.PermissaoNegadaException;
import br.com.dbc.vemser.ecososapi.ecosos.exceptions.RegraDeNegocioException;
import br.com.dbc.vemser.ecososapi.ecosos.repository.OcorrenciaRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OcorrenciaService - Test")
class OcorrenciaServiceTest {

    @Mock
    private OcorrenciaRepository ocorrenciaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OcorrenciaService ocorrenciaService;

    @Mock
    private EnderecoService enderecoService;

    @Mock
    private UsuarioComumService usuarioComumService;
    @Mock
    private NotificacaoService notificacaoService;

    OcorrenciaCreateDTO ocorrenciaCreateDTOMock;
    Ocorrencia ocorrenciaMock;
    OcorrenciaDTO ocorrenciaDTOMock;

    OcorrenciaUpdateDTO ocorrenciaUpdateDTOMock;
    Usuario usuarioExistente;
    Endereco enderecoExistente;

    Character ativa;

    Character inativa;

    @BeforeEach
    void setUp() {
        ocorrenciaCreateDTOMock = new OcorrenciaCreateDTO();
        ocorrenciaCreateDTOMock.setNome("Enchente em Terra Nova");
        ocorrenciaCreateDTOMock.setIdEndereco(1);
        ocorrenciaCreateDTOMock.setIdUsuario(1);
        ocorrenciaCreateDTOMock.setGravidade(TipoRisco.ALTO);
        ocorrenciaCreateDTOMock.setDescricao("Altos níveis de água");
        ocorrenciaCreateDTOMock.setTipo(TipoOcorrencia.ENCHENTE);

        usuarioExistente = new Usuario();
        usuarioExistente.setIdUsuario(1);

        enderecoExistente = new Endereco();
        enderecoExistente.setIdEndereco(1);

        ocorrenciaMock = new Ocorrencia();
        ocorrenciaMock.setIdOcorrencia(99);
        ocorrenciaMock.setNome("Enchente em Terra Nova");
        ocorrenciaMock.setIdEndereco(1);
        ocorrenciaMock.setIdUsuario(1);
        ocorrenciaMock.setGravidade(TipoRisco.ALTO);
        ocorrenciaMock.setDescricao("Altos níveis de água");
        ocorrenciaMock.setTipo(TipoOcorrencia.ENCHENTE);
        ocorrenciaMock.setStatus('1');
        ocorrenciaMock.setCriadoEm(new Timestamp(System.currentTimeMillis()));
        ocorrenciaMock.setUsuario(usuarioExistente);
        ocorrenciaMock.setEndereco(enderecoExistente);

        ocorrenciaDTOMock = new OcorrenciaDTO();
        ocorrenciaDTOMock.setIdOcorrencia(99);
        ocorrenciaDTOMock.setNome("Enchente em Terra Nova");
        ocorrenciaDTOMock.setIdEndereco(1);
        ocorrenciaDTOMock.setIdUsuario(1);
        ocorrenciaDTOMock.setGravidade(TipoRisco.ALTO);
        ocorrenciaDTOMock.setDescricao("Altos níveis de água");
        ocorrenciaDTOMock.setTipo(TipoOcorrencia.ENCHENTE);
        ocorrenciaDTOMock.setCriadoEm(new Timestamp(System.currentTimeMillis()));

        ocorrenciaUpdateDTOMock = new OcorrenciaUpdateDTO();
        ocorrenciaUpdateDTOMock.setNome("Enchente em Terra Nova");
        ocorrenciaUpdateDTOMock.setIdEndereco(1);
        ocorrenciaUpdateDTOMock.setGravidade(TipoRisco.ALTO);
        ocorrenciaUpdateDTOMock.setDescricao("Altos níveis de água");
        ocorrenciaUpdateDTOMock.setTipo(TipoOcorrencia.ENCHENTE);

        ativa = '1';
        inativa = '0';
    }


    @Test
    @DisplayName("Deveria Criar Ocorrência com Sucesso")
    void deveriaCriarOcorrenciaComSucesso() throws Exception {
        Optional<Usuario> usuarioOptional = Optional.of(usuarioExistente);
        when(usuarioRepository.findById(anyInt())).thenReturn(usuarioOptional);
        when(enderecoService.getEndereco(anyInt())).thenReturn(enderecoExistente);
        when(ocorrenciaService.converterDTO(ocorrenciaCreateDTOMock)).thenReturn(ocorrenciaMock);
        when(ocorrenciaRepository.save(any())).thenReturn(ocorrenciaMock);
        when(ocorrenciaService.retornarDTO(ocorrenciaMock)).thenReturn(ocorrenciaDTOMock);

        OcorrenciaDTO ocorrenciaDTOCriada = ocorrenciaService.adicionar(ocorrenciaCreateDTOMock);

        verify(notificacaoService).sendMessage(ocorrenciaMock.getNome(), ocorrenciaMock.getGravidade());
        assertNotNull(ocorrenciaDTOCriada);
        assertEquals(ocorrenciaDTOMock, ocorrenciaDTOCriada);
    }

    @Test
    @DisplayName("Deveria Retornar Exception de que o ID do usuário fornecido não existe.")
    void deveriaRetornarIdUsuarioNaoEncontradoException() {
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> ocorrenciaService.adicionar(ocorrenciaCreateDTOMock));
    }

    @Test
    @DisplayName("Deveria Listar Todas as Ocorrência, Ativas e Inativas")
    void deveriaListarTodasOcorrencias() throws Exception {
        List<Ocorrencia> ocorrenciaList = List.of(ocorrenciaMock);
        when(ocorrenciaRepository.findAll()).thenReturn(ocorrenciaList);

        List<OcorrenciaDTO> ocorrenciaDTOList = ocorrenciaList.stream()
                .map(ocorrencia -> ocorrenciaService.retornarDTO(ocorrencia))
                .toList();
        List<OcorrenciaDTO> listaRetornada = ocorrenciaService.listarTodas();

        assertNotNull(ocorrenciaList);
        assertEquals(ocorrenciaDTOList, listaRetornada);
    }

    @Test
    @DisplayName("Deveria Retornar Exception que Nenhuma Ocorrencia foi Encontrada.")
    void deveriaRetornarNenhumaOcorrenciaEncontradaException() {
        when((ocorrenciaRepository.findAll())).thenReturn(Collections.emptyList());
        assertThrows(EntidadeNaoEncontradaException.class, () -> ocorrenciaService.listarTodas());
    }

    @Test
    @DisplayName("Deveria Listar Ocorrências Ativas")
    void deveriaListarOcorrenciasAtivas() throws Exception {
        List<Ocorrencia> ocorrenciaList = List.of(ocorrenciaMock);
        when(ocorrenciaRepository.findAllByStatusEquals(ativa)).thenReturn(ocorrenciaList);

        List<OcorrenciaDTO> ocorrenciaDTOList = ocorrenciaList.stream()
                .map(ocorrencia -> ocorrenciaService.retornarDTO(ocorrencia))
                .toList();
        List<OcorrenciaDTO> listaRetornada = ocorrenciaService.listarAtivas();

        assertNotNull(ocorrenciaList);
        assertEquals(ocorrenciaDTOList, listaRetornada);
    }

    @Test
    @DisplayName("Deveria Retornar Exception que Nenhuma Ocorrencia foi Encontrada.")
    void deveriaRetornarNenhumaOcorrenciaAtivaEncontradaException() {
        when((ocorrenciaRepository.findAllByStatusEquals(ativa))).thenReturn(Collections.emptyList());
        assertThrows(EntidadeNaoEncontradaException.class, () -> ocorrenciaService.listarAtivas());
    }

    @Test
    @DisplayName("Deveria Listar Ocorrências Ativas Por Id")
    void deveriaListarOcorrenciasAtivasPorId() throws Exception {
        Integer idOcorrencia = 123;
        ocorrenciaMock.setIdOcorrencia(idOcorrencia);
        when(ocorrenciaRepository.findAllByStatusEquals(ativa)).thenReturn(Collections.singletonList(ocorrenciaMock));

        ocorrenciaDTOMock.setIdOcorrencia(idOcorrencia);
        when(ocorrenciaService.retornarDTO(ocorrenciaMock)).thenReturn(ocorrenciaDTOMock);

        List<OcorrenciaDTO> listaRetornada = ocorrenciaService.listarAtivasPorId(idOcorrencia);

        assertNotNull(listaRetornada);
        assertEquals(ocorrenciaDTOMock, listaRetornada.get(0));
    }

    @Test
    @DisplayName("Deveria Paginar Ocorrências")
    void deveriaPaginarOcorrencias() {
        List<Ocorrencia> ocorrenciaList = List.of(ocorrenciaMock);
        Page<Ocorrencia> ocorrenciaPaginada = new PageImpl<>(ocorrenciaList, PageRequest.of(0, 10), ocorrenciaList.size());
        when(ocorrenciaRepository.findAll(Pageable.unpaged())).thenReturn(ocorrenciaPaginada);

        Page<Ocorrencia> retorno = ocorrenciaService.listar(Pageable.unpaged());

        assertEquals(ocorrenciaPaginada, retorno);
    }

    @Test
    @DisplayName("Deveria Atualizar Ocorrência para Usuario Admin")
    void deveriaAtualizarOcorrenciaParaUsuarioAdmin() throws Exception {
        when(ocorrenciaRepository.findById(anyInt())).thenReturn(Optional.of(ocorrenciaMock));
        when(ocorrenciaService.retornarDTO(ocorrenciaMock)).thenReturn(ocorrenciaDTOMock);
        when(ocorrenciaRepository.save(ocorrenciaMock)).thenReturn(ocorrenciaMock);

        OcorrenciaDTO ocorrenciaDTO = ocorrenciaService.atualizarParaAdmin(anyInt(), ocorrenciaUpdateDTOMock);

        verify(ocorrenciaRepository).save(ocorrenciaMock);
        assertEquals(ocorrenciaDTOMock, ocorrenciaDTO);
    }

    @Test
    @DisplayName("Deveria Retornar Exception Ocorrência Não Existe ao Tentar Atualizar Ocorrência para Usuario Admin")
    void deveriaRetornarExceptionOcorrenciaNaoExisteAoTentarAtualizarOcorrenciaParaUsuarioAdmin() {
        ocorrenciaMock.setStatus(inativa);
        when(ocorrenciaRepository.findById(anyInt())).thenReturn(Optional.of(ocorrenciaMock));
        assertThrows(RegraDeNegocioException.class, () -> ocorrenciaService.atualizarParaAdmin(anyInt(), ocorrenciaUpdateDTOMock));
    }

    @Test
    @DisplayName("Deveria Retornar Exception Permissão Negada ao Tentar Atualizar Ocorrência para Usuario Admin")
    void deveriaAtualizarOcorrenciaParaUsuarioComum() throws Exception {
        when(usuarioComumService.getIdLoggedUser()).thenReturn(usuarioExistente.getIdUsuario());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "usuario",
                "senha",
                AuthorityUtils.createAuthorityList("COMUM")
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(ocorrenciaRepository.findById(anyInt())).thenReturn(Optional.of(ocorrenciaMock));
        when(ocorrenciaService.retornarDTO(ocorrenciaMock)).thenReturn(ocorrenciaDTOMock);
        when(ocorrenciaRepository.save(ocorrenciaMock)).thenReturn(ocorrenciaMock);

        OcorrenciaDTO ocorrenciaDTO = ocorrenciaService.atualizarParaComum(anyInt(), ocorrenciaUpdateDTOMock);

        verify(ocorrenciaRepository).save(ocorrenciaMock);
        assertEquals(ocorrenciaDTOMock, ocorrenciaDTO);
    }

    @Test
    @DisplayName("Deveria Retornar Exception Permissão Negada ao Tentar Atualizar Ocorrência para Usuario Comum")
    void deveriaRetornarExceptionPermissaoNegadaAoTentarAtualizarOcorrenciaParaUsuarioComum() {
        when(usuarioComumService.getIdLoggedUser()).thenReturn(2);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "usuario",
                "senha",
                AuthorityUtils.createAuthorityList("COMUM")
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(ocorrenciaRepository.findById(anyInt())).thenReturn(Optional.of(ocorrenciaMock));
        assertThrows(PermissaoNegadaException.class, () -> ocorrenciaService.atualizarParaComum(anyInt(), ocorrenciaUpdateDTOMock));
    }

    @Test
    @DisplayName("Deveria Retornar Exception Ocorrência Não Existe ao Tentar Remover Ocorrência para Usuario Comum")
    void deveriaRetornarExceptionOcorrenciaNaoExisteAoTentarAtualizarComUsuarioComum() {
        ocorrenciaMock.setStatus(inativa);
        when(ocorrenciaRepository.findById(anyInt())).thenReturn(Optional.of(ocorrenciaMock));
        assertThrows(RegraDeNegocioException.class, () -> ocorrenciaService.atualizarParaComum(anyInt(), ocorrenciaUpdateDTOMock));
    }

    @Test
    @DisplayName("Deveria Remover Ocorrência Usuario Admin")
    void deveriaRemoverOcorrenciaUsuarioAdmin() throws Exception {
        when(ocorrenciaRepository.findById(anyInt())).thenReturn(Optional.of(ocorrenciaMock));
        ocorrenciaService.removerDeAdmin(anyInt());
        verify(ocorrenciaRepository).save(ocorrenciaMock);
        assertEquals(ocorrenciaMock.getStatus(), inativa);
    }

    @Test
    @DisplayName("Deveria Remover Ocorrência Usuario Comum")
    void deveriaRemoverOcorrenciaUsuarioComum() throws Exception {
        when(usuarioComumService.getIdLoggedUser()).thenReturn(usuarioExistente.getIdUsuario());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "usuario",
                "senha",
                AuthorityUtils.createAuthorityList("COMUM")
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(ocorrenciaRepository.findById(anyInt())).thenReturn(Optional.of(ocorrenciaMock));

        ocorrenciaService.removerDeComum(anyInt());

        verify(ocorrenciaRepository).save(ocorrenciaMock);

        assertEquals(ocorrenciaMock.getStatus(), inativa);
    }
    @Test
    @DisplayName("Deveria Retornar Exception Permissão Negada ao Tentar Remover Ocorrência para Usuario Comum")
    void deveriaRetornarExceptionPermissaoNegadaAoTentarRemoverOcorrenciaParaUsuarioComum() {
        when(usuarioComumService.getIdLoggedUser()).thenReturn(2);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "usuario",
                "senha",
                AuthorityUtils.createAuthorityList("COMUM")
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(ocorrenciaRepository.findById(anyInt())).thenReturn(Optional.of(ocorrenciaMock));
        assertThrows(PermissaoNegadaException.class, () -> ocorrenciaService.removerDeComum(anyInt()));
    }

    @Test
    @DisplayName("Deveria Retornar Ocorrência por Id")
    void deveriaListarOcorrenciaPorId() throws Exception {
        when(ocorrenciaRepository.findById(anyInt())).thenReturn(Optional.of(ocorrenciaMock));
        when(ocorrenciaService.retornarDTO(ocorrenciaMock)).thenReturn(ocorrenciaDTOMock);
        OcorrenciaDTO ocorrenciaDTO = ocorrenciaService.listarPorId(anyInt());
        assertNotNull(ocorrenciaDTO);
        assertEquals(ocorrenciaDTOMock, ocorrenciaDTO);
    }

    @Test
    @DisplayName("Deveria Retornar Ocorrência Por Id")
    void deveriaRetornarOcorrenciaPorId() throws Exception {
        Optional<Ocorrencia> optionalOcorrencia = Optional.of(ocorrenciaMock);
        when(ocorrenciaRepository.findById(anyInt())).thenReturn(optionalOcorrencia);

        Ocorrencia ocorrenciaRetornada = ocorrenciaService.getOcorrencia(anyInt());
        assertNotNull(optionalOcorrencia);
        assertEquals(ocorrenciaMock, ocorrenciaRetornada);
    }

    @Test
    @DisplayName("Deveria Retornar Exception Nenhuma Ocorrência Encontrada Por Id")
    void deveriaRetornarNenhumaOcorrenciaPorIdException() {
        when(ocorrenciaRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class, () -> ocorrenciaService.getOcorrencia(anyInt()));
    }

    @Test
    void converterDTO() {
        when(objectMapper.convertValue(ocorrenciaCreateDTOMock, Ocorrencia.class)).thenReturn(ocorrenciaMock);
        Ocorrencia retorno = ocorrenciaService.converterDTO(ocorrenciaCreateDTOMock);
        assertEquals(ocorrenciaMock, retorno);
    }

    @Test
    void retornarDTO() {
        when(objectMapper.convertValue(ocorrenciaMock, OcorrenciaDTO.class)).thenReturn(ocorrenciaDTOMock);
        OcorrenciaDTO retorno = ocorrenciaService.retornarDTO(ocorrenciaMock);
        assertEquals(ocorrenciaDTOMock, retorno);
    }
}