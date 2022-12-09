package com.govideo.gerenciador.services;

import com.govideo.gerenciador.dtos.EquipamentoDTO;
import com.govideo.gerenciador.dtos.RespostaDTO;
import com.govideo.gerenciador.entities.Emprestimo;
import com.govideo.gerenciador.entities.Equipamento;
import com.govideo.gerenciador.entities.enuns.StatusEquipamento;
import com.govideo.gerenciador.exceptions.OperacaoNaoPermitidaException;
import com.govideo.gerenciador.exceptions.RecursoNaoEncontradoException;
import com.govideo.gerenciador.forms.EquipamentoForm;
import com.govideo.gerenciador.repositories.EmprestimoRepository;
import com.govideo.gerenciador.repositories.EquipamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
public class EquipamentoServiceTest {

    @InjectMocks
    private EquipamentoService equipamentoService;

    @Mock
    private EquipamentoRepository equipamentoRepository;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    public Equipamento mockEquipamentoEntity() {
        Equipamento equipamento = new Equipamento("Pocket Cinema 6K", "Filmadora profissional Pocket Cinema 6K", "Black Magic", "Filmadoras", "https://emania.vteximg.com.br/arquivos/ids/209607");
        equipamento.setId(1L);
        return equipamento;
    }

    public EquipamentoForm mockEquipamentoForm() {
        return new EquipamentoForm("Pocket Cinema 6K", "Filmadora profissional Pocket Cinema 6K", "Black Magic", "Filmadoras", "https://emania.vteximg.com.br/arquivos/ids/209607");
    }

    public Page<Equipamento> mockEquipamentoPage() {
        return new PageImpl<>(Collections.singletonList(mockEquipamentoEntity()));
    }

    public Page<Emprestimo> mockEmprestimoPage(Equipamento equipamento) {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setEquipamento(equipamento);
        return new PageImpl<>(Collections.singletonList(emprestimo));
    }

    @Test
    public void deveriaCadastrarEquipamento() {
        when(equipamentoRepository.save(any())).thenReturn(mockEquipamentoEntity());
        EquipamentoDTO retornoEquipamento = equipamentoService.cadastrar(mockEquipamentoForm());
        assertEquals(1L, (long) retornoEquipamento.getId());
    }

    @Test
    public void deveriaRetornarEquipamentoAoBuscarPorId() {
        when(equipamentoRepository.findById(any())).thenReturn(Optional.of(mockEquipamentoEntity()));
        Equipamento retornoEquipamento = equipamentoService.consultarPorId(1L);
        assertNotNull(retornoEquipamento);
    }

    @Test
    public void naoDeveriaRetornarEquipamentoAoBuscarPorId() {
        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class, () -> {
            when(equipamentoRepository.findById(any())).thenReturn(Optional.empty());
            equipamentoService.consultarPorId(1L);
        });
        assertEquals("Equipamento não encontrado!", exception.getMessage());
    }

    @Test
    public void deveriaRetornarEquipamentoDTOAoBuscarPorId() {
        when(equipamentoRepository.findById(any())).thenReturn(Optional.of(mockEquipamentoEntity()));
        EquipamentoDTO retornoEquipamento = equipamentoService.consultarPorIdRetornarDTO(1L);
        assertNotNull(retornoEquipamento);
    }

    @Test
    public void naoDeveriaRetornarEquipamentoDTOAoBuscarPorId() {
        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class, () -> {
            when(equipamentoRepository.findById(any())).thenReturn(Optional.empty());
            equipamentoService.consultarPorIdRetornarDTO(1L);
        });
        assertEquals("Equipamento não encontrado!", exception.getMessage());
    }

    @Test
    public void deveriaRetornarTodosOsEquipamentos() {
        Pageable pageable = PageRequest.of(0, 10);
        when(equipamentoRepository.findAll(pageable)).thenReturn(mockEquipamentoPage());
        Page<EquipamentoDTO> equipamentoDTOPage = equipamentoService.consultar(pageable);
        assertEquals(1L, equipamentoDTOPage.getTotalElements());
    }

    @Test
    public void deveriaRetornarEquipamentosComStatusDisponivel() {
        Pageable paginacao = PageRequest.of(0, 10);
        when(equipamentoRepository.findAll(paginacao)).thenReturn(mockEquipamentoPage());
        when(equipamentoRepository.findByStatus(StatusEquipamento.DISPONÍVEL, paginacao)).thenReturn(mockEquipamentoPage());
        Page<EquipamentoDTO> equipamentoDTO = equipamentoService.consultarPorStatus("DISPONIVEL", paginacao);
        assertEquals(1L, equipamentoDTO.getTotalElements());
    }

    @Test
    public void deveriaRetornarEquipamentosComStatusIndisponivel() {
        Pageable paginacao = PageRequest.of(0, 10);
        when(equipamentoRepository.findAll(paginacao)).thenReturn(mockEquipamentoPage());
        when(equipamentoRepository.findByStatus(StatusEquipamento.INDISPONÍVEL, paginacao)).thenReturn(mockEquipamentoPage());
        Page<EquipamentoDTO> equipamentoDTO = equipamentoService.consultarPorStatus("INDISPONIVEL", paginacao);
        assertEquals(1L, equipamentoDTO.getTotalElements());
    }

    @Test
    public void deveriaRetornarEquipamentosComStatusInativo() {
        Pageable paginacao = PageRequest.of(0, 10);
        when(equipamentoRepository.findAll(paginacao)).thenReturn(mockEquipamentoPage());
        when(equipamentoRepository.findByStatus(StatusEquipamento.INATIVO, paginacao)).thenReturn(mockEquipamentoPage());
        Page<EquipamentoDTO> equipamentoDTO = equipamentoService.consultarPorStatus("INATIVO", paginacao);
        assertEquals(1L, equipamentoDTO.getTotalElements());
    }

    @Test
    public void deveriaAlterarEquipamentoComSucesso() {
        when(equipamentoRepository.findById(1L)).thenReturn(Optional.of(mockEquipamentoEntity()));
        when(equipamentoRepository.save(any())).thenReturn(mockEquipamentoEntity());
        EquipamentoDTO retornoEquipamento = equipamentoService.alterar(1L, mockEquipamentoForm());
        assertEquals(1L, (long) retornoEquipamento.getId());
    }

    @Test
    public void deveriaAlterarStatusDeEquipamentoComSucesso() {
        when(equipamentoRepository.findById(1L)).thenReturn(Optional.of(mockEquipamentoEntity()));
        Equipamento equipamento = mockEquipamentoEntity();
        equipamento.setStatus(StatusEquipamento.INATIVO);
        when(equipamentoRepository.save(any())).thenReturn(equipamento);
        EquipamentoDTO retornoEquipamento = equipamentoService.alterarStatus(1L, StatusEquipamento.INATIVO);
        assertEquals(StatusEquipamento.INATIVO, retornoEquipamento.getStatus());
    }

    @Test
    public void deveriaExcluirEquipamentoDisponivelSemEmprestimos() {
        Pageable paginacao = PageRequest.of(0, 10);
        Equipamento equipamento = mockEquipamentoEntity();
        when(equipamentoRepository.findById(any())).thenReturn(Optional.of(equipamento));
        when(emprestimoRepository.findByEquipamento(equipamento, paginacao)).thenReturn(Page.empty());
        doNothing().when(equipamentoRepository).delete(any());
        RespostaDTO retorno = equipamentoService.excluir(1L);
        assertEquals("Equipamento excluído com sucesso!", retorno.getMensagem());
    }

    @Test
    public void deveriaExibirMensagemAoTentarExcluirEquipamentoIndisponivel() {
        Pageable paginacao = PageRequest.of(0, 10);
        Equipamento equipamento = mockEquipamentoEntity();
        equipamento.setStatus(StatusEquipamento.INDISPONÍVEL);

        OperacaoNaoPermitidaException exception = assertThrows(OperacaoNaoPermitidaException.class, () -> {
            when(equipamentoRepository.findById(any())).thenReturn(Optional.of(equipamento));
            when(emprestimoRepository.findByEquipamento(equipamento, paginacao)).thenReturn(mockEmprestimoPage(equipamento));
            RespostaDTO retorno = equipamentoService.excluir(1L);
        });
        assertEquals("O status atual do equipamento é INDISPONÍVEL, então ele não pode ser inativado ou excluído!", exception.getMessage());

        verify(equipamentoRepository, Mockito.times(0)).delete(equipamento);
    }

    @Test
    public void deveriaInativarEquipamentoDisponivelComEmprestimos() {
        Pageable paginacao = PageRequest.of(0, 10);
        Equipamento equipamento = mockEquipamentoEntity();
        when(equipamentoRepository.findById(1L)).thenReturn(Optional.of(equipamento));
        when(equipamentoRepository.save(any())).thenReturn(equipamento);
        when(emprestimoRepository.findByEquipamento(equipamento, paginacao)).thenReturn(mockEmprestimoPage(equipamento));
        RespostaDTO retorno = equipamentoService.excluir(1L);
        assertEquals("Equipamento inativado com sucesso!", retorno.getMensagem());
        verify(equipamentoRepository, Mockito.times(1)).save(equipamento);
        verify(equipamentoRepository, Mockito.times(0)).delete(equipamento);
    }

}
