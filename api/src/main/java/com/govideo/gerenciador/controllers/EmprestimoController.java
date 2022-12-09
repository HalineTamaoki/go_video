package com.govideo.gerenciador.controllers;

import com.govideo.gerenciador.dtos.EmprestimoDTO;
import com.govideo.gerenciador.entities.Usuario;
import com.govideo.gerenciador.exceptions.EquipamentoNaoDisponivelException;
import com.govideo.gerenciador.services.EmprestimoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Tag(name = "Empréstimos Endpoint")
@RestController
@RequestMapping("/emprestimos")
@SecurityRequirement(name = "bearer-key")
public class EmprestimoController {

    @Autowired
    EmprestimoService emprestimoService;

    @GetMapping
    @Operation(summary = "Listar todos os empréstimos")
    public ResponseEntity<Page<EmprestimoDTO>> consultar(@PageableDefault(sort = "id", direction = Sort.Direction.DESC, page = 0, size = 5) Pageable paginacao) {
        Page<EmprestimoDTO> emprestimoDTOS = emprestimoService.consultar(paginacao);
        return ResponseEntity.ok().body(emprestimoDTOS);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar empréstimo por ID")
    public ResponseEntity<EmprestimoDTO> consultarPorId(@PathVariable("id") Long id) {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(emprestimoService.consultarPorIdRetornarDTO(id, usuarioLogado));
    }

    @GetMapping("/encerrados")
    @Operation(summary = "Listar empréstimos encerrados")
    public ResponseEntity<Page<EmprestimoDTO>> consultarEncerrados(@PageableDefault(sort = "id", direction = Sort.Direction.DESC, page = 0, size = 5) Pageable paginacao) {
        return ResponseEntity.ok().body(emprestimoService.consultarEncerrados(paginacao));
    }

    @GetMapping("/vigentes")
    @Operation(summary = "Listar empréstimos vigentes")
    public ResponseEntity<Page<EmprestimoDTO>> consultarVigentes(@PageableDefault(sort = "id", direction = Sort.Direction.DESC, page = 0, size = 5) Pageable paginacao) {
        return ResponseEntity.ok().body(emprestimoService.consultarVigentes(paginacao));
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Listar empréstimos por usuário")
    public ResponseEntity<Page<EmprestimoDTO>> consultarPorUsuario(@PathVariable("idUsuario") Long idUsuario, @PageableDefault(sort = "id", direction = Sort.Direction.DESC, page = 0, size = 5) Pageable paginacao) {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(emprestimoService.consultarEmprestimosPorUsuario(idUsuario, usuarioLogado, paginacao));
    }

    @GetMapping("/encerrados/usuario/{idUsuario}")
    @Operation(summary = "Listar empréstimos encerrados por usuário")
    public ResponseEntity<Page<EmprestimoDTO>> consultarEncerradosPorUsuario(@PathVariable("idUsuario") Long idUsuario, @PageableDefault(sort = "id", direction = Sort.Direction.DESC, page = 0, size = 5) Pageable paginacao) {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(emprestimoService.consultarEmprestimosEncerradosPorUsuario(idUsuario, usuarioLogado, paginacao));
    }

    @GetMapping("/vigentes/usuario/{idUsuario}")
    @Operation(summary = "Listar empréstimos vigentes por usuário")
    public ResponseEntity<Page<EmprestimoDTO>> consultarVigentesPorUsuario(@PathVariable("idUsuario") Long idUsuario, @PageableDefault(sort = "id", direction = Sort.Direction.DESC, page = 0, size = 5) Pageable paginacao) {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(emprestimoService.consultarEmprestimosVigentesPorUsuario(idUsuario, usuarioLogado, paginacao));
    }

    @PostMapping("/{idEquipamento}")
    @Operation(summary = "Cadastrar empréstimo")
    public ResponseEntity<EmprestimoDTO> cadastrar(@PathVariable("idEquipamento") Long idEquipamento, UriComponentsBuilder uriBuilder) throws EquipamentoNaoDisponivelException {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmprestimoDTO emprestimoDTO = emprestimoService.cadastrar(idEquipamento, usuarioLogado);
        URI uri = uriBuilder.path("/emprestimos/{id}").buildAndExpand(emprestimoDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(emprestimoDTO);
    }

    @PutMapping("/encerrar/{id}")
    @Operation(summary = "Encerrar empréstimo")
    public ResponseEntity<EmprestimoDTO> encerrar(@PathVariable Long id) {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(emprestimoService.encerrar(id, usuarioLogado));
    }

}
