package br.com.dbc.vemser.ecososapi.ecosos.service;

import br.com.dbc.vemser.ecososapi.ecosos.dto.notificacao.NotificacaoContadorDTO;
import br.com.dbc.vemser.ecososapi.ecosos.dto.notificacao.NotificacaoDTO;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Notificacao;
import br.com.dbc.vemser.ecososapi.ecosos.entity.NotificacaoContador;
import br.com.dbc.vemser.ecososapi.ecosos.enums.TipoRisco;
import br.com.dbc.vemser.ecososapi.ecosos.repository.NotificacaoRepository;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificacaoService - Test")
class NotificacaoServiceTest {
    @Mock
    private NotificacaoRepository notificacaoRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private NotificacaoService notificacaoService;

    Notificacao notificacaoMock;

    NotificacaoContador notificacaoContadorMock;

    NotificacaoDTO notificacaoDTOMock;

    NotificacaoContadorDTO notificacaoContadorDTOMock;

    @BeforeEach
    void setUp() {
        notificacaoMock = new Notificacao();
        notificacaoMock.setMensagem("Alerta de Ocorrências! ");
        notificacaoMock.setTiporisco(TipoRisco.ALTO);
        notificacaoMock.setHorario(LocalDateTime.now());

        notificacaoContadorMock = new NotificacaoContador();
        notificacaoContadorMock.setTiporisco(TipoRisco.ALTO);
        notificacaoContadorMock.setQuantidade(1);

        notificacaoDTOMock = new NotificacaoDTO();
        notificacaoDTOMock.setMensagem("Alerta de Ocorrências! ");
        notificacaoDTOMock.setTiporisco(TipoRisco.ALTO);
        notificacaoDTOMock.setHorario(LocalDateTime.now());

        notificacaoContadorDTOMock = new NotificacaoContadorDTO();
        notificacaoContadorDTOMock.setTiporisco(TipoRisco.ALTO);
        notificacaoContadorDTOMock.setQuantidade(1);
    }

    @Test
    @DisplayName("Deveria enviar mensagem de notificação")
    void deveriaEnviarMensagemDeNotificacao() {
        notificacaoService.sendMessage(notificacaoDTOMock.getMensagem(), notificacaoDTOMock.getTiporisco());
        verify(notificacaoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deveria listar por gravidade e contagem")
    void deveriaListarPorGravidadeEContagem() {
        List<NotificacaoContador> notificacaoContadores = List.of(notificacaoContadorMock);
        when(notificacaoRepository.listarPorGravidadeEContagem()).thenReturn(notificacaoContadores);
        notificacaoService.listarPorGravidadeEContagem();
        verify(notificacaoRepository, times(1)).listarPorGravidadeEContagem();
    }

    @Test
    @DisplayName("Deveria listar todas as notificações")
    void deveriaListarTodasAsNotificacoes() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Notificacao> notificacoes = List.of(notificacaoMock);
        Page<Notificacao> notificacoesPage = new PageImpl<>(notificacoes);
        when(notificacaoRepository.findAll(pageable)).thenReturn(notificacoesPage);
        notificacaoService.listarTodasAsNotificacoes(pageable);
        verify(notificacaoRepository, times(1)).findAll(pageable);
    }
}