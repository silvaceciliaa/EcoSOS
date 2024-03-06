package br.com.dbc.vemser.ecososapi.ecosos.service;

import br.com.dbc.vemser.ecososapi.ecosos.dto.comentario.ComentarioRelatorioDTO;
import br.com.dbc.vemser.ecososapi.ecosos.dto.ocorrencia.OcorrenciaRelatorioDTO;
import br.com.dbc.vemser.ecososapi.ecosos.dto.usuario.*;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Comentario;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Usuario;
import br.com.dbc.vemser.ecososapi.ecosos.enums.TipoOcorrencia;
import br.com.dbc.vemser.ecososapi.ecosos.enums.TipoRisco;
import br.com.dbc.vemser.ecososapi.ecosos.exceptions.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.ecososapi.ecosos.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioAdminService - Test")
class UsuarioAdminServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private UsuarioAdminService usuarioAdminService;

    @Mock
    private SecurityContext  securityContextMock;

    @Test
    public void deveriaListarComSucesso() throws Exception {
        List<Usuario> listaMockada = List.of(retornarUsuario(), retornarUsuario(), retornarUsuario());

        when(usuarioRepository.findAll()).thenReturn(listaMockada);

        List<UsuarioDTO> listaDTORetornada = usuarioAdminService.listar();

        assertNotNull(listaDTORetornada);
        assertEquals(listaMockada.size(), listaDTORetornada.size());
    }
    @Test
    public void deveriaListarUsuariosAtivosComSucesso() {
        List<Usuario> usuariosAtivos = List.of(
                retornarUsuarioAtivo(),
                retornarUsuarioAtivo(),
                retornarUsuarioAtivo()
        );

        when(usuarioRepository.findByStatus("A")).thenReturn(usuariosAtivos);

        List<UsuarioDTO> usuariosAtivosDTO = usuarioAdminService.listarUsuariosAtivos();

        assertEquals(usuariosAtivos.size(), usuariosAtivosDTO.size());
    }

    @Test
    public void deveriaAtualizarPessoaPorId() throws Exception {
        Usuario usuarioEntityMock = retornarUsuario();

        Usuario pessoaEntityAntigo = new Usuario();
        BeanUtils.copyProperties(usuarioEntityMock, pessoaEntityAntigo);

        UsuarioUpdateDTO pessoaCreateDTOMock = retornarUsuarioUpdateDTO();
        Usuario pessoaAlterada = retornarUsuario();
        UsuarioDTO pessoaDTOMock = retornarUsuarioDTO();

        pessoaAlterada.setNome(pessoaCreateDTOMock.getNome());
        pessoaAlterada.setTelefone(pessoaCreateDTOMock.getTelefone());
        pessoaAlterada.setEmail(pessoaCreateDTOMock.getEmail());

        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioEntityMock));
        when(usuarioRepository.save(anyObject())).thenReturn(pessoaAlterada);
        when(objectMapper.convertValue(pessoaAlterada, UsuarioDTO.class)).thenReturn(pessoaDTOMock);

        UsuarioDTO pessoaDTORetornada = usuarioAdminService.atualizar(usuarioEntityMock.getIdUsuario(), pessoaCreateDTOMock);

        assertNotNull(pessoaDTORetornada);
        assertEquals(pessoaCreateDTOMock.getNome(), pessoaDTORetornada.getNome());
        assertEquals(pessoaCreateDTOMock.getTelefone(), pessoaDTORetornada.getTelefone());
        assertEquals(pessoaCreateDTOMock.getEmail(), pessoaDTORetornada.getEmail());
    }

    @Test
    void deveriaDeletarUsuarioComSucesso() throws Exception {
        Integer idUsuario = 123;
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setStatus("A");

        when(usuarioRepository.findById(idUsuario)).thenReturn(java.util.Optional.of(usuario));

        usuarioAdminService.deletar(idUsuario);

        verify(usuarioRepository).save(usuario);
    }
    @Test
    public void deveriaDeletarUsuarioInexistente() {
        Integer idUsuario = 123;

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class, () -> usuarioAdminService.deletar(idUsuario));
    }

    @Test
    public void deveriaListarTodosComSucesso() {
        List<Usuario> usuarios = List.of(
                retornarUsuario(),
                retornarUsuario(),
                retornarUsuario()
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> usuariosPage = new PageImpl<>(usuarios, pageable, usuarios.size());

        when(usuarioRepository.findAll(pageable)).thenReturn(usuariosPage);

        Page<UsuarioDTO> usuariosDTO = usuarioAdminService.listarTodos(pageable);

        assertEquals(usuarios.size(), usuariosDTO.getContent().size());
    }

    @Test
    public void deveriaListarTodosComSucessoQuandoNaoHouverUsuarios() {
        List<Usuario> usuarios = Collections.emptyList();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> usuariosPage = new PageImpl<>(usuarios, pageable, usuarios.size());

        when(usuarioRepository.findAll(pageable)).thenReturn(usuariosPage);

        Page<UsuarioDTO> usuariosDTO = usuarioAdminService.listarTodos(pageable);

        assertEquals(usuarios.size(), usuariosDTO.getContent().size());
    }


    @Test
    void deveriaRetornarUsuarioQuandoUsuarioExiste() throws Exception {
        int idUsuario = 123;
        Usuario usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(idUsuario);

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioMock));

        Usuario usuarioRetornado = usuarioAdminService.returnUsuario(idUsuario);

        assertEquals(usuarioMock, usuarioRetornado);
    }

    @Test
    void deveriaRetornarUsuarioQuandoUsuarioNaoExiste() {
        int idUsuario = 456;

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class, () -> usuarioAdminService.returnUsuario(idUsuario));
    }

    @Test
    public void deveriaListarComentariosDeUsuarioComSucesso() {
        Usuario usuario = retornarUsuarioComComentario();
        Integer idUsuario = usuario.getIdUsuario();

        when(usuarioRepository.procurarPessoaComentarios(idUsuario)).thenReturn(List.of(new UsuarioComentarioRelatorioDTO(idUsuario, usuario.getNome(), usuario.getEmail())));

        when(usuarioRepository.procurarComentarios(idUsuario)).thenReturn(List.of(new ComentarioRelatorioDTO("Conteúdo do comentário")));

        List<UsuarioComentarioRelatorioDTO> usuarioComentarioRelatorioDTO = usuarioAdminService.listPessoaComentarios(idUsuario);

        assertEquals(1, usuarioComentarioRelatorioDTO.size());
    }

    @Test
    public void deveriaListarOcorrenciasDeUsuarioComSucesso() {
        Usuario usuario = retornarUsuario();
        Integer idUsuario = usuario.getIdUsuario();

        when(usuarioRepository.procurarPessoaOcorrencias(idUsuario)).thenReturn(List.of(new UsuarioOcorrenciaRelatorioDTO(idUsuario, usuario.getNome(), usuario.getEmail())));

        when(usuarioRepository.procurarOcorrencias(idUsuario)).thenReturn(List.of(retornarOcorrenciaRelatorioDTO()));

        List<UsuarioOcorrenciaRelatorioDTO> usuarioOcorrenciaRelatorioDTO = usuarioAdminService.listPessoaOcorrencias(idUsuario);

        assertEquals(1, usuarioOcorrenciaRelatorioDTO.size());
    }

    @Test
    public void deveriaRetornarDTOQuandoUsuarioExiste() {
        Usuario usuario = retornarUsuario();
        UsuarioDTO usuarioDTO = retornarUsuarioDTO();

        when(objectMapper.convertValue(usuario, UsuarioDTO.class)).thenReturn(usuarioDTO);

        UsuarioDTO usuarioDTORetornado = usuarioAdminService.retornarDTO(usuario);

        assertEquals(usuarioDTO, usuarioDTORetornado);
    }

    @Test
    public void deveriaRetornarUsuarioLogado() throws Exception {
        int idUsuario = 123;
        Authentication authentication = new UsernamePasswordAuthenticationToken(idUsuario, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Usuario usuario = retornarUsuario();
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));

        String emailUsuarioLogado = usuarioAdminService.getLoggedUser();

        assertEquals(usuario.getEmail(), emailUsuarioLogado);
    }

    @Test
    public void DeveRetornarOUsuarioLogadoQuandoEstiverLogado() {
        int expectedUserId = 123;
        Authentication authentication = new UsernamePasswordAuthenticationToken(expectedUserId, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Integer actualUserId = usuarioAdminService.getIdLoggedUser();

        assertEquals(expectedUserId, actualUserId);
    }


//----------------------------------------------------------------------------------------------------------------


    public static UsuarioDTO retornarUsuarioDTO(){
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setIdUsuario(109);
        usuario.setIdEndereco(101);
        usuario.setNome("Deyvid Lindo");
        usuario.setTelefone("01987324843");
        usuario.setEmail("deyvid.lucas@dbccompany.com.br");

        return usuario;
    }
    public static Usuario retornarUsuario(){
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(109);
        usuario.setIdEndereco(101);
        usuario.setNome("Deyvid Lindo");
        usuario.setTelefone("01987324843");
        usuario.setEmail("deyvid.lucas@dbccompany.com.br");
        usuario.setStatus("A");

        return usuario;
    }
    public static UsuarioCreateDTO retornarUsuarioCreateDTO(){
        UsuarioCreateDTO usuario = new UsuarioCreateDTO();
        usuario.setIdEndereco(101);
        usuario.setNome("Deyvid Lindo");
        usuario.setTelefone("01987324843");
        usuario.setEmail("deyvid.lucas@dbccompany.com.br");

        return usuario;
    }

    private Usuario retornarUsuarioAtivo() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(109);
        usuario.setIdEndereco(101);
        usuario.setNome("Deyvid Lindo");
        usuario.setTelefone("01987324843");
        usuario.setEmail("deyvid.lucas@dbccompany.com.br");
        usuario.setStatus("A");
        return usuario;
    }

    private Usuario retornarUsuarioComComentario() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(109);
        usuario.setIdEndereco(101);
        usuario.setNome("Deyvid Lindo");
        usuario.setTelefone("01987324843");
        usuario.setEmail("deyvid.lucas@dbccompany.com.br");

        Set<Comentario> comentarios = retornarComentarioEntitySet();
        usuario.setComentarios(comentarios);

        return usuario;
    }


    private static Comentario retornarComentarioEntity(){
        Comentario comentario = new Comentario();

        comentario.setIdComentario(1);
        comentario.setIdUsuario(123);
        comentario.setConteudo("Este é o conteúdo do comentário");
        comentario.setCriadoEm(new Timestamp(System.currentTimeMillis()));
        comentario.setAtualizadoEm(new Timestamp(System.currentTimeMillis()));
        comentario.setIdOcorrencia(456);
        return comentario;
    }

    private Set<Comentario> retornarComentarioEntitySet() {
        Set<Comentario> comentarios = new HashSet<>();

        comentarios.add(retornarComentarioEntity());

        return comentarios;
    }

    private static UsuarioUpdateDTO retornarUsuarioUpdateDTO(){
    UsuarioUpdateDTO usuarioUpdateDTO = new UsuarioUpdateDTO();
    usuarioUpdateDTO.setIdEndereco(101);
    usuarioUpdateDTO.setNome("Deyvid Lindo");
    usuarioUpdateDTO.setTelefone("01987324843");
    usuarioUpdateDTO.setEmail("deyvid.lucas@dbccompany.com.br");
    return usuarioUpdateDTO;
    }

    private Usuario criarUsuarioComId(Integer id, String email) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setEmail(email);
        return usuario;
    }

    private static OcorrenciaRelatorioDTO retornarOcorrenciaRelatorioDTO(){
        OcorrenciaRelatorioDTO ocorrencia = new OcorrenciaRelatorioDTO();
        ocorrencia.setNomeOcorrencia("Nome da Ocorrência");
        ocorrencia.setTipoOcorrencia(TipoOcorrencia.DESLIZAMENTO);
        ocorrencia.setTipoRisco(TipoRisco.ALTO);
        return ocorrencia;
    }
}
