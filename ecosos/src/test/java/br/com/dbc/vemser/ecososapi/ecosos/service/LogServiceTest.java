package br.com.dbc.vemser.ecososapi.ecosos.service;

import br.com.dbc.vemser.ecososapi.ecosos.dto.log.LogContadorDTO;
import br.com.dbc.vemser.ecososapi.ecosos.dto.log.LogDTO;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Log;
import br.com.dbc.vemser.ecososapi.ecosos.entity.LogContador;
import br.com.dbc.vemser.ecososapi.ecosos.enums.TipoUserLog;
import br.com.dbc.vemser.ecososapi.ecosos.repository.LogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogService - Test")
class LogServiceTest {
    @Mock
    private LogRepository logRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private LogService logService;

    Log logMock;
    LogContador logContadorMock;
    LogDTO logDTOMock;

    LogContadorDTO logContadorDTOMock;

    @BeforeEach
    void setUp() {
        logMock = new Log();
        logMock.setId("1");
        logMock.setDescricao("Teste");
        logMock.setData("2021-08-01");
        logMock.setTipoLog(TipoUserLog.ADMIN);

        logContadorMock = new LogContador();
        logContadorMock.setGravidade("ALTO");
        logContadorMock.setQuantidade(1);

        logDTOMock = new LogDTO();
        logDTOMock.setId("1");
        logDTOMock.setDescricao("Teste");
        logDTOMock.setData("2021-08-01");
        logDTOMock.setTipoLog(TipoUserLog.ADMIN);

        logContadorDTOMock = new LogContadorDTO();
        logContadorDTOMock.setGravidade("ALTO");
        logContadorDTOMock.setQuantidade(1);
    }

    @Test
    @DisplayName("Deveria Listar Todos os Logs")
    void deveriaListarTodosLogs() {
        when(logRepository.findAll()).thenReturn(List.of(logMock));
        when(objectMapper.convertValue(logMock, LogDTO.class)).thenReturn(logDTOMock);
        assertEquals(List.of(logDTOMock), logService.listAllLogs());
    }

    @Test
    @DisplayName("Deveria Listar Todos os Logs Paginados")
    void deveriaListarTodosLogsPaginados() {
        when(logRepository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(logMock)));
        when(objectMapper.convertValue(logMock, LogDTO.class)).thenReturn(logDTOMock);
        assertEquals(new PageImpl<>(List.of(logDTOMock)), logService.listAllLogsPageable(PageRequest.of(0, 10)));
    }

    @Test
    @DisplayName("Deveria Listar Todos os Logs Paginados Por Descrição")
    void deveriaListarTodosLogsPaginadosPorDescricao() {
        when(logRepository.findAllByDescricaoContainingIgnoreCase(logMock.getDescricao(), PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(logMock)));
        when(objectMapper.convertValue(logMock, LogDTO.class)).thenReturn(logDTOMock);
        assertEquals(new PageImpl<>(List.of(logDTOMock)), logService.listAllLogsPageableByDescricao("Teste", PageRequest.of(0, 10)));
    }

    @Test
    @DisplayName("Deveria Listar Todos os Logs Paginados Por Data")
    void deveriaListarTodosLogsPaginadosPorData() {
        when(logRepository.findAllByDataContainingIgnoreCase(logMock.getData(), PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(logMock)));
        when(objectMapper.convertValue(logMock, LogDTO.class)).thenReturn(logDTOMock);
        assertEquals(new PageImpl<>(List.of(logDTOMock)), logService.listAllLogsPageableByData("2021-08-01", PageRequest.of(0, 10)));
    }

    @Test
    @DisplayName("Deveria Listar Por Gravidade")
    void deveriaListarPorGravidade() {
        when(logRepository.listaPorGravidade()).thenReturn(List.of(logContadorMock));
        when(objectMapper.convertValue(logContadorMock, LogContadorDTO.class)).thenReturn(logContadorDTOMock);
        assertEquals(List.of(logContadorDTOMock), logService.listarPorGravidade());
    }

    @Test
    @DisplayName("Deveria Listar Por Tipo User Log")
    void deveriaListarPorTipoUserLog() {
        when(logRepository.listaPorTipoUserLog()).thenReturn(List.of(logContadorMock));
        when(objectMapper.convertValue(logContadorMock, LogContadorDTO.class)).thenReturn(logContadorDTOMock);
        assertEquals(List.of(logContadorDTOMock), logService.listarPorTipoUserLog());
    }

    @Test
    @DisplayName("Deveria Listar Por Gravidade Alto")
    void deveriaListarPorGravidadeAlto() {
        when(logRepository.listaPorGravidadeAlto()).thenReturn(List.of(logContadorMock));
        when(objectMapper.convertValue(logContadorMock, LogContadorDTO.class)).thenReturn(logContadorDTOMock);
        assertEquals(List.of(logContadorDTOMock), logService.listarPorGravidadeAlto());
    }

    @Test
    @DisplayName("Deveria Listar Por Gravidade Baixo")
    void deveriaListarPorGravidadeBaixo() {
        when(logRepository.listaPorGravidadeBaixo()).thenReturn(List.of(logContadorMock));
        when(objectMapper.convertValue(logContadorMock, LogContadorDTO.class)).thenReturn(logContadorDTOMock);
        assertEquals(List.of(logContadorDTOMock), logService.listarPorGravidadeBaixo());
    }
}