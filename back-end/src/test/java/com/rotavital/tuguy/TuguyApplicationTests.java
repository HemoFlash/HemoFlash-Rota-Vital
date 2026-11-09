package com.rotavital.tuguy;

import com.rotavital.tuguy.model.requisicao;
import com.rotavital.tuguy.model.bolsahemocomponente;
import com.rotavital.tuguy.model.hospital;
import com.rotavital.tuguy.model.veiculotransporte;
import com.rotavital.tuguy.service.bolsahemocomponenteservice;
import com.rotavital.tuguy.service.hospitalservice;
import com.rotavital.tuguy.service.monitoramentotemperaturaservice;
import com.rotavital.tuguy.service.requisicaoservice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.core.io.ClassPathResource;

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
import javax.sql.DataSource;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:rotavitaltest",
		"rotavital.monitoramento.initial-delay-ms=3600000"
})
@AutoConfigureMockMvc
class TuguyApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private requisicaoservice requisicaoService;

	@Autowired
	private bolsahemocomponenteservice bolsaService;

	@Autowired
	private monitoramentotemperaturaservice monitoramentoTemperaturaService;

	@Autowired
	private hospitalservice hospitalService;

	@Autowired
	private DataSource dataSource;

	@Test
	void contextLoads() {
	}

	@Test
	void painelExibeIndicadoresDeEstoqueERequisicoesComDadosReais() throws Exception {
		for (int i = 0; i < 4; i++) {
			bolsaService.criar(new bolsahemocomponente(
					null, "HU7-BPLUS-" + i, "B+", "Hemácias", 250,
					LocalDate.now(), LocalDate.now().plusDays(30), "DISPONIVEL", null));
		}
		bolsaService.criar(new bolsahemocomponente(
				null, "HU7-BMINUS", "B-", "Hemácias", 250,
				LocalDate.now(), LocalDate.now().plusDays(30), "DISPONIVEL", null));
		requisicaoService.salvar(
				requisicaoDeTeste("Pedido HU07", "Demanda para indicador", "Alta", "B-"));

		mockMvc.perform(get("/menu"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Indicadores de estoque")))
				.andExpect(content().string(containsString("Disponibilidade por grupo ABO")))
				.andExpect(content().string(containsString("Disponibilidade por tipo ABO/Rh")))
				.andExpect(content().string(containsString("Estoque por componente")))
				.andExpect(content().string(containsString("Demanda registrada por tipo")))
				.andExpect(content().string(containsString("RESERVA BAIXA")))
				.andExpect(content().string(containsString("Pedido HU07")));
	}

	@Test
	void painelMonitoraCadaVeiculoEmTransporteEDeixaDeMonitorarAoConcluir() throws Exception {
		mockMvc.perform(post("/monitoramento/temperatura/veiculos")
						.param("codigo", "HU6-VEICULO-01")
						.param("descricao", "Caminhão refrigerado"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/monitoramento/temperatura"));

		veiculotransporte segundo = new veiculotransporte();
		segundo.setCodigo("HU6-VEICULO-02");
		segundo.setDescricao("Van térmica");
		monitoramentoTemperaturaService.registrarVeiculoEmTransporte(segundo);

		mockMvc.perform(get("/monitoramento/temperatura"))
				.andExpect(status().isOk())
				.andExpect(view().name("monitoramento-temperatura"))
				.andExpect(content().string(containsString("Faixa simulada esperada: 1 °C a 7 °C")))
				.andExpect(content().string(containsString("HU6-VEICULO-01")))
				.andExpect(content().string(containsString("HU6-VEICULO-02")))
				.andExpect(content().string(containsString("Em transporte")))
				.andExpect(content().string(not(containsString("Código da bolsa"))));

		monitoramentoTemperaturaService.atualizarLeituras();
		for (veiculotransporte monitorado : monitoramentoTemperaturaService.listarMonitorados()) {
			if (monitorado.getCodigo().startsWith("HU6-VEICULO-")) {
				org.junit.jupiter.api.Assertions.assertNotNull(monitorado.getTemperaturaAtual());
				org.junit.jupiter.api.Assertions.assertNotNull(monitorado.getUltimaLeituraTemperatura());
				org.junit.jupiter.api.Assertions.assertTrue(
						monitorado.getTemperaturaAtual().compareTo(new java.math.BigDecimal("0.0")) >= 0);
				org.junit.jupiter.api.Assertions.assertTrue(
						monitorado.getTemperaturaAtual().compareTo(new java.math.BigDecimal("8.0")) <= 0);
			}
		}

		Long primeiroId = monitoramentoTemperaturaService.listarMonitorados().stream()
				.filter(veiculo -> "HU6-VEICULO-01".equals(veiculo.getCodigo()))
				.map(veiculotransporte::getId)
				.findFirst()
				.orElseThrow();
		monitoramentoTemperaturaService.concluirTransporte(primeiroId);
		org.junit.jupiter.api.Assertions.assertFalse(
				monitoramentoTemperaturaService.listarMonitorados().stream()
						.anyMatch(veiculo -> "HU6-VEICULO-01".equals(veiculo.getCodigo())));

		veiculotransporte reutilizado = new veiculotransporte();
		reutilizado.setCodigo("HU6-VEICULO-01");
		reutilizado.setDescricao("Caminhão refrigerado");
		monitoramentoTemperaturaService.registrarVeiculoEmTransporte(reutilizado);
		org.junit.jupiter.api.Assertions.assertTrue(
				monitoramentoTemperaturaService.listarMonitorados().stream()
						.anyMatch(veiculo -> "HU6-VEICULO-01".equals(veiculo.getCodigo())));
	}

	@Test
	void permiteCadastrarUsuariosDiferentesComASenha() throws Exception {
		new org.springframework.jdbc.core.JdbcTemplate(dataSource).execute(
				"ALTER TABLE users ADD CONSTRAINT UK5IIM42DXO1FE4CVGYQ67IU2BA UNIQUE (senha)");
		new ResourceDatabasePopulator(new ClassPathResource("schema.sql")).execute(dataSource);

		mockMvc.perform(post("/salvar")
						.param("nome", "Usuário Primeiro")
						.param("email", "primeiro-hu8@example.test")
						.param("senha", "86753091234"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));

		mockMvc.perform(post("/salvar")
						.param("nome", "Usuário Segundo")
						.param("email", "segundo-hu8@example.test")
						.param("senha", "86753091234"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	void emailDuplicadoRetornaErroDeValidacaoEmVezDeErroInterno() throws Exception {
		mockMvc.perform(post("/salvar")
						.param("nome", "Usuário Original")
						.param("email", "email-duplicado-hu8@example.test")
						.param("senha", "12345678901"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));

		mockMvc.perform(post("/salvar")
						.param("nome", "Outro Usuário")
						.param("email", "email-duplicado-hu8@example.test")
						.param("senha", "10987654321"))
				.andExpect(status().isOk())
				.andExpect(view().name("cadastrar"))
				.andExpect(content().string(containsString("Este e-mail já está cadastrado.")));
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
				.andExpect(content().string(containsString("Hospital onde a bolsa está em estoque")))
				.andExpect(content().string(not(containsString("Requisição associada"))));
	}

	@Test
	void hospitalsArePersistedAndListedForStockAndDestinationSelection() throws Exception {
		mockMvc.perform(get("/hospitais/novo"))
				.andExpect(status().isOk())
				.andExpect(content().string(not(containsString("name=\"codigo\""))));

		mockMvc.perform(get("/hospitais"))
				.andExpect(status().isOk())
				.andExpect(view().name("hospitais"))
				.andExpect(content().string(containsString("Hospital Recife")));

		mockMvc.perform(post("/hospitais")
						.param("nome", "Hospital de Teste da Rede")
						.param("municipio", "Recife")
						.param("codigo", "CODIGO-IGNORADO"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/hospitais"));

		mockMvc.perform(get("/requisicoes"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Hospital de Teste da Rede")));
		hospital cadastrado = hospitalService.listarTodos().stream()
				.filter(item -> "Hospital de Teste da Rede".equals(item.getNome()))
				.findFirst()
				.orElseThrow();
		org.junit.jupiter.api.Assertions.assertTrue(cadastrado.getCodigo().matches("REC\\d{4}"));
	}

	@Test
	void codigosDeHospitaisRecebemSequenciaPorMunicipio() {
		hospital segundoRecife = hospitalService.cadastrar(
				new hospital(null, "Hospital Recife Unidade B", "Recife"));
		hospital terceiroRecife = hospitalService.cadastrar(
				new hospital(null, "Hospital Recife Unidade C", "Recife"));

		org.junit.jupiter.api.Assertions.assertTrue(segundoRecife.getCodigo().matches("REC\\d{4}"));
		int segundaSequencia = Integer.parseInt(segundoRecife.getCodigo().substring(3));
		int terceiraSequencia = Integer.parseInt(terceiroRecife.getCodigo().substring(3));
		org.junit.jupiter.api.Assertions.assertEquals(
				segundaSequencia + 1, terceiraSequencia);
	}

	@Test
	void sidebarPermaneceNasPaginasOperacionais() throws Exception {
		requisicao requisicao = salvarRequisicaoComDestino("Sidebar rota", "REC0001");
		criarBolsaAlocada(requisicao, "SIDEBAR-ROTA-" + requisicao.getId(),
				"OLI0001", "Hemácias", LocalDate.now().plusDays(30));

		for (String url : new String[]{
				"/menu",
				"/requisicoes",
				"/requisicoes/" + requisicao.getId() + "/alocar-bolsa",
				"/bolsas",
				"/bolsas/nova",
				"/hospitais",
				"/hospitais/novo",
				"/requisicoes/" + requisicao.getId() + "/rota",
				"/monitoramento/temperatura"}) {
			mockMvc.perform(get(url))
					.andExpect(status().isOk())
					.andExpect(content().string(containsString("class=\"rv-sidebar\"")))
					.andExpect(content().string(containsString("Monitoramento de temperatura")));
		}
	}

	@Test
	void formularioSalvaBolsaSemRequisicaoAssociada() throws Exception {
		long ultimoIdAntes = bolsaService.listarTodos().stream()
				.mapToLong(bolsahemocomponente::getId)
				.max()
				.orElse(0);
		mockMvc.perform(post("/bolsas")
						.param("codigo", "ID-FORJADO")
						.param("status", "ALOCADA")
						.param("tipoSanguineo", "O+")
						.param("componente", "Plasma")
						.param("quantidadeMl", "250")
						.param("dataColeta", "2026-10-18")
						.param("dataValidade", "2026-11-18")
						.param("hospitalEstoqueId", hospitalId("REC0001")))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/menu"));

		bolsahemocomponente bolsa = bolsaService.listarTodos().stream()
				.filter(item -> item.getId() > ultimoIdAntes
						&& "O+".equals(item.getTipoSanguineo())
						&& "Plasma".equals(item.getComponente())
						&& Integer.valueOf(250).equals(item.getQuantidadeMl()))
				.findFirst()
				.orElseThrow();
		org.junit.jupiter.api.Assertions.assertNotNull(bolsa.getId());
		org.junit.jupiter.api.Assertions.assertEquals(ultimoIdAntes + 1, bolsa.getId());
		org.junit.jupiter.api.Assertions.assertEquals("BOLSA-" + bolsa.getId(), bolsa.getCodigo());
		org.junit.jupiter.api.Assertions.assertEquals("DISPONIVEL", bolsa.getStatus());

		mockMvc.perform(get("/menu"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Bolsas cadastradas")))
				.andExpect(content().string(containsString(String.valueOf(bolsa.getId()))))
				.andExpect(content().string(containsString("O+")))
				.andExpect(content().string(containsString("250 ml")))
				.andExpect(content().string(containsString("Hospital Recife")))
				.andExpect(content().string(containsString("18/11/2026")));
		org.junit.jupiter.api.Assertions.assertNull(bolsa.getRequisicao());
		org.junit.jupiter.api.Assertions.assertEquals("REC0001", bolsa.getHospitalEstoque().getCodigo());
	}

	@Test
	void formularioDeBolsaExigeHospitalDeEstoque() throws Exception {
		mockMvc.perform(post("/bolsas")
						.param("tipoSanguineo", "O+")
						.param("componente", "Hemácias")
						.param("quantidadeMl", "250")
						.param("dataColeta", LocalDate.now().toString())
						.param("dataValidade", LocalDate.now().plusDays(10).toString()))
				.andExpect(status().isOk())
				.andExpect(view().name("bolsa-form"))
				.andExpect(content().string(containsString("Selecione o hospital onde a bolsa está em estoque.")));
	}

	@Test
	void cadastroEdicaoNaoExibemCodigoNemStatusParaEdicaoManual() throws Exception {
		mockMvc.perform(get("/bolsas/nova"))
				.andExpect(status().isOk())
				.andExpect(content().string(not(containsString("name=\"codigo\""))))
				.andExpect(content().string(not(containsString("name=\"status\""))));

		bolsahemocomponente existente = new bolsahemocomponente(
				null, "BOLSA-SEM-CAMPOS-" + System.nanoTime(), "A+", "Plasma", 250,
				LocalDate.now(), LocalDate.now().plusDays(10), "DISPONIVEL", null);
		bolsaService.criar(existente);
		mockMvc.perform(get("/bolsas/{id}/editar", existente.getId()))
				.andExpect(status().isOk())
				.andExpect(content().string(not(containsString("name=\"codigo\""))))
				.andExpect(content().string(not(containsString("name=\"status\""))));
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
						.param("tipoSanguineo", "B-")
						.param("hospitalDestino", "REC0001"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/menu"));

		mockMvc.perform(get("/menu"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Requisição com tipo")))
				.andExpect(content().string(containsString("B-")));
	}

	@Test
	void formularioRejeitaHospitalDeDestinoNaoCadastrado() throws Exception {
		mockMvc.perform(post("/requisicoes")
						.param("titulo", "Destino inválido")
						.param("descricao", "Teste de validação do hospital de destino")
						.param("prioridade", "Alta")
						.param("tipoSanguineo", "B-")
						.param("hospitalDestino", "HOSPITAL_INEXISTENTE"))
				.andExpect(status().isOk())
				.andExpect(view().name("requisicao"))
				.andExpect(content().string(containsString("Selecione um hospital cadastrado.")));
	}

	@Test
	void requisicaoPodeSerEditadaERemovida() throws Exception {
		requisicao requisicao = requisicaoService.salvar(
				requisicaoDeTeste("Requisição editável", "Descrição original", "Baixa", "O+"));

		mockMvc.perform(get("/requisicoes/{id}/editar", requisicao.getId()))
				.andExpect(status().isOk())
				.andExpect(view().name("requisicao"))
				.andExpect(content().string(containsString("Editar requisição")));

		mockMvc.perform(post("/requisicoes/{id}", requisicao.getId())
						.param("titulo", "Requisição atualizada")
						.param("descricao", "Descrição modificada")
						.param("prioridade", "Alta")
						.param("tipoSanguineo", "AB+")
						.param("hospitalDestino", "OLI0001"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/menu"));

		requisicao atualizada = requisicaoService.buscarPorId(requisicao.getId());
		org.junit.jupiter.api.Assertions.assertEquals("Requisição atualizada", atualizada.getTitulo());
		org.junit.jupiter.api.Assertions.assertEquals("AB+", atualizada.getTipoSanguineo());
		org.junit.jupiter.api.Assertions.assertEquals("OLI0001", atualizada.getHospitalDestino());

		mockMvc.perform(post("/requisicoes/{id}/remover", requisicao.getId()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/menu"));
		org.junit.jupiter.api.Assertions.assertThrows(
				RuntimeException.class, () -> requisicaoService.buscarPorId(requisicao.getId()));
	}

	@Test
	void requisicaoComBolsaAssociadaNaoPodeSerRemovida() throws Exception {
		requisicao requisicao = requisicaoService.salvar(
				requisicaoDeTeste("Requisição vinculada", "Possui uma bolsa associada", "Alta", "O-"));
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
				requisicaoDeTeste("Requisição da bolsa editável", "Requisição para teste CRUD", "Alta", "A+"));
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
						.param("hospitalEstoqueId", hospitalId("REC0001")))
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
				requisicaoDeTeste("Requisição tipo O negativo", "Teste de compatibilidade ABO e Rh", "Alta", "O-"));
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
				requisicaoDeTeste("Requisição ordenação FEFO", "Teste de validade FEFO", "Alta", "O-"));
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
	void planejamentoApresentaRotaEntreHospitaisComLimitesDoComponente() throws Exception {
		requisicao requisicao = salvarRequisicaoComDestino("Rota Recife", "REC0001");
		criarBolsaAlocada(requisicao, "ROTA-RECIFE-" + requisicao.getId(),
				"OLI0001", "Hemácias", LocalDate.now().plusDays(30));

		mockMvc.perform(get("/requisicoes/{id}/rota", requisicao.getId()))
				.andExpect(status().isOk())
				.andExpect(view().name("rota"))
				.andExpect(content().string(containsString("ROTA_ENCONTRADA")))
				.andExpect(content().string(containsString("Hospital Recife")))
				.andExpect(content().string(containsString("Hospital Olinda")))
				.andExpect(content().string(containsString("18.0 km")))
				.andExpect(content().string(containsString("refrigerado")));
	}

	@Test
	void planejamentoInformaQuandoDuracaoExcedeToleranciaDoComponente() throws Exception {
		requisicao requisicao = salvarRequisicaoComDestino("Transporte fora do limite", "REC0001");
		criarBolsaAlocada(requisicao, "ROTA-PRAZO-" + requisicao.getId(),
				"PET0001", "Hemácias", LocalDate.now().plusDays(30));

		mockMvc.perform(get("/requisicoes/{id}/rota", requisicao.getId())
						.param("modalidade", "NAO_REFRIGERADO"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("TEMPO_TRANSPORTE_EXCEDIDO")))
				.andExpect(content().string(containsString("supera o tempo sintético permitido")))
				.andExpect(content().string(containsString("Hospital Caruaru")))
				.andExpect(content().string(containsString("não refrigerado")));
	}

	@Test
	void planejamentoInformaQuandoNaoExisteTrechoEntreHospitaisCadastrados() throws Exception {
		hospital destinoSemTrecho = hospitalService.cadastrar(
				new hospital(null, "Hospital sem trecho", "Recife"));
		requisicao requisicao = salvarRequisicaoComDestino(
				"Rota não cadastrada", destinoSemTrecho.getCodigo());
		criarBolsaAlocada(requisicao, "ROTA-SEM-TRECHO-" + requisicao.getId(),
				"OLI0001", "Hemácias", LocalDate.now().plusDays(30));

		mockMvc.perform(get("/requisicoes/{id}/rota", requisicao.getId()))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("SEM_ROTA")))
				.andExpect(content().string(containsString("Não foi encontrada rota cadastrada entre os hospitais")));
	}

	@Test
	void planejamentoExigeBolsaAlocada() throws Exception {
		requisicao requisicao = salvarRequisicaoComDestino("Rota sem bolsa", "REC0001");

		mockMvc.perform(get("/requisicoes/{id}/rota", requisicao.getId()))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("SEM_BOLSA")))
				.andExpect(content().string(containsString("Nenhuma bolsa está alocada")));
	}

	@Test
	void planejamentoUsaValidadeDoHemocomponente() throws Exception {
		requisicao requisicao = salvarRequisicaoComDestino("Bolsa vencida", "REC0001");
		criarBolsaAlocada(requisicao, "ROTA-VENCIDA-" + requisicao.getId(),
				"OLI0001", "Hemácias", LocalDate.now().minusDays(1));

		mockMvc.perform(get("/requisicoes/{id}/rota", requisicao.getId()))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("HEMOCOMPONENTE_VENCIDO")))
				.andExpect(content().string(containsString("já está vencido")));
	}

	private requisicao salvarRequisicaoComDestino(String titulo, String destino) {
		requisicao requisicao = requisicaoDeTeste(
				titulo, "Requisição para teste de planejamento de rota", "Alta", "O+");
		requisicao.setHospitalDestino(destino);
		return requisicaoService.salvar(requisicao);
	}

	private requisicao requisicaoDeTeste(String titulo, String descricao, String prioridade, String tipoSanguineo) {
		requisicao requisicao = new requisicao(null, titulo, descricao, prioridade, tipoSanguineo);
		requisicao.setHospitalDestino("REC0001");
		return requisicao;
	}

	private void criarBolsaAlocada(requisicao requisicao, String codigo, String origem,
								   String componente, LocalDate validade) {
		bolsahemocomponente bolsa = new bolsahemocomponente(
				null, codigo, "O+", componente, 250, LocalDate.now(),
				validade, "ALOCADA", requisicao);
		bolsa.setHospitalEstoque(hospitalService.buscarPorCodigo(origem));
		bolsaService.criar(bolsa);
	}

	private String hospitalId(String codigo) {
		return hospitalService.buscarPorCodigo(codigo).getId().toString();
	}

	@Test
	void alocarVinculaBolsaEImpedeQueOutraRequisicaoAloqueNovamente() throws Exception {
		requisicao primeira = requisicaoService.salvar(
				requisicaoDeTeste("Requisição alocadora", "Receberá a bolsa compatível", "Alta", "A+"));
		requisicao segunda = requisicaoService.salvar(
				requisicaoDeTeste("Outra requisição", "Não deve receber a bolsa já alocada", "Media", "A+"));
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
				requisicaoDeTeste("Prioridade baixa no menu", "Solicitação de baixa prioridade", "Baixa", "A+"));
		requisicao media = requisicaoService.salvar(
				requisicaoDeTeste("Prioridade média no menu", "Solicitação de média prioridade", "Média", "A+"));
		requisicao alta = requisicaoService.salvar(
				requisicaoDeTeste("Prioridade alta no menu", "Solicitação de alta prioridade", "Alta", "A+"));

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
				requisicaoDeTeste("Requisição troca de bolsa", "Substituir a bolsa alocada", "Alta", "A+"));
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
				requisicaoDeTeste("Requisição já alocada", "Tem uma bolsa vinculada", "Alta", "O+"));
		requisicao semAlocacao = requisicaoService.salvar(
				requisicaoDeTeste("Requisição sem alocação", "Ainda sem bolsa", "Baixa", "A+"));
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
		org.junit.jupiter.api.Assertions.assertTrue(linhaAlocada.contains("Planejar rota"));
		org.junit.jupiter.api.Assertions.assertTrue(linhaSemAlocacao.contains("Não alocada"));
		org.junit.jupiter.api.Assertions.assertTrue(linhaSemAlocacao.contains("Nenhuma"));
		org.junit.jupiter.api.Assertions.assertFalse(linhaSemAlocacao.contains("Planejar rota"));
	}
}
