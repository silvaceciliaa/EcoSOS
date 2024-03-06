package br.com.dbc.vemser.ecososapi.ecosos.service;

import br.com.dbc.vemser.ecososapi.ecosos.entity.Cargo;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Usuario;
import br.com.dbc.vemser.ecososapi.ecosos.exceptions.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.ecososapi.ecosos.exceptions.RegraDeNegocioException;
import br.com.dbc.vemser.ecososapi.ecosos.repository.CargoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CargoService - Test")
class CargoServiceTest {

    @InjectMocks
    private CargoService cargoService;

    @Mock
    private CargoRepository cargoRepository;

    @Test
    void deveriaRetornarCargoPorId() throws Exception {

        Cargo cargoMock = new Cargo();
        Usuario usuarioTeste = new Usuario();

        cargoMock.setIdCargo(1);
        cargoMock.setNome("ROLE_ADMIN");
        cargoMock.setUsuarios(new HashSet<>());
        cargoMock.getUsuarios().add(usuarioTeste);

        usuarioTeste.setIdUsuario(1);
        usuarioTeste.setEmail("admin@admin");
        usuarioTeste.setSenha("admin12345");
        usuarioTeste.setCargos(new HashSet<>());
        usuarioTeste.getCargos().add(cargoMock);

        when(cargoRepository.findById(cargoMock.getIdCargo())).thenReturn(Optional.of(cargoMock));
        Cargo cargoEncontrado = cargoService.findCargoById(cargoMock.getIdCargo());

        verify(cargoRepository).findById(cargoMock.getIdCargo());
        assertNotNull(cargoEncontrado);
        assertDoesNotThrow(() -> new RegraDeNegocioException("Cargo não encontrado: " + cargoMock.getIdCargo()));
        assertEquals(cargoMock, cargoEncontrado);
    }

    @Test
    void deveriaRetornarExceptionAoReceberIdNaoExistente() {
        Integer idCargoAleatorio = new Random().nextInt();

        assertThrows(RegraDeNegocioException.class, () -> cargoService.findCargoById(idCargoAleatorio));
    }
}