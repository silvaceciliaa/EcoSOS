package br.com.dbc.vemser.ecososapi.ecosos.controller;

import br.com.dbc.vemser.ecososapi.ecosos.controller.interfaces.IPaginacaoController;
import br.com.dbc.vemser.ecososapi.ecosos.dto.usuario.UsuarioDTO;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Comentario;
import br.com.dbc.vemser.ecososapi.ecosos.entity.Ocorrencia;
import br.com.dbc.vemser.ecososapi.ecosos.service.ComentarioService;
import br.com.dbc.vemser.ecososapi.ecosos.service.OcorrenciaService;
import br.com.dbc.vemser.ecososapi.ecosos.service.UsuarioAdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paginacao")
@Tag(name = "Paginação")
@RequiredArgsConstructor
public class PaginacaoController implements IPaginacaoController {

    private final OcorrenciaService ocorrenciaService;
    private final ComentarioService comentarioService;
    private final UsuarioAdminService usuarioAdminService;

    @GetMapping("/usuarios")
    public ResponseEntity<Page<UsuarioDTO>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UsuarioDTO> usuarios = usuarioAdminService.listarTodos(pageable);
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/ocorrencias")
    public ResponseEntity<Page<Ocorrencia>> ocorrenciasPaginadas(
            @PageableDefault @SortDefault(sort = "idOcorrencia") Pageable pageable
    ) {
        return ResponseEntity.ok(ocorrenciaService.listar(pageable));
    }

    @GetMapping("/comentarios")
    public ResponseEntity<Page<Comentario>> comentariosPaginados(
            @PageableDefault @SortDefault(sort = "idComentario") Pageable pageable
    ) {
        return ResponseEntity.ok(comentarioService.listar(pageable));
    }
}
