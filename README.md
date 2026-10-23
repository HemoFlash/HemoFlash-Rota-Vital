# Rota Vital — Gestão e Distribuição de Hemocomponentes na Rede de Sangue

> Projeto Integrador — 3º Semestre ADS · Squad **Tuguy**
> **Última atualização:** `19/10/2026 — Entrega 03 (POO)`

---

## Sobre o projeto

**Rota Vital** é uma aplicação web que apoia a rede de sangue (inspirada no fluxo da Hemorrede/SUS: coleta/doação → hemocentro → estoque → hospitais) na gestão e distribuição de hemocomponentes.

O sistema gerencia o estoque por tipo e componente, recebe requisições dos hospitais, aloca bolsas compatíveis priorizando a validade (FEFO), calcula rotas de distribuição respeitando a cadeia fria e as janelas de tempo, e monitora temperatura e rede em painéis.

> ⚠️ O projeto utiliza **exclusivamente dados sintéticos** (sem dados reais de doadores/pacientes — LGPD). A telemetria de temperatura/GPS é simulada e a compatibilidade ABO/Rh possui finalidade **didática**, não substituindo protocolos clínicos. Não há integração com sistemas oficiais da Hemorrede.

### Ficha do Projeto

| Campo | Descrição |
|---|---|
| **Problema central** | Garantir o componente certo (compatível e válido), no lugar certo, no tempo certo e na temperatura certa — evitando desabastecimento, descarte por vencimento e risco ao paciente. |
| **Produto esperado** | App web que gerencia estoque, recebe requisições hospitalares, aloca bolsas por compatibilidade + FEFO, calcula rotas respeitando cadeia fria/janelas de tempo, e exibe painéis de estoque/demanda e monitoramento. |
| **Complexidade** | Média (escopo controlado: poucos componentes, grafo limitado, dados sintéticos). |
| **Limites de escopo** | Só dados sintéticos; telemetria simulada; compatibilidade ABO/Rh didática; grafo de rotas limitado; sem integração com sistemas oficiais. |

### Tecnologias Usadas

| Camada | Tecnologia |
|---|---|
| Backend | Java 21, Spring Boot |
| Frontend | HTML, CSS, Thymeleaf |
| Prototipação | Figma |
| Banco de dados | H2 (arquivo local, via Spring Data JPA) |
| Versionamento | Git / GitHub |
| CI/CD | GitHub Actions |

### Como rodar o projeto

**Pré-requisitos:** Java 21 (JDK). O Maven não precisa estar instalado — o projeto inclui o wrapper `mvnw`.

1. Clone o repositório e entre na pasta do back-end:
```bash
   git clone https://github.com/Vitoria-Gabrielly-DEV/Tuguy-Rota-Vital.git
   cd Tuguy-Rota-Vital/back-end/tuguy
```
2. Execute a aplicação:
```bash
   ./mvnw spring-boot:run
```
   (no Windows: `mvnw.cmd spring-boot:run`)

3. Acesse **http://localhost:8080/login**

4. Console do banco H2 (dados em `./data/users-db`): **http://localhost:8080/h2-console**
   - JDBC URL: `jdbc:h2:file:./data/users-db`
   - Usuário: `sa` · Senha: (em branco)

---

## Equipe

| Integrante | Área/Papel no PI | E-mail (@cesar.school) |
|---|---|---|
| Dilvanir Aline Alves de Melo | Desenvolvedora | `dacm@cesar.school` |
| Emanoel Alesandro da Silva | Desenvolvedor | `eas3@cesar.school` |
| Marcio Aureliano da Silva | Desenvolvedor | `maps@cesar.school` |
| Maria Larysse Yasmin Lira | Desenvolvedora | `mlylp@cesar.school` |
| Pedro Pessoa de Albuquerque | Desenvolvedor | `ppan@cesar.school` |
| Vitória Gabrielly Silva | Líder, Desenvolvedora | `vggs@cesar.school` |

**Membros anteriores / que saíram do grupo:** Nenhuma alteração até o momento.
---
| Ana Beatriz da Costa | Desenvolvedora | `abblc@cesar.school` |
- Saiu do grupo pois está pagando a cadeira com outro professor, portanto está com outro modo de avaliação.

## Repositório e links úteis

- **Repositório:** https://github.com/Vitoria-Gabrielly-DEV/Tuguy-Rota-Vital
- **Ambiente/deploy:** *a definir*
- **Quadro de acompanhamento:** https://algs2.atlassian.net/jira/software/c/projects/PI3E3/boards/10 (colunas: Squad | Disciplina | Entrega | Unidade | Semana prevista | Status | Dependências | Riscos | Observações)
- **Planejamento oficial:** `Planejamento_Projeto_Integrador_3.pdf`

---

## Roadmap Geral (Projeto 3)

| Semana | Foco | Disciplinas | Status |
|---|---|---|---|
| 1 | Kickoff do PI e organização das squads | Todas | ✅ |
| 2–3 | Modelagem do domínio e escopo de dados/algoritmos | POO, AED, RSD | ✅ |
| 4–5 | Setup de infraestrutura e início dos algoritmos | SO, RSD, AED | ✅ |
| 6–7 | Primeira sprint — convergência das entregas U1 | Todas | ✅ |
| **8–9** | **Fechamento e checkpoint da Entrega U1** | Todas | ✅ |
| 10–11 | Replanejamento U2 + compatibilidade/integração | POO, AED | ☐ |
| 12–13 | Telemetria, concorrência e painéis | SO, RSD, EST | ☐ |
| 14–15 | Consolidação integrada + preparação da apresentação | Todas | ☐ |
| **16–17** | **Apresentação final e avaliação de processo/colaboração** | Todas | ☐ |

---

## Entregas por Disciplina

> Cada disciplina tem sua própria seção abaixo, com uma subseção **por entrega**, contendo o checklist de itens pedidos e os **links diretos** dos artefatos correspondentes (documentos, protótipos, vídeos, prints). Isso garante que cada professor/monitor encontre rapidamente o material da sua matéria.

### 🩸 POO (Programação Orientada a Objetos)

#### Entrega 01 — 31/08 ✅
- [x] Histórias de usuário (mínimo 7, escritas em `.md`, com detalhes de negócio) → [`docs/historias-usuario.md`](./docs/historias-usuario.md)
- [x] Cenários de aceitação em BDD (Dado-Quando-Então) para cada história → incluídos no mesmo documento acima
- [x] Protótipo Lo-Fi no Figma (mínimo 5 histórias) → [Acessar protótipo](https://www.figma.com/design/U21MRJxsocn9mvfMPw5yEj/Rota-Vital?node-id=0-1&p=f&t=Qi3l1zU3RWARUcw2-0)
- [x] Screencast apresentando o protótipo, explicando cada história (áudio/legenda) → [Assistir no YouTube](https://youtu.be/VXIJSzfzIQQ)

**Histórias mapeadas no protótipo:** HU08 (Login/Cadastro), HU01 (Estoque), Lista de Bolsas, Detalhe da Bolsa, Cadastro de Bolsa, Lista de Doações, Cadastro de Doação, Dashboard.

#### Entrega 02 — 21/09 ✅
- [x] Ao menos 2 histórias implementadas (com descrição formato POST-IT em "Histórias implementadas" abaixo)
- [x] Modelo de domínio documentado (diagrama de classes) → [`docs/diagrama-classes.png`](./docs/diagrama-classes.png) · [fonte editável (.mmd)](./docs/diagrama-classes.mmd)
- [x] Commits frequentes (mínimo semanais) direto na `main`
- [x] Issue/bug tracker do GitHub atualizado (print anexado)
- [x] Screencast do sistema funcionando (uso) —> [Acessar screencast](https://youtu.be/x03UCdjJq5I)
- [x] Screencast da explicação do código —> [Acessar screencast](https://youtu.be/x03UCdjJq5I)

**Modelo de domínio (entidades, atributos, relacionamentos e cardinalidade):**

![Diagrama de classes — Rota Vital](./docs/diagrama-classes.png)

- Fonte editável: [`docs/diagrama-classes.mmd`](./docs/diagrama-classes.mmd) — abra em [mermaid.live](https://mermaid.live) para ajustar.

- Regras de negócio no formato regra → serviço/método → exceção → HU de origem: [`Regra de negócio — Rota Vital`](docs/regras-de-negocio.md)

**Histórias implementadas nesta entrega:**
*(preencher no formato: "Como [usuário], eu gostaria de [ação], para [valor]")*
> **HU08 — Cadastrar e acessar o aplicativo**
> Como usuário, eu gostaria de realizar meu cadastro utilizando meu e-mail de trabalho e acessar minha conta por meio de login, para que eu possa utilizar o aplicativo.

> **HU02 — Registrar requisição hospitalar**
> Como funcionário do hemocentro, eu gostaria de registrar requisições de hemocomponentes realizadas pelos hospitais, para que eu possa organizar o atendimento das solicitações.

**Issue/bug tracker:**

![Issues do projeto no GitHub](./docs/prints/issues-entrega-02.png)
[Acessar Issues no GitHub](https://github.com/Vitoria-Gabrielly-DEV/Tuguy-Rota-Vital/issues)

#### Entrega 03 — 19/10 ✅
- [x] Mais 2 histórias implementadas (descrição formato POST-IT abaixo)
- [x] Commits frequentes (mínimo semanais)
- [x] Screencast do sistema funcionando com as novas histórias  — [Acessar screencast PARTE 1](https://youtu.be/IahHX3jv77I)
- [x] Screencast da explicação do código das novas histórias - [Acessar screencast PARTE 2](https://youtu.be/Tu8iwwz41mw)
- [x] Issue/bug tracker atualizado (print anexado)

**Histórias implementadas nesta entrega:**
*(preencher no formato: "Como [usuário], eu gostaria de [ação], para [valor]")*
> **HU03 — Alocar hemocomponentes priorizando a validade**
> Como funcionário do hemocentro, eu gostaria de alocar bolsas compatíveis com a requisição hospitalar priorizando aquelas com menor prazo de validade, para que eu possa reduzir o descarte de hemocomponentes por vencimento.

> **HU04 — Consultar compatibilidade ABO/Rh**
> Como funcionário do hemocentro, eu gostaria de consultar a compatibilidade ABO/Rh entre a requisição e as bolsas disponíveis, para que eu possa selecionar hemocomponentes compatíveis de forma adequada.

**Issue/bug tracker:**

![Issues do projeto no GitHub](./docs/prints/issues-entrega-03.png)
[Acessar Issues no GitHub](https://github.com/Vitoria-Gabrielly-DEV/Tuguy-Rota-Vital/issues)

#### Entrega 04 — 09/11 ☐
- [ ] Histórias restantes implementadas (mínimo 2, descrição formato POST-IT abaixo)
- [ ] Commits frequentes (mínimo semanais)
- [ ] Screencast do uso do sistema (ênfase nas novas histórias) — link:
- [ ] Screencast da explicação do código — link:
- [ ] Issue/bug tracker atualizado (print anexado)

**Histórias implementadas nesta entrega:**

#### Apresentação Final — 09/11 a 13/11 ☐
- [ ] Vídeo/apresentação de até 8 min cobrindo: problema, solução, fluxo de trabalho, ferramentas, lições aprendidas, demonstração do produto — link:

---

### 🧮 AED (Algoritmos e Estruturas de Dados)

#### Unidade 1 (fecha semana 8–9) ✅
- [x] Implementação das estruturas de estoque (lista), requisições (fila) e, se aplicável, histórico de operações (pilha), com alocação manual de memória (malloc/free) e manipulação explícita de ponteiros.
- [x] Reimplementação das mesmas estruturas, prontas para serem consumidas pela aplicação de POO (Spring Boot).
- [x] Para cada estrutura, um parágrafo explicando por que a versão Java corresponde à lógica da versão em C/C++ — não uma descrição genérica, mas uma justificativa amarrada ao código real que o aluno escreveu.
- [x] Modelagem de domínio (entidades e estruturas escolhidas corretamente, com justificativa)
- [x] Operações básicas funcionando (inserir, remover, consultar) e testadas
- [x] Código pronto para integração (documentado, sem dependências fixas que travem o consumo pela aplicação)
- [x] Qualidade e coerência da tradução comentada entre C/C++ e Java.

- Links dos artefatos:

#### Unidade 2 (fecha semana 15–16) ☐
- [ ] Implementação da lógica de roteirização (busca de vizinho mais próximo), priorização FEFO (inserção ordenada), índice de estoque por hash e compatibilidade ABO/Rh (matriz de compatibilidade).
- [ ] Reimplementação de todos esses algoritmos, integrados entre si e com a aplicação de POO — dado um pedido, o sistema deve alocar a bolsa compatível, priorizada por validade, e calcular a rota.
- [ ] Mesma lógica da U1, cobrindo agora os quatro algoritmos.
- [ ] Para cada algoritmo, a complexidade declarada e justificada com base na implementação real (não copiada de material genérico).
- [ ] Compatibilidade ABO/Rh implementada corretamente via matriz, com casos de teste cobrindo compatibilidade e incompatibilidade
- [ ] Integração completa: rota + validade + compatibilidade + consulta por hash funcionando em conjunto, em Java
- [ ] Análise de complexidade correta e justificada
- [ ] Qualidade e coerência da tradução comentada entre C/C++ e Java

- Links dos artefatos:

---

### 📊 EST (Estatística)

#### Unidade 1 ☐
- [ ] Indicadores + análise descritiva + painel inicial
- Links dos artefatos:

#### Unidade 2 ☐
- [ ] Análise probabilística (desabastecimento, descarte) + painel consolidado
- Links dos artefatos:

---

### 🖥️ SO (Sistemas Operacionais)

#### Unidade 1 ✅
- [ ] Pipeline CI/CD + primeiro deploy
- [x] Threads em processo real — ordenação de requisições por prioridade (sequencial × threads × virtual threads)
- Links dos artefatos:
  - Serviço: [`ordenacaorequisicaoservice.java`](./back-end/tuguy/src/main/java/com/rotavital/tuguy/ordenacao/ordenacaorequisicaoservice.java)
  - Endpoint REST: [`ordenacaocontroller.java`](./back-end/tuguy/src/main/java/com/rotavital/tuguy/controller/ordenacaocontroller.java)
  - Como rodar o benchmark: [`docs/como-rodar-benchmark-ordenacao.md`](./docs/como-rodar-benchmark-ordenacao.md)
  - Análise (Big-O, threads, Amdahl): [`docs/analise-ordenacao-threads.md`](./docs/analise-ordenacao-threads.md)

#### Unidade 2 ☐
- [ ] Arquitetura em 3 cenários + orçamento + sincronização
- Links dos artefatos:

---

### 🌐 RSD (Redes e Sistemas Distribuídos)

#### Unidade 1 ✅
- [x] Diagrama de topologia + requisitos de rede
- [x] Protocolos/APIs definidos
- Links dos artefatos:

#### Unidade 2 ☐
- [ ] Benchmarking (latência/vazão/erros) + painel de rede + plano de migração
- Links dos artefatos:

---

## Status por Disciplina (atualizar semanalmente)

| Disciplina | Entrega em foco | Status | Bloqueios | Próximo passo |
|---|---|---|---|---|
| POO | Entrega 01 | 🟢 | — | Iniciar implementação (Entrega 02) |
| AED | Unidade 1 | 🔴/🟡/🟢 | | |
| EST | Unidade 1 | 🔴/🟡/🟢 | | |
| SO | Unidade 1 | 🔴/🟡/🟢 | | |
| RSD | Unidade 1 | 🔴/🟡/🟢 | | |

🟢 no prazo · 🟡 atenção · 🔴 bloqueado/atrasado

---

## Riscos Ativos

| Risco | Impacto | Mitigação em andamento |
|---|---|---|
| App virar CRUD sem integração dos algoritmos | Alto | |
| Grafo de rotas grande demais | Médio | |
| Compatibilidade ABO/Rh simplista | Médio | |
| Deploy/CI-CD deixado para o fim | Alto | |
| Telemetria/painéis desconectados do app | Médio | |

---

## Checkpoints de Comunicação Docente

- Reunião quinzenal Projeto ↔ disciplinas técnicas: semanas 3, 5, 7, 9, 11, 13, 15
- Checkpoint de integração U1: semana 8–9 · U2: semana 15–16

---

## Estrutura de pastas sugerida no repositório
```
/docs
  historias-usuario.md      # Histórias de usuário + critérios BDD (POO)
  diagrama-classes.png      # Diagrama de classes do modelo de domínio (POO — U1)
  diagrama-classes.mmd      # Código-fonte Mermaid do diagrama (editável)
  /prints                   # Prints do issue/bug tracker, telas etc.
/src                         # Código-fonte da aplicação Spring Boot
README.md                    # Este arquivo
```


## Estratégia de Testes — E2 TDD

### Planejamento e Release 1

O planejamento da Release 1, incluindo backbone, histórias de usuário, tarefas, critérios de aceitação e cenários BDD, está documentado no [Story Map](boarddocs/story-map.md).

A Release 1 prioriza três histórias:

1. Cadastrar requisições de sangue com tipo sanguíneo e prioridade.
2. Consultar bolsas compatíveis com o tipo sanguíneo solicitado.
3. Alocar bolsas respeitando a regra FEFO (*First Expired, First Out*) e evitando dupla alocação.

### Estratégia de testes por nível

**Testes unitários:** verificam regras de negócio isoladas, incluindo a matriz de compatibilidade ABO/Rh para hemácias, rejeição de tipos sanguíneos inválidos e ordenação de bolsas pela regra FEFO.

**Testes de integração:** utilizam Spring Boot, MockMvc e banco H2 em memória para verificar operações com requisições, bolsas, consultas e alocações.

**Testes de aceitação:** os cenários em formato Gherkin estão descritos no Story Map e representam os comportamentos esperados das três histórias da Release 1. Esses cenários documentados não equivalem, por si só, a testes BDD automatizados.

### Priorização baseada em riscos

| Prioridade | Risco | Estratégia |
|---|---|---|
| Crítica | Apresentar uma bolsa incompatível com o receptor | Testes da matriz ABO/Rh e de filtragem de bolsas |
| Alta | Alocar uma bolsa já utilizada em outra requisição | Teste de integração da prevenção de dupla alocação |
| Alta | Priorizar uma bolsa com validade posterior, contrariando FEFO | Testes unitários de ordenação e seleção FEFO |
| Média | Registrar dados incorretos ou duplicados | Testes de cadastro, validação e persistência |

Essa priorização considera o impacto potencial de uma falha sobre a segurança das decisões de alocação e a integridade dos registros.

### Integração contínua e qualidade

O repositório utiliza GitHub Actions para executar a compilação e os testes Maven em eventos de *push* e *pull request*, conforme o arquivo `.github/workflows/ci.yml`.

O Pull Request #18 contém as alterações de documentação e testes desta etapa. A execução bem-sucedida da CI demonstra que o pipeline concluiu suas verificações, mas não substitui a evidência de um ciclo TDD Red → Green.

A configuração da proteção da branch `main`, exigindo verificações bem-sucedidas antes do merge, depende das permissões de administração do repositório e deve ser comprovada separadamente.

### Limitações e lacunas de cobertura

- A cobertura percentual de código ainda não foi mensurada por uma ferramenta específica, como JaCoCo.
- Os cenários Gherkin estão documentados, mas não foram apresentados como testes de aceitação automatizados.
- A execução bem-sucedida da CI não comprova que todas as regras foram originalmente implementadas seguindo TDD.
- Ainda é necessária evidência cronológica de pelo menos um ciclo Red → Green, com teste falhando antes da implementação e passando após a correção.
- O sistema é um projeto acadêmico e seus testes não constituem validação clínica ou autorização para uso em decisões transfusionais reais.

### Uso de inteligência artificial

Foi utilizada inteligência artificial generativa como ferramenta de apoio à organização da documentação, elaboração de cenários de teste e preparação de código de testes unitários.

O material gerado foi submetido à revisão humana e integrado ao repositório pelos integrantes da equipe. A execução dos testes pelo GitHub Actions foi utilizada como verificação técnica complementar. A responsabilidade pela avaliação, manutenção e entrega do projeto permanece com a equipe.


## Evidências do ciclo TDD — RED → GREEN

Foi aplicado o desenvolvimento orientado a testes (TDD) à regra de disponibilidade de bolsas de hemocomponentes, com commits separados para o teste e para a implementação.

### RED — Teste falhando

Criamos o teste `bolsaVinculadaARequisicaoNaoDeveEstarDisponivel()`, que verifica se uma bolsa já vinculada a uma requisição é considerada indisponível para novas alocações.

O teste falhou inicialmente porque o método `estaDisponivel()` não verificava a existência de uma requisição associada.

**Resultado:** 22 testes executados, 1 falha.

**Evidência:** [Execução RED no GitHub Actions](https://github.com/Vitoria-Gabrielly-DEV/Tuguy-Rota-Vital/actions/runs/39873676256)

### GREEN — Implementação e validação

Atualizamos o método `estaDisponivel()` para exigir também que `requisicao == null`.

Em seguida, ajustamos os dados de preparação de um teste de integração existente, que cadastrava bolsas vinculadas como se estivessem disponíveis para alocação.

Após essas alterações, a execução do pipeline foi concluída com sucesso.

**Resultado:** pipeline Maven `clean verify` aprovado.

**Evidência:** [Execução GREEN no GitHub Actions](https://github.com/Vitoria-Gabrielly-DEV/Tuguy-Rota-Vital/actions/runs/39875683316)

### Regra de negócio validada

Uma bolsa somente pode ser considerada disponível para alocação quando:

- Possui status `DISPONIVEL`;
- Possui data de validade válida e não está vencida;
- Não está vinculada a nenhuma requisição.

Esse ciclo demonstra a identificação de uma falha por meio de teste automatizado, sua correção no código de produção e a validação pela integração contínua.
