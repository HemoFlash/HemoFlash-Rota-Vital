package com.rotavital.tuguy;

import com.rotavital.tuguy.model.requisicao;
import com.rotavital.tuguy.model.bolsahemocomponente;
import com.rotavital.tuguy.service.bolsahemocomponenteservice;
import com.rotavital.tuguy.service.requisicaoservice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import java.time.LocalDate;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:rotavitaltest")
@AutoConfigureMockMvc
class TuguyApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private requisicaoservice requisicaoService;

	@Autowired
	private bolsahemocomponenteservice bolsaService;

	@Test
	void contextLoads() {
	}

	@Test
	void paginaDeBolsasEFormularioSaoExibidos() throws Exception {
		mockMvc.perform(get("/bolsas"))
				.andExpect(status().isOk())
				.andExpect(view().name("bolsas"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bolsas cadastradas")));

		mockMvc.perform(get("/bolsas/nova"))
				.andExpect(status().isOk())
				.andExpect(view().name("bolsa-form"))
				.andExpect(content().string(containsString("Nova bolsa")))
				.andExpect(content().string(not(containsString("Requisição associada"))));
	}

	@Test
	void formularioSalvaBolsaSemRequisicaoAssociada() throws Exception {
		mockMvc.perform(post("/bolsas")
						.param("codigo", "BOLSA-TESTE")
						.param("tipoSanguineo", "O+")
						.param("componente", "Plasma")
						.param("quantidadeMl", "250")
						.param("dataColeta", "2026-10-18")
						.param("dataValidade", "2026-11-18")
						.param("status", "DISPONIVEL"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/menu"));

		mockMvc.perform(get("/menu"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("BOLSA-TESTE")));
		org.junit.jupiter.api.Assertions.assertTrue(
				bolsaService.listarTodos().stream()
						.filter(bolsa -> "BOLSA-TESTE".equals(bolsa.getCodigo()))
						.findFirst()
						.orElseThrow()
						.getRequisicao() == null);
	}

	@Test
	void formularioRejeitaCodigoDuplicadoDeBolsa() throws Exception {
		mockMvc.perform(post("/bolsas")
					.param("codigo", "BOLSA-DUPLICADA")
					.param("tipoSanguineo", "O+")
					.param("componente", "Plasma")
					.param("quantidadeMl", "250")
					.param("dataColeta", "2026-10-18")
					.param("dataValidade", "2026-11-18")
					.param("status", "DISPONIVEL"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/menu"));

		mockMvc.perform(post("/bolsas")
					.param("codigo", "BOLSA-DUPLICADA")
					.param("tipoSanguineo", "A+")
					.param("componente", "Hemácias")
					.param("quantidadeMl", "300")
					.param("dataColeta", "2026-10-19")
					.param("dataValidade", "2026-11-19")
					.param("status", "DISPONIVEL"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/menu"))
				.andExpect(flash().attribute("erroBolsa",
						"Já existe uma bolsa cadastrada com este código."));
	}

	@Test
	void formularioSalvaTipoSanguineoDaRequisicao() throws Exception {
		mockMvc.perform(get("/requisicoes"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Tipo sanguíneo ABO/Rh")));

		mockMvc.perform(post("/requisicoes")
						.param("titulo", "Requisição com tipo")
						.param("descricao", "Teste de cadastro do tipo sanguíneo")
						.param("prioridade", "Alta")
						.param("tipoSanguineo", "B-"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/menu"));

		mockMvc.perform(get("/menu"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Requisição com tipo")))
				.andExpect(content().string(containsString("B-")));
	}

	@Test
	void requisicaoPodeSerEditadaERemovida() throws Exception {
		requisicao requisicao = requisicaoService.salvar(
				new requisicao(null, "Requisição editável", "Descrição original", "Baixa", "O+"));

		mockMvc.perform(get("/requisicoes/{id}/editar", requisicao.getId()))
				.andExpect(status().isOk())
				.andExpect(view().name("requisicao"))
				.andExpect(content().string(containsString("Editar requisição")));

		mockMvc.perform(post("/requisicoes/{id}", requisicao.getId())
						.param("titulo", "Requisição atualizada")
						.param("descricao", "Descrição modificada")
						.param("prioridade", "Alta")
						.param("tipoSanguineo", "AB+"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/menu"));

		requisicao atualizada = requisicaoService.buscarPorId(requisicao.getId());
		org.junit.jupiter.api.Assertions.assertEquals("Requisição atualizada", atualizada.getTitulo());
		org.junit.jupiter.api.Assertions.assertEquals("AB+", atualizada.getTipoSanguineo());

		mockMvc.perform(post("/requisicoes/{id}/remover", requisicao.getId()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/menu"));
		org.junit.jupiter.api.Assertions.assertThrows(
				RuntimeException.class, () -> requisicaoService.buscarPorId(requisicao.getId()));
	}

	@Test
	void requisicaoComBolsaAssociadaNaoPodeSerRemovida() throws Exception {
		requisicao requisicao = requisicaoService.salvar(
				new requisicao(null, "Requisição vinculada", "Possui uma bolsa associada", "Alta", "O-"));
		bolsaService.criar(new bolsahemocomponente(
				null, "BOLSA-VINCULADA-" + requisicao.getId(), "O-", "Hemácias", 250,
				LocalDate.now(), LocalDate.now().plusDays(30), "DISPONIVEL", requisicao));

		mockMvc.perform(post("/requisicoes/{id}/remover", requisicao.getId()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/menu"))
				.andExpect(flash().attribute("erroRequisicao",
						"Não é possível remover esta requisição porque existem bolsas associadas."));
		org.junit.jupiter.api.Assertions.assertEquals(
				"Requisição vinculada", requisicaoService.buscarPorId(requisicao.getId()).getTitulo());
	}

	@Test
	void bolsaPodeSerEditadaERemovida() throws Exception {
		requisicao requisicao = requisicaoService.salvar(
				new requisicao(null, "Requisição da bolsa editável", "Requisição para teste CRUD", "Alta", "A+"));
		bolsahemocomponente bolsa = bolsaService.criar(new bolsahemocomponente(
				null, "BOLSA-EDITAVEL-" + requisicao.getId(), "A+", "Hemácias", 250,
				LocalDate.now(), LocalDate.now().plusDays(30), "DISPONIVEL", requisicao));

		mockMvc.perform(get("/bolsas/{id}/editar", bolsa.getId()))
				.andExpect(status().isOk())
				.andExpect(view().name("bolsa-form"))
				.andExpect(content().string(containsString("Editar bolsa")));

		mockMvc.perform(post("/bolsas/{id}", bolsa.getId())
						.param("codigo", bolsa.getCodigo())
						.param("tipoSanguineo", "A+")
						.param("componente", "Hemácias")
						.param("quantidadeMl", "375")
						.param("dataColeta", LocalDate.now().toString())
						.param("dataValidade", LocalDate.now().plusDays(30).toString())
						.param("status", "DISPONIVEL"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/menu"));

		org.junit.jupiter.api.Assertions.assertEquals(
				375, bolsaService.buscarPorId(bolsa.getId()).orElseThrow().getQuantidadeMl());

		mockMvc.perform(post("/bolsas/{id}/remover", bolsa.getId()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/menu"));
		org.junit.jupiter.api.Assertions.assertTrue(bolsaService.buscarPorId(bolsa.getId()).isEmpty());
	}

	@Test
	void paginaDeAlocacaoExibeSomenteBolsasCompativeisComTipoDaRequisicao() throws Exception {
		requisicao requisicao = requisicaoService.salvar(
				new requisicao(null, "Requisição tipo O negativo", "Teste de compatibilidade ABO e Rh", "Alta", "O-"));
		LocalDate coleta = LocalDate.now();
		LocalDate validade = coleta.plusDays(30);

		bolsaService.criar(new bolsahemocomponente(
				null, "COMPATIVEL-" + requisicao.getId(), "O-", "Hemácias", 250,
				coleta, validade, "DISPONIVEL", null));
		bolsaService.criar(new bolsahemocomponente(
				null, "INCOMPATIVEL-" + requisicao.getId(), "A-", "Hemácias", 250,
				coleta, validade, "DISPONIVEL", null));

		mockMvc.perform(get("/requisicoes/{id}/alocar-bolsa", requisicao.getId()))
				.andExpect(status().isOk())
				.andExpect(view().name("bolsas"))
				.andExpect(content().string(containsString("COMPATIVEL-" + requisicao.getId())))
				.andExpect(content().string(not(containsString("INCOMPATIVEL-" + requisicao.getId()))))
				.andExpect(content().string(containsString(">Alocar</button>")))
				.andExpect(content().string(not(containsString("Editar"))))
				.andExpect(content().string(not(containsString("Remover"))));
	}

	@Test
	void bolsasCompativeisNaAlocacaoSaoOrdenadasPorValidadeCrescente() throws Exception {
		requisicao requisicao = requisicaoService.salvar(
				new requisicao(null, "Requisição ordenação FEFO", "Teste de validade FEFO", "Alta", "O-"));
		LocalDate coleta = LocalDate.now();
		bolsaService.criar(new bolsahemocomponente(null, "FEFO-TARDIA-" + requisicao.getId(), "O-", "Hemácias",
				250, coleta, coleta.plusDays(20), "DISPONIVEL", null));
		bolsaService.criar(new bolsahemocomponente(null, "FEFO-PROXIMA-" + requisicao.getId(), "O-", "Hemácias",
				250, coleta, coleta.plusDays(5), "DISPONIVEL", null));

		String conteudo = mockMvc.perform(get("/requisicoes/{id}/alocar-bolsa", requisicao.getId()))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		org.junit.jupiter.api.Assertions.assertTrue(
				conteudo.indexOf("FEFO-PROXIMA-" + requisicao.getId())
						< conteudo.indexOf("FEFO-TARDIA-" + requisicao.getId()));
	}

	@Test
	void alocarVinculaBolsaEImpedeQueOutraRequisicaoAloqueNovamente() throws Exception {
		requisicao primeira = requisicaoService.salvar(
				new requisicao(null, "Requisição alocadora", "Receberá a bolsa compatível", "Alta", "A+"));
		requisicao segunda = requisicaoService.salvar(
				new requisicao(null, "Outra requisição", "Não deve receber a bolsa já alocada", "Media", "A+"));
		bolsahemocomponente bolsa = bolsaService.criar(new bolsahemocomponente(
				null, "BOLSA-ALOCAVEL-" + primeira.getId(), "O+", "Hemácias", 250,
				LocalDate.now(), LocalDate.now().plusDays(10), "DISPONIVEL", null));

		mockMvc.perform(post("/requisicoes/{requisicaoId}/alocar-bolsa/{bolsaId}",
						primeira.getId(), bolsa.getId()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/requisicoes/" + primeira.getId() + "/alocar-bolsa"))
				.andExpect(flash().attribute("sucessoAlocacao",
						"Bolsa alocada para a requisição com sucesso."));

		bolsahemocomponente alocada = bolsaService.buscarPorId(bolsa.getId()).orElseThrow();
		org.junit.jupiter.api.Assertions.assertEquals("ALOCADA", alocada.getStatus());
		org.junit.jupiter.api.Assertions.assertEquals(primeira.getId(), alocada.getRequisicao().getId());

		mockMvc.perform(get("/requisicoes/{id}/alocar-bolsa", segunda.getId()))
				.andExpect(status().isOk())
				.andExpect(content().string(not(containsString("BOLSA-ALOCAVEL-" + primeira.getId()))));

		mockMvc.perform(post("/requisicoes/{requisicaoId}/alocar-bolsa/{bolsaId}",
						segunda.getId(), bolsa.getId()))
				.andExpect(status().is3xxRedirection())
				.andExpect(flash().attribute("erroAlocacao",
						"A bolsa não está mais disponível para alocação"));
		org.junit.jupiter.api.Assertions.assertEquals(primeira.getId(),
				bolsaService.buscarPorId(bolsa.getId()).orElseThrow().getRequisicao().getId());
	}

	@Test
	void menuOrdenaRequisicoesDaMaiorParaMenorPrioridade() throws Exception {
		requisicao baixa = requisicaoService.salvar(
				new requisicao(null, "Prioridade baixa no menu", "Solicitação de baixa prioridade", "Baixa", "A+"));
		requisicao media = requisicaoService.salvar(
				new requisicao(null, "Prioridade média no menu", "Solicitação de média prioridade", "Média", "A+"));
		requisicao alta = requisicaoService.salvar(
				new requisicao(null, "Prioridade alta no menu", "Solicitação de alta prioridade", "Alta", "A+"));

		String conteudo = mockMvc.perform(get("/menu"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		int indiceAlta = conteudo.indexOf(alta.getTitulo());
		int indiceMedia = conteudo.indexOf(media.getTitulo());
		int indiceBaixa = conteudo.indexOf(baixa.getTitulo());
		org.junit.jupiter.api.Assertions.assertTrue(indiceAlta < indiceMedia);
		org.junit.jupiter.api.Assertions.assertTrue(indiceMedia < indiceBaixa);
	}

	@Test
	void alocarOutraBolsaDesalocaAnteriorDaMesmaRequisicao() throws Exception {
		requisicao requisicao = requisicaoService.salvar(
				new requisicao(null, "Requisição troca de bolsa", "Substituir a bolsa alocada", "Alta", "A+"));
		bolsahemocomponente anterior = bolsaService.criar(new bolsahemocomponente(
				null, "BOLSA-ANTERIOR-" + requisicao.getId(), "O+", "Hemácias", 250,
				LocalDate.now(), LocalDate.now().plusDays(20), "ALOCADA", requisicao));
		bolsahemocomponente nova = bolsaService.criar(new bolsahemocomponente(
				null, "BOLSA-NOVA-" + requisicao.getId(), "A+", "Hemácias", 250,
				LocalDate.now(), LocalDate.now().plusDays(10), "DISPONIVEL", null));

		mockMvc.perform(post("/requisicoes/{requisicaoId}/alocar-bolsa/{bolsaId}",
						requisicao.getId(), nova.getId()))
				.andExpect(status().is3xxRedirection());

		bolsahemocomponente anteriorAtualizada = bolsaService.buscarPorId(anterior.getId()).orElseThrow();
		bolsahemocomponente novaAtualizada = bolsaService.buscarPorId(nova.getId()).orElseThrow();
		org.junit.jupiter.api.Assertions.assertEquals("DISPONIVEL", anteriorAtualizada.getStatus());
		org.junit.jupiter.api.Assertions.assertNull(anteriorAtualizada.getRequisicao());
		org.junit.jupiter.api.Assertions.assertEquals("ALOCADA", novaAtualizada.getStatus());
		org.junit.jupiter.api.Assertions.assertEquals(requisicao.getId(), novaAtualizada.getRequisicao().getId());
	}

	@Test
	void menuMostraEstadoECodigoDaBolsaAlocadaPorRequisicao() throws Exception {
		requisicao alocada = requisicaoService.salvar(
				new requisicao(null, "Requisição já alocada", "Tem uma bolsa vinculada", "Alta", "O+"));
		requisicao semAlocacao = requisicaoService.salvar(
				new requisicao(null, "Requisição sem alocação", "Ainda sem bolsa", "Baixa", "A+"));
		bolsaService.criar(new bolsahemocomponente(
				null, "CODIGO-ALOCADO-" + alocada.getId(), "O+", "Hemácias", 250,
				LocalDate.now(), LocalDate.now().plusDays(10), "ALOCADA", alocada));

		String conteudo = mockMvc.perform(get("/menu"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Alocação")))
				.andExpect(content().string(containsString("Bolsa alocada")))
				.andReturn()
				.getResponse()
				.getContentAsString();

		int indiceAlocada = conteudo.indexOf(alocada.getTitulo());
		int indiceSemAlocacao = conteudo.indexOf(semAlocacao.getTitulo());
		org.junit.jupiter.api.Assertions.assertTrue(indiceAlocada >= 0);
		org.junit.jupiter.api.Assertions.assertTrue(indiceSemAlocacao >= 0);

		String linhaAlocada = conteudo.substring(indiceAlocada, conteudo.indexOf("</tr>", indiceAlocada));
		String linhaSemAlocacao = conteudo.substring(indiceSemAlocacao, conteudo.indexOf("</tr>", indiceSemAlocacao));
		org.junit.jupiter.api.Assertions.assertTrue(linhaAlocada.contains("Alocada"));
		org.junit.jupiter.api.Assertions.assertTrue(linhaAlocada.contains("CODIGO-ALOCADO-" + alocada.getId()));
		org.junit.jupiter.api.Assertions.assertTrue(linhaSemAlocacao.contains("Não alocada"));
		org.junit.jupiter.api.Assertions.assertTrue(linhaSemAlocacao.contains("Nenhuma"));
	}
}
