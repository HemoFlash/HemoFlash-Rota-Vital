# Rota Vital — Story Map do PI | E2 — TDD

## Objetivo da Release 1
Permitir registrar requisições de sangue, consultar bolsas elegíveis por compatibilidade ABO/Rh (para hemácias) e priorizar a alocação por validade (FEFO), respeitando a disponibilidade.

## Backbone — jornada do usuário
1. **Registrar solicitação:** cadastrar e consultar requisição com tipo sanguíneo e prioridade.
2. **Consultar estoque:** visualizar bolsas cadastradas e sua disponibilidade.
3. **Verificar elegibilidade:** filtrar bolsas compatíveis com a requisição.
4. **Priorizar e alocar:** ordenar bolsas elegíveis por validade e impedir alocação duplicada.
5. **Acompanhar atendimento:** consultar status e bolsa vinculada à requisição.

## Release 1 — histórias e tarefas

### US-01 — Cadastrar requisição
**Como** profissional responsável pelo atendimento, **quero** cadastrar uma requisição com tipo sanguíneo e prioridade, **para** organizar o atendimento.

**Critérios de aceitação:** dados da requisição são persistidos e podem ser consultados; o tipo sanguíneo e a prioridade aparecem corretamente.

**Tarefas:**
- T01.1 Definir os campos e validações da requisição (regras a confirmar com a equipe).
- T01.2 Implementar persistência e recuperação da requisição.
- T01.3 Expor formulário/rota de cadastro e apresentar dados no menu.

**Testes existentes relacionados:** `formularioSalvaTipoSanguineoDaRequisicao`, `requisicaoPodeSerEditadaERemovida` (TuguyApplicationTests). **Lacuna:** identificar/criar teste unitário para cada regra de validação definida em T01.1.

**Aceitação — Gherkin:**
```gherkin
Funcionalidade: Cadastro de requisições
  Cenário: Cadastrar requisição com tipo sanguíneo
    Dado que o profissional acessa o formulário de requisição
    Quando informa título, descrição, prioridade e tipo sanguíneo válidos
    E confirma o cadastro
    Então a requisição deve ser persistida
    E deve aparecer no menu com seu tipo sanguíneo
```

### US-02 — Consultar bolsas compatíveis
**Como** profissional de um hemocentro, **quero** visualizar bolsas compatíveis com a requisição, **para** evitar selecionar uma bolsa incompatível.

**Critérios de aceitação:** o filtro considera a matriz ABO/Rh para hemácias e não exibe bolsas incompatíveis.

**Tarefas:**
- T02.1 Implementar a regra de compatibilidade ABO/Rh para hemácias.
- T02.2 Rejeitar entradas de tipo sanguíneo ausentes ou inválidas.
- T02.3 Filtrar bolsas exibidas na tela de alocação conforme o tipo da requisição.

**Testes existentes relacionados:** `testeABOAplicaTodaMatrizDeCompatibilidadeParaHemacias`, `testeABORejeitaTiposAusentesOuInvalidos`, `testeABONormalizaEspacosEMinusculas` (CompatibilidadeABOServiceTest); `paginaDeAlocacaoExibeSomenteBolsasCompativeisComTipoDaRequisicao` (TuguyApplicationTests).

**Aceitação — Gherkin:**
```gherkin
Funcionalidade: Consulta de bolsas compatíveis
  Cenário: Exibir somente bolsas compatíveis com receptor O negativo
    Dado que existe uma requisição de hemácias para receptor O negativo
    E existem bolsas O negativo e A negativo disponíveis
    Quando o profissional abre a tela de alocação
    Então a bolsa O negativo deve aparecer como elegível
    E a bolsa A negativo não deve aparecer como elegível
```

### US-03 — Priorizar e alocar bolsa
**Como** profissional responsável pelo estoque, **quero** priorizar bolsas elegíveis por vencimento e impedir dupla alocação, **para** organizar o uso do estoque.

**Critérios de aceitação:** bolsas elegíveis aparecem em ordem crescente de validade; uma bolsa alocada não pode ser atribuída a outra requisição.

**Tarefas:**
- T03.1 Implementar a ordenação FEFO de bolsas elegíveis.
- T03.2 Validar disponibilidade antes de alocar.
- T03.3 Persistir vínculo entre bolsa e requisição e atualizar status.

**Testes existentes relacionados:** `bolsasCompativeisNaAlocacaoSaoOrdenadasPorValidadeCrescente`, `alocarVinculaBolsaEImpedeQueOutraRequisicaoAloqueNovamente`, `menuMostraEstadoECodigoDaBolsaAlocadaPorRequisicao` (TuguyApplicationTests). **Lacuna:** criar teste unitário isolado do serviço FEFO, caso ainda não exista no repositório.

**Aceitação — Gherkin:**
```gherkin
Funcionalidade: Priorização e alocação de bolsas
  Cenário: Priorizar bolsa elegível que vence primeiro
    Dado que existem duas bolsas compatíveis e disponíveis
    E uma vence em 5 dias e outra em 20 dias
    Quando o profissional consulta as bolsas para alocação
    Então a bolsa que vence em 5 dias deve aparecer primeiro

  Cenário: Impedir dupla alocação
    Dado que uma bolsa já foi alocada à primeira requisição
    Quando o profissional tenta alocá-la a uma segunda requisição
    Então o sistema deve rejeitar a nova alocação
    E manter o vínculo com a primeira requisição
```

## Matriz de cobertura — estado inicial
| História | Unitário | Integração com banco | Aceitação |
|---|---|---|---|
| US-01 | Pendente verificar regras e testes isolados | Há testes com `@SpringBootTest`, `MockMvc` e H2; verificar persistência explicitamente | Cenário Gherkin documentado; automação a verificar |
| US-02 | Testes de matriz ABO/Rh disponíveis | Teste de tela com contexto Spring | Cenário Gherkin documentado; automação a verificar |
| US-03 | Teste unitário FEFO a verificar/criar | Testes de alocação e consulta com contexto Spring e H2 | Cenários Gherkin documentados; automação a verificar |

## Evidências e pendências para concluir E2
- [ ] Confirmar testes unitários para cada tarefa que contém regra de negócio.
- [ ] Confirmar ao menos um teste que grava e consulta dados no H2.
- [ ] Relacionar um teste de aceitação executável ou cenário Gherkin a cada história.
- [ ] Registrar commits reais `test:` (vermelho) → `feat:` (verde) → `refactor:` (se houver), com links e logs.
- [ ] Confirmar workflow em push e pull request e branch protection/ruleset exigindo checks.
- [ ] Registrar captura autêntica de merge bloqueado em `docs/evidencias/pipeline-red-gate.png`.
- [ ] Explicar no README prioridades de teste por risco, lacunas e uso/revisão de IA.

> Nota: cenários Gherkin acima são especificações de aceitação. Não se afirma que foram executados automaticamente sem integração com ferramenta BDD ou testes equivalentes verificáveis.
