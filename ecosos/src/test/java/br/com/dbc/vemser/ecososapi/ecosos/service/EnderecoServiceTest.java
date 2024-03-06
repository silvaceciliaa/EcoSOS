package br.com.dbc.vemser.ecososapi.ecosos.service;

import br.com.dbc.vemser.ecososapi.ecosos.dto.endereco.EnderecoCreateDTO;
import br.com.dbc.vemser.ecososapi.ecosos.dto.endereco.EnderecoDTO;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Endereco;
import br.com.dbc.vemser.ecososapi.ecosos.exceptions.RegraDeNegocioException;
import br.com.dbc.vemser.ecososapi.ecosos.repository.EnderecoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
@DisplayName("EnderecoService - Test")
class EnderecoServiceTest {
    @Mock
    private  EnderecoRepository enderecoRepository;
    @Mock
    private  ObjectMapper objectMapper;

    @InjectMocks
    private EnderecoService enderecoService;

    @Test
    @DisplayName("Deveria criar um novo endereço com sucesso")
    public void deveriaCriarEndereco() throws Exception {

        // ARRANGE - GIVEN
        EnderecoCreateDTO enderecoCreateDTOMock = retornarEnderecoCreateDTO();
        Endereco enderecoEntityMock = retornarEnderecoEntity();
        EnderecoDTO enderecoDTOMock = reotornarEnderecoDTO();

        // ACT - WHEN

        when(objectMapper.convertValue(enderecoCreateDTOMock, Endereco.class)).thenReturn(enderecoEntityMock);
        when(enderecoRepository.save(any())).thenReturn(enderecoEntityMock);
        when(objectMapper.convertValue(enderecoEntityMock, EnderecoDTO.class)).thenReturn(enderecoDTOMock);

        EnderecoDTO enderecoDTOCriado =  enderecoService.adicionar(enderecoCreateDTOMock);

        // ASSERT - THEN
        assertNotNull(enderecoDTOCriado);
        assertEquals(enderecoDTOCriado, enderecoDTOMock);
    }


    @Test
    public void deveriaListarComSucesso() throws Exception {
        List<Endereco> listaMock = List.of(retornarEnderecoEntity(), retornarEnderecoEntity(), retornarEnderecoEntity());

        when(enderecoRepository.findAll()).thenReturn(listaMock);

        List<EnderecoDTO> listaDTORetornada = enderecoService.listar();

        assertNotNull(listaDTORetornada);
        assertEquals(listaMock.size(), listaDTORetornada.size());

    }

    @Test
    public void deveriaRetornarEnderecoPorId() throws Exception {
        Integer idAleatorio = new Random().nextInt();

        Endereco enderecoMock = retornarEnderecoEntity();
        enderecoMock.setIdEndereco(idAleatorio);

        when(enderecoRepository.findAll()).thenReturn(Collections.singletonList(enderecoMock));

        Endereco enderecoRetornado = enderecoService.getEndereco(idAleatorio);

        assertEquals(enderecoMock, enderecoRetornado);
    }

    @Test
    public void deveriaLancarExcecaoQuandoNenhumEnderecoEncontrado() {
        Integer idAleatorio = new Random().nextInt();

        when(enderecoRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(RegraDeNegocioException.class, () -> enderecoService.getEndereco(idAleatorio));
    }



    public static EnderecoCreateDTO retornarEnderecoCreateDTO(){
        EnderecoCreateDTO endereco = new EnderecoCreateDTO();
        endereco.setCep("40854891");
        endereco.setEstado("SC");
        endereco.setCidade("Florianópolis");
        endereco.setBairro("Monte Verde");

        return endereco;
    }


   public static Endereco retornarEnderecoEntity() {
       Endereco endereco = new Endereco();
       endereco.setCep("40854891");
       endereco.setEstado("SC");
       endereco.setCidade("Florianópolis");
       endereco.setBairro("Monte Verde");

       return endereco;
   }

   public static EnderecoDTO reotornarEnderecoDTO(){
       EnderecoDTO enderecoDTO = new EnderecoDTO();
       enderecoDTO.setIdEndereco(100);
       enderecoDTO.setCep("40854891");
       enderecoDTO.setEstado("SC");
       enderecoDTO.setCidade("Florianópolis");
       enderecoDTO.setBairro("Monte Verde");
       return enderecoDTO;
   }


}