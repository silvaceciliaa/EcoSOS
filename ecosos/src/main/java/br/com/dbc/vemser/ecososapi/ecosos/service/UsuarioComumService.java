package br.com.dbc.vemser.ecososapi.ecosos.service;

import br.com.dbc.vemser.ecososapi.ecosos.dto.auth.AlteraSenhaDTO;
import br.com.dbc.vemser.ecososapi.ecosos.dto.usuario.UsuarioCreateDTO;
import br.com.dbc.vemser.ecososapi.ecosos.dto.usuario.UsuarioDTO;
import br.com.dbc.vemser.ecososapi.ecosos.dto.usuario.UsuarioUpdateDTO;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Cargo;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Endereco;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Usuario;
import br.com.dbc.vemser.ecososapi.ecosos.exceptions.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.ecososapi.ecosos.exceptions.PermissaoNegadaException;
import br.com.dbc.vemser.ecososapi.ecosos.exceptions.RegraDeNegocioException;
import br.com.dbc.vemser.ecososapi.ecosos.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UsuarioComumService {

    private final UsuarioRepository usuarioRepository;
    private final CargoService cargoService;
    private final EnderecoService enderecoService;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UsuarioDTO alterarSenha(AlteraSenhaDTO alteraSenhaDTO) throws Exception {
        Optional<Usuario> usuarioAtualizar = findByEmail(alteraSenhaDTO.getEmail());
        if (usuarioAtualizar.isEmpty()) {
            throw new RegraDeNegocioException("Usuário ou Senha inválida");
        }
        if (!passwordEncoder.matches(alteraSenhaDTO.getSenhaAtual(), usuarioAtualizar.get().getSenha())) {
            throw new RegraDeNegocioException("Usuário ou Senha inválida");
        }

        String senhaNovaCriptografada = passwordEncoder.encode(alteraSenhaDTO.getSenhaNova());
        usuarioAtualizar.get().setSenha(senhaNovaCriptografada);
        return retornarDTO(usuarioRepository.save(usuarioAtualizar.get()));
    }

    public UsuarioDTO adicionar(UsuarioCreateDTO usuarioCreateDTO) throws Exception{

        if (telefoneExiste(usuarioCreateDTO.getTelefone())) {
            throw new RegraDeNegocioException("Telefone já cadastrado.");
        }

        if (emailExiste(usuarioCreateDTO.getEmail())) {
            throw new RegraDeNegocioException("Email já cadastrado.");
        }

        Endereco endereco = enderecoService.getEndereco(usuarioCreateDTO.getIdEndereco());

        String senhaCriptografada = passwordEncoder.encode(usuarioCreateDTO.getSenha());
        Cargo cargo = cargoService.findCargoById(2);

        Usuario usuarioEntity = converterDTO(usuarioCreateDTO);
        usuarioEntity.setEndereco(endereco);
        usuarioEntity.setSenha(senhaCriptografada);
        usuarioEntity.setCargos(new HashSet<>());
        usuarioEntity.getCargos().add(cargo);
        usuarioEntity.setCriadoEm(Timestamp.valueOf(LocalDateTime.now()));
        usuarioEntity.setStatus("A");
        Map<String, Object> dadosEmail = new HashMap<>();
        dadosEmail.put("nomeUsuario", usuarioEntity.getNome());
        dadosEmail.put("email", usuarioEntity.getEmail());
        dadosEmail.put("mensagem", "Aproveite a plataforma " + usuarioEntity.getUsername() + " \n Caso tenha alguma duvida contate o suporte: ecossossuporte@outlook.com");
        emailService.enviarEmail(dadosEmail, "Ecosos criação de conta!", usuarioEntity.getEmail());

        return retornarDTO(usuarioRepository.save(usuarioEntity));
    }

    public UsuarioDTO verPerfil() throws Exception{
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(getIdLoggedUser());
        Usuario usuario = usuarioOptional.get();
        return retornarDTO(usuario);
    }

    public UsuarioDTO atualizar(Integer id, UsuarioUpdateDTO usuarioUpdateDTO) throws Exception {
        Usuario usuarioAtualizar = returnUsuario(id);

        if(usuarioAtualizar.getIdUsuario() != getIdLoggedUser()) {
            throw new PermissaoNegadaException("Você não tem permissão para atualizar um usuário que não é você");
        }

        if (usuarioUpdateDTO.getNome() != null){
            usuarioAtualizar.setNome(usuarioUpdateDTO.getNome());
        } if(usuarioUpdateDTO.getTelefone() != null){
            usuarioAtualizar.setTelefone(usuarioUpdateDTO.getTelefone());
        } if(usuarioUpdateDTO.getEmail() != null) {
            usuarioAtualizar.setEmail(usuarioUpdateDTO.getEmail());
        }
        Map<String, Object> dadosEmail = new HashMap<>();
        dadosEmail.put("nomeUsuario", usuarioAtualizar.getNome());
        dadosEmail.put("email", usuarioAtualizar.getEmail());
        dadosEmail.put("mensagem", "Usuario atualizado com sucesso! " + usuarioAtualizar.getUsername() + " \n Caso tenha alguma duvida contate o suporte: ecossossuporte@outlook.com");
        emailService.enviarEmail(dadosEmail, "Usuario atualizado!", usuarioAtualizar.getEmail());

        usuarioAtualizar.setAtualizadoEm(Timestamp.valueOf(LocalDateTime.now()));
        return retornarDTO(usuarioRepository.save(usuarioAtualizar));
    }

    public void deletar(Integer idUsuario) throws Exception {
        Usuario usuarioDeletado = returnUsuario(idUsuario);
        if (usuarioDeletado.getIdUsuario() != getIdLoggedUser()) {
            throw new PermissaoNegadaException("Permissão negada para excluir este usuário");
        }
        usuarioDeletado.setStatus("D");
        usuarioRepository.save(usuarioDeletado);
        Map<String, Object> dadosEmail = new HashMap<>();
        dadosEmail.put("nomeUsuario", usuarioDeletado.getNome());
        dadosEmail.put("email", usuarioDeletado.getEmail());
        dadosEmail.put("mensagem", "Usuario deletado com sucesso! " + usuarioDeletado.getUsername() + " \n Caso tenha alguma duvida contate o suporte: ecossossuporte@outlook.com");
        emailService.enviarEmail(dadosEmail, "Usuario deletado!", usuarioDeletado.getEmail());
    }

    public Integer getIdLoggedUser() {
        Integer findUserId = Integer.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        return findUserId;
    }


    public Usuario returnUsuario(Integer idUsuario) throws Exception{
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Nenhum usuário encontrado com o id " + idUsuario));
    }

    public boolean telefoneExiste(String telefone) {
        return usuarioRepository.findByTelefoneEquals(telefone) != null;
    }

    public boolean emailExiste(String email) throws Exception {
        return usuarioRepository.findByEmailEquals(email) != null;
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario converterDTO(UsuarioCreateDTO dto) {
        return objectMapper.convertValue(dto, Usuario.class);
    }

    public UsuarioDTO retornarDTO(Usuario usuario) {
        return objectMapper.convertValue(usuario, UsuarioDTO.class);
    }


}