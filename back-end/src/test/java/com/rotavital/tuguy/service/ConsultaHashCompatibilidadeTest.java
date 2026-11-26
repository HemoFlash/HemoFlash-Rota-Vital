package com.rotavital.tuguy.service;

import com.rotavital.tuguy.model.bolsahemocomponente;
import com.rotavital.tuguy.model.requisicao;
import com.rotavital.tuguy.repository.bolsahemocomponenterepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConsultaHashCompatibilidadeTest {

    @Test
    void buscaTodosOsTiposDoadoresCompativeisDoMesmoComponenteEmFefo() {
        var indice = new EspalhamentoHash();
        var repository = mock(bolsahemocomponenterepository.class);
        var service = new bolsahemocomponenteservice(repository,
                new CompatibilidadeABOService(),
                new bolsahemocomponentefefoservice(),
                indice);
        var oNegativa = bolsa("O-", "O-", 3);
        var oPositiva = bolsa("O+", "O+", 5);
        var aNegativa = bolsa("A-", "A-", 8);
        var aPositiva = bolsa("A+", "A+", 12);
        var bPositiva = bolsa("B+", "B+", 1);
        var plasma = bolsa("PLASMA", "O-", 2, "Plasma");
        indice.reconstruir(List.of(oNegativa, oPositiva, aNegativa, aPositiva, bPositiva, plasma));
        requisicao pedido = new requisicao();
        pedido.setTipoSanguineo("A+");
        pedido.setComponente("Hemácias");

        assertEquals(List.of(oNegativa, oPositiva, aNegativa, aPositiva),
                service.consultarPorHashCompatibilidadeFefo(pedido));
    }

    @Test
    void consultaSemComponenteNaoFazVarreduraDoRepositorio() {
        var repository = mock(bolsahemocomponenterepository.class);
        var service = new bolsahemocomponenteservice(repository,
                new CompatibilidadeABOService(),
                new bolsahemocomponentefefoservice(),
                new EspalhamentoHash());
        requisicao pedido = new requisicao();
        pedido.setTipoSanguineo("A+");

        assertEquals(List.of(), service.consultarPorHashCompatibilidadeFefo(pedido));
        verify(repository, never()).findAll();
    }

    @Test
    void alocarRejeitaBolsaDeComponenteDiferente() {
        var repository = mock(bolsahemocomponenterepository.class);
        var service = new bolsahemocomponenteservice(repository,
                new CompatibilidadeABOService(), new bolsahemocomponentefefoservice(),
                new EspalhamentoHash());
        var bolsa = bolsa("HEM-1", "O-", 3);
        bolsa.setId(1L);
        requisicao pedido = pedido("A+", "PLASMA");
        when(repository.findById(1L)).thenReturn(Optional.of(bolsa));

        assertThrows(IllegalArgumentException.class, () -> service.alocar(1L, pedido));
        verify(repository, never()).save(bolsa);
    }

    @Test
    void alocarRejeitaBolsaQueNaoSejaAProximaPorFefo() {
        var indice = new EspalhamentoHash();
        var repository = mock(bolsahemocomponenterepository.class);
        var service = new bolsahemocomponenteservice(repository,
                new CompatibilidadeABOService(), new bolsahemocomponentefefoservice(), indice);
        var primeira = bolsa("P1", "O-", 3, "Plasma");
        primeira.setId(1L);
        var posterior = bolsa("P2", "O-", 10, "Plasma");
        posterior.setId(2L);
        indice.reconstruir(List.of(primeira, posterior));
        requisicao pedido = pedido("A+", "PLASMA");
        when(repository.findById(2L)).thenReturn(Optional.of(posterior));

        assertThrows(IllegalStateException.class, () -> service.alocar(2L, pedido));
        verify(repository, never()).save(posterior);
    }

    private bolsahemocomponente bolsa(String codigo, String tipo, int validadeDias) {
        return bolsa(codigo, tipo, validadeDias, "Hemácias");
    }

    private bolsahemocomponente bolsa(String codigo, String tipo, int validadeDias, String componente) {
        return new bolsahemocomponente(null, codigo, tipo, componente, 250,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(validadeDias),
                "DISPONIVEL", null);
    }

    private requisicao pedido(String tipo, String componente) {
        requisicao pedido = new requisicao();
        pedido.setId(50L);
        pedido.setTipoSanguineo(tipo);
        pedido.setComponente(componente);
        return pedido;
    }
}
