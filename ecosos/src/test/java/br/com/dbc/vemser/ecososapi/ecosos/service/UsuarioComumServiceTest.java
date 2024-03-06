package br.com.dbc.vemser.ecososapi.ecosos.service;

import br.com.dbc.vemser.ecososapi.ecosos.dto.auth.AlteraSenhaDTO;
import br.com.dbc.vemser.ecososapi.ecosos.dto.usuario.UsuarioCreateDTO;
import br.com.dbc.vemser.ecososapi.ecosos.dto.usuario.UsuarioDTO;
import br.com.dbc.vemser.ecososapi.ecosos.dto.usuario.UsuarioUpdateDTO;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Cargo;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Usuario;
import br.com.dbc.vemser.ecososapi.ecosos.exceptions.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.ecososapi.ecosos.exceptions.RegraDeNegocioException;
import br.com.dbc.vemser.ecososapi.ecosos.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioComumService - Test")
class UsuarioComumServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EnderecoService enderecoService;

    @Mock
    private CargoService cargoService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ObjectMapper objectMapper;

    @Spy
    @InjectMocks
    UsuarioComumService usuarioComumService;

    @BeforeEach
    public void setUp() {
        Authentication auth = new UsernamePasswordAuthenticationToken("1", null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Deveria alterar a senha com sucesso")
    void deveriaAlterarSenhaComSucesso() throws Exception {
        String email = "usuario@example.com";
        String senhaAtual = "senhaAntiga";
        String senhaNova = "senhaNova";

        AlteraSenhaDTO alteraSenhaDTO = new AlteraSenhaDTO();
        alteraSenhaDTO.setEmail(email);
        alteraSenhaDTO.setSenhaAtual(senhaAtual);
        alteraSenhaDTO.setSenhaNova(senhaNova);

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senhaAtual));

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setEmail(email);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senhaAtual, usuario.getSenha())).thenReturn(true);
        when(passwordEncoder.encode(senhaNova)).thenReturn("senhaNovaCriptografada");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(objectMapper.convertValue(usuario, UsuarioDTO.class)).thenReturn(usuarioDTO);

        UsuarioDTO resultUsuarioDTO = usuarioComumService.alterarSenha(alteraSenhaDTO);

        assertNotNull(resultUsuarioDTO);
        assertEquals(email, resultUsuarioDTO.getEmail());
    }

    @Test
    @DisplayName("Deveria cadastrar um usuário com sucesso")
    void deveriaCadastrarComSucesso() throws Exception {

        UsuarioCreateDTO usuarioCreateDTO = criarUsuarioCreateDTO();
        Usuario usuario = criarUsuario();
        UsuarioDTO usuarioDTO = criarUsuarioDTO();
        Usuario usuarioMock = new Usuario();
        usuarioMock.setTelefone("12345098123");
        usuarioMock.setEmail("mock@mock.com");

        when(usuarioRepository.findByEmailEquals(usuarioCreateDTO.getEmail())).thenReturn(null);
        when(usuarioRepository.findByTelefoneEquals(usuarioCreateDTO.getTelefone())).thenReturn(null);

        String senhaCriptografada = "123";
        when(passwordEncoder.encode(usuarioCreateDTO.getSenha())).thenReturn(senhaCriptografada);

        when(objectMapper.convertValue(usuarioCreateDTO, Usuario.class)).thenReturn(usuario);

        when(usuarioRepository.save(any())).thenReturn(usuario);
        when(objectMapper.convertValue(usuario, UsuarioDTO.class)).thenReturn(usuarioDTO);

        lenient().when(objectMapper.convertValue(usuario, UsuarioDTO.class)).thenReturn(usuarioDTO);

        UsuarioDTO usuarioCreated = usuarioComumService.adicionar(usuarioCreateDTO);

        assertNotNull(usuarioCreated);
        Assertions.assertEquals(usuario.getEmail(), usuarioCreated.getEmail());
        Assertions.assertEquals(usuario.getTelefone(), usuarioCreated.getTelefone());

    }

    @Test
    @DisplayName("Deveria lançar exception de email já cadastrado")
    void deveriaLancarEmailJaCadastradoException() {

        UsuarioCreateDTO usuarioCreateDTO = new UsuarioCreateDTO();
        usuarioCreateDTO.setEmail("test@example.com");

        when(usuarioRepository.findByEmailEquals(usuarioCreateDTO.getEmail())).thenReturn(new Usuario());

        assertThrows(RegraDeNegocioException.class, () -> usuarioComumService.adicionar(usuarioCreateDTO));
    }

    @Test
    @DisplayName("Deveria lançar exception de telefone já cadastrado")
    void deveriaLancarTelfoneJaCadastradoException() {

        UsuarioCreateDTO usuarioCreateDTO = new UsuarioCreateDTO();
        usuarioCreateDTO.setTelefone("123445678901");

        when(usuarioRepository.findByTelefoneEquals(usuarioCreateDTO.getTelefone())).thenReturn(new Usuario());

        assertThrows(RegraDeNegocioException.class, () -> usuarioComumService.adicionar(usuarioCreateDTO));
    }

    @Test
    @DisplayName("Deveria mostrar perfil com sucesso")
    void deveriaVerPerfilComSucesso() throws Exception {
        Usuario usuario = new Usuario();
        UsuarioDTO usuarioDTO = criarUsuarioDTO();

        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuario));
        when(objectMapper.convertValue(usuario, UsuarioDTO.class)).thenReturn(usuarioDTO);

        UsuarioDTO usuarioDTORetorno = usuarioComumService.verPerfil();

        assertEquals(usuarioDTO, usuarioDTORetorno);
        }

    @Test
    @DisplayName("Deveria atualizar com sucesso")
    void deveriaAtualizarComSucesso() throws Exception {

        UsuarioUpdateDTO usuarioUpdateDTO = criarUsuarioUpdateDTO();
        Usuario usuarioAlterado = criarUsuario();
        UsuarioDTO usuarioDTO = criarUsuarioDTO();

        usuarioAlterado.setNome(usuarioUpdateDTO.getNome());
        usuarioAlterado.setTelefone(usuarioUpdateDTO.getTelefone());
        usuarioAlterado.setEmail(usuarioUpdateDTO.getEmail());


        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioAlterado));
        when(usuarioComumService.getIdLoggedUser()).thenReturn(usuarioAlterado.getIdUsuario());
        when(usuarioRepository.save(anyObject())).thenReturn(usuarioAlterado);
        when(objectMapper.convertValue(usuarioAlterado, UsuarioDTO.class)).thenReturn(usuarioDTO);

        UsuarioDTO pessoaDTORetornada = usuarioComumService.atualizar(usuarioAlterado.getIdUsuario(), usuarioUpdateDTO);

        assertNotNull(pessoaDTORetornada);
        assertEquals(usuarioUpdateDTO.getNome(), pessoaDTORetornada.getNome());
        assertEquals(usuarioUpdateDTO.getTelefone(), pessoaDTORetornada.getTelefone());
        assertEquals(usuarioUpdateDTO.getEmail(), pessoaDTORetornada.getEmail());
    }

    @Test
    @DisplayName("Deveria deletar com sucesso")
    void deveriaDeletarUsuarioComSucesso() throws Exception {
        Integer idUsuario = 1;
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setStatus("A");
        Usuario usuarioMock = criarUsuario();

        when(usuarioComumService.getIdLoggedUser()).thenReturn(usuario.getIdUsuario());

        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioMock));
        usuarioComumService.deletar(anyInt());

        verify(usuarioRepository).save(usuarioMock);
        assertEquals(usuarioMock.getStatus(), "D");
    }

    @Test
    @DisplayName("Deveria retornar usuário com o id passado")
    void deveriaRetornarUsuarioPorId() throws Exception {
        Usuario usuarioInput = criarUsuario();

        when(usuarioRepository.findById(any())).thenReturn(Optional.of(usuarioInput));

        Usuario usuario = usuarioComumService.returnUsuario(1);

        Assertions.assertNotNull(usuario);
        Assertions.assertEquals(usuarioInput, usuario);
    }

    @Test
    @DisplayName("Deveria retornar exception pois o id não existe")
    void deveriaRetornarExceptionAoReceberIdNaoExistente() throws Exception {
        Integer idNaoExistente = new Random().nextInt();

        assertThrows(EntidadeNaoEncontradaException.class, () -> usuarioComumService.returnUsuario(idNaoExistente));

    }

    @Test
    @DisplayName("Deveria retornar o usuário logado")
    public void deveriaRetornarOUsuarioLogadoQuandoEstiverLogado() {

        int expectedUserId = 123;
        Authentication authentication = new UsernamePasswordAuthenticationToken(expectedUserId, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Integer actualUserId = usuarioComumService.getIdLoggedUser();

        assertEquals(expectedUserId, actualUserId);
    }

    @Test
    @DisplayName("Deveria retornar que telefone existe")
    void deveriaLancarTrueTelefoneExiste() throws Exception {
        String telefoneTeste = "12345678912";
        when(usuarioRepository.findByTelefoneEquals(telefoneTeste)).thenReturn(new Usuario());

        boolean result = usuarioComumService.telefoneExiste(telefoneTeste);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deveria retornar que telefone não existe")
    void deveriaLancarFalseTelefoneExiste() throws Exception {
        String telefoneTeste = "12345678912";
        when(usuarioRepository.findByTelefoneEquals(telefoneTeste)).thenReturn(null);

        boolean result = usuarioComumService.telefoneExiste(telefoneTeste);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deveria retornar que email existe")
    void deveriaLancarTrueEmailExiste() throws Exception {
        String emailTeste = "test@example.com";
        when(usuarioRepository.findByEmailEquals(emailTeste)).thenReturn(new Usuario());

        boolean result = usuarioComumService.emailExiste(emailTeste);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deveria retornar que email não existe")
    void deveriaLancarFalseEmailExiste() throws Exception {
        String emailTeste = "test@example.com";
        when(usuarioRepository.findByEmailEquals(emailTeste)).thenReturn(null);

        boolean result = usuarioComumService.emailExiste(emailTeste);

        assertFalse(result);
    }

    public static UsuarioDTO criarUsuarioDTO(){

        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setIdUsuario(100);
        usuario.setIdEndereco(4);
        usuario.setNome("Cecília Silva");
        usuario.setTelefone("47987324843");
        usuario.setEmail("cecilia.alice@dbccompany.com.br");

        return usuario;
    }

    public static UsuarioCreateDTO criarUsuarioCreateDTO(){

        UsuarioCreateDTO usuario = new UsuarioCreateDTO();
        usuario.setIdEndereco(4);
        usuario.setNome("Cecília Silva");
        usuario.setTelefone("47987324843");
        usuario.setEmail("cecilia.alice@dbccompany.com.br");
        usuario.setSenha("123");

        return usuario;
    }

    public static Usuario criarUsuario(){
        Usuario usuario = new Usuario();

        String CARGO_NOME = "ROLE_COMUM";

        Cargo cargo = new Cargo();
        cargo.setIdCargo(2);
        cargo.setNome(CARGO_NOME);

        usuario.setCargos(new HashSet<>());

        usuario.setIdUsuario(1);
        usuario.setIdEndereco(4);
        usuario.setNome("Cecília Silva");
        usuario.setTelefone("47987324843");
        usuario.setEmail("cecilia.alice@dbccompany.com.br");
        usuario.setSenha("123");
        usuario.setCriadoEm(Timestamp.valueOf(LocalDateTime.now()));
        usuario.setStatus("A");
        usuario.getCargos().add(cargo);

        return usuario;
    }

    private static UsuarioUpdateDTO criarUsuarioUpdateDTO(){
        UsuarioUpdateDTO usuarioUpdateDTO = new UsuarioUpdateDTO();
        usuarioUpdateDTO.setIdEndereco(4);
        usuarioUpdateDTO.setNome("Cecília Silva");
        usuarioUpdateDTO.setTelefone("47987324843");
        usuarioUpdateDTO.setEmail("cecilia.alice@dbccompany.com.br");
        return usuarioUpdateDTO;
    }
}