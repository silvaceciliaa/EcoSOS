package br.com.dbc.vemser.ecososapi.ecosos.service;

import br.com.dbc.vemser.ecososapi.ecosos.dto.ocorrencia.OcorrenciaCreateDTO;
import br.com.dbc.vemser.ecososapi.ecosos.dto.ocorrencia.OcorrenciaDTO;
import br.com.dbc.vemser.ecososapi.ecosos.dto.ocorrencia.OcorrenciaUpdateDTO;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Endereco;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Ocorrencia;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Usuario;
import br.com.dbc.vemser.ecososapi.ecosos.exceptions.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.ecososapi.ecosos.exceptions.PermissaoNegadaException;
import br.com.dbc.vemser.ecososapi.ecosos.exceptions.RegraDeNegocioException;
import br.com.dbc.vemser.ecososapi.ecosos.repository.OcorrenciaRepository;
import br.com.dbc.vemser.ecososapi.ecosos.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OcorrenciaService {

  private final OcorrenciaRepository ocorrenciaRepository;
  private final ObjectMapper objectMapper;
  private final EnderecoService enderecoService;
  private final UsuarioComumService usuarioComumService;
  private final NotificacaoService notificacaoService;
  private final UsuarioRepository usuarioRepository;
  private final Character ATIVA = '1';
  private final Character INATIVA = '0';

  public OcorrenciaDTO adicionar(OcorrenciaCreateDTO ocorrenciaCreateDTO) throws Exception {
    Optional<Usuario> usuarioOptional = usuarioRepository.findById(ocorrenciaCreateDTO.getIdUsuario());
    if (usuarioOptional.isEmpty()) {
      throw new IllegalArgumentException("O ID do usuário fornecido não existe.");
    }

    Endereco endereco = enderecoService.getEndereco(ocorrenciaCreateDTO.getIdEndereco());
    Usuario usuario = usuarioOptional.get();
    Ocorrencia ocorrencia = converterDTO(ocorrenciaCreateDTO);
    ocorrencia.setEndereco(endereco);
    ocorrencia.setUsuario(usuario);
    ocorrencia.setCriadoEm(new Timestamp(System.currentTimeMillis()));
    ocorrencia.setStatus(ATIVA);
    notificacaoService.sendMessage(ocorrencia.getNome(), ocorrencia.getGravidade());
    return retornarDTO(ocorrenciaRepository.save(ocorrencia));
  }

  public List<OcorrenciaDTO> listarTodas() throws Exception{
    List<Ocorrencia> ocorrenciaList = ocorrenciaRepository.findAll();
    if (ocorrenciaList.isEmpty()) {
      throw new EntidadeNaoEncontradaException("Nenhuma Ocorrência Encontrada!");
    }
    return ocorrenciaList.stream()
            .map(this::retornarDTO)
            .toList();
  }

  public List<OcorrenciaDTO> listarAtivasPorId(Integer idOcorrencia) throws Exception{

    return listarAtivas().stream()
            .filter(ocorrencia -> ocorrencia.getIdOcorrencia()
            .equals(idOcorrencia)).toList();
  }

  public List<OcorrenciaDTO> listarAtivas() throws Exception{
    List<Ocorrencia> ocorrenciaList = ocorrenciaRepository.findAllByStatusEquals(ATIVA);
    if (ocorrenciaList.isEmpty()) {
      throw new EntidadeNaoEncontradaException("Nenhuma Ocorrência Encontrada!");
    }
    return ocorrenciaList.stream()
            .map(this::retornarDTO)
            .toList();
  }

  public Page<Ocorrencia> listar(Pageable pageable){
    return ocorrenciaRepository.findAll(pageable);
  }

  public OcorrenciaDTO listarPorId(Integer idOcorrencia) throws Exception {
    Ocorrencia ocorrenciaAchada = getOcorrencia(idOcorrencia);
    return retornarDTO(ocorrenciaAchada);
  }

  public OcorrenciaDTO atualizarParaAdmin(Integer idOcorrencia, OcorrenciaUpdateDTO ocorrenciaUpdateDTO) throws Exception {
    Ocorrencia ocorrenciaRecuperada = getOcorrencia(idOcorrencia);

    if(ocorrenciaRecuperada.getStatus() == '0') {
      throw new RegraDeNegocioException("Ocorrência não existe no sistema");
    }

    return getOcorrenciaDTO(ocorrenciaUpdateDTO, ocorrenciaRecuperada);

  }

  public OcorrenciaDTO atualizarParaComum(Integer idOcorrencia, OcorrenciaUpdateDTO ocorrenciaUpdateDTO) throws Exception {
    Ocorrencia ocorrenciaRecuperada = getOcorrencia(idOcorrencia);

    if(ocorrenciaRecuperada.getStatus() == INATIVA) {
      throw new RegraDeNegocioException("Ocorrência não existe no sistema");
    }

    if(!Objects.equals(ocorrenciaRecuperada.getIdUsuario(), usuarioComumService.getIdLoggedUser())) {
      throw new PermissaoNegadaException("Você não tem permissão para atualizar uma ocorrência que não foi criada por você");
    }

    return getOcorrenciaDTO(ocorrenciaUpdateDTO, ocorrenciaRecuperada);
  }

  private OcorrenciaDTO getOcorrenciaDTO(OcorrenciaUpdateDTO ocorrenciaUpdateDTO, Ocorrencia ocorrenciaRecuperada) {
    if(ocorrenciaUpdateDTO.getNome() != null){
      ocorrenciaRecuperada.setNome(ocorrenciaUpdateDTO.getNome());
    }
    if(ocorrenciaUpdateDTO.getIdEndereco() != null){
      ocorrenciaRecuperada.setIdEndereco(ocorrenciaUpdateDTO.getIdEndereco());
    }
    if(ocorrenciaUpdateDTO.getGravidade() != null){
      ocorrenciaRecuperada.setGravidade(ocorrenciaUpdateDTO.getGravidade());
    }
    if (ocorrenciaUpdateDTO.getDescricao() != null) {
      ocorrenciaRecuperada.setDescricao(ocorrenciaUpdateDTO.getDescricao());
    }
    if(ocorrenciaUpdateDTO.getTipo() != null) {
      ocorrenciaRecuperada.setTipo(ocorrenciaUpdateDTO.getTipo());
    }

    ocorrenciaRecuperada.setAtualizadoEm(Timestamp.valueOf(LocalDateTime.now()));
    return retornarDTO(ocorrenciaRepository.save(ocorrenciaRecuperada));
  }


  public void removerDeAdmin(Integer idOcorrencia) throws Exception{
    Ocorrencia ocorrenciaRecuperada = getOcorrencia(idOcorrencia);
    ocorrenciaRecuperada.setStatus(INATIVA);
    ocorrenciaRepository.save(ocorrenciaRecuperada);
  }

  public void removerDeComum(Integer idOcorrencia) throws Exception{
    Ocorrencia ocorrenciaRecuperada = getOcorrencia(idOcorrencia);

    if(Objects.equals(ocorrenciaRecuperada.getIdUsuario(), usuarioComumService.getIdLoggedUser())){
      ocorrenciaRecuperada.setStatus(INATIVA);
      ocorrenciaRepository.save(ocorrenciaRecuperada);
    } else {
      throw new PermissaoNegadaException("Você não tem permissão para deletar essa ocorrência");
    }
  }

  public Ocorrencia getOcorrencia(Integer idOcorrencia) throws Exception{
    return ocorrenciaRepository.findById(idOcorrencia)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Nenhuma ocorrência encontrada com o id " + idOcorrencia));
  }

  public Ocorrencia converterDTO(OcorrenciaCreateDTO dto) {
    return objectMapper.convertValue(dto, Ocorrencia.class);
  }

  public OcorrenciaDTO retornarDTO(Ocorrencia ocorrencia) {
    return objectMapper.convertValue(ocorrencia, OcorrenciaDTO.class);
  }
}