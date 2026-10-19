# Rota Vital — Regras de Negócio (Service Layer)

> Complementa `modelo-dominio.md`. Aqui as regras já vêm mapeadas por serviço/método/exceção, prontas para virar código na Unidade 1 de POO.

## RequisicaoService

| ID | Regra | Onde aplicar | Erro sugerido | Origem |
|---|---|---|---|---|
| RN01 | `hospitalId`, `tipoABORh`, `componente` e `quantidade` são obrigatórios ao registrar requisição | `registrar()` | `CampoObrigatorioAusenteException` indicando qual campo falta | HU02 C2 |
| RN02 | Requisição registrada recebe `id` automático e `status = PENDENTE` | `registrar()` | — | HU02 C1 |
| RN03 | `tipoABORh` deve ser um valor válido do enum de tipos sanguíneos | `registrar()` / validação de entrada | `TipoSanguineoInvalidoException` | HU04 |

## BolsaService / AlocacaoService

| ID | Regra | Onde aplicar | Erro sugerido | Origem |
|---|---|---|---|---|
| RN04 | Bolsa só pode ser alocada se `status == DISPONIVEL` e `dataValidade >= hoje` | `alocar()` | `BolsaVencidaException` / `BolsaIndisponivelException` | HU03 C1 |
| RN05 | Entre bolsas compatíveis e disponíveis, alocar primeiro a de menor `dataValidade` (FEFO); ao alocar, associar à requisição e alterar o status para `ALOCADA`, removendo-a da disponibilidade para outras requisições | `alocar()` — seleção FEFO e atualização do vínculo/status | — | HU03 C2 |
| RN06 | Se não houver bolsa compatível disponível, não alocar e retornar mensagem explicativa | `alocar()` | `NenhumaBolsaCompativelException` | HU03 C3 |
| RN07 | Para concentrado de hemácias, compatibilidade ABO/Rh entre tipo da requisição (receptor) e tipo da bolsa (doador) checada via tabela fixa antes da alocação; a regra é didática e não clínica | `testeABO()` | `IncompatibilidadeSanguineaException` | HU04 |

## EstoqueService

| ID | Regra | Onde aplicar | Erro sugerido | Origem |
|---|---|---|---|---|
| RN08 | Consulta por tipo/componente sem unidades deve retornar indicação explícita de indisponibilidade, não erro genérico | `consultarDisponibilidade()` | resposta `disponivel: false` (não exceção) | HU01 C3 |
| RN09 | `quantidadeTotal` deve ser recalculado sempre que uma bolsa mudar de status (nova doação, alocação, descarte) | `atualizarQuantidade()`, chamado em callback de mudança de status da Bolsa | — | HU01, HU07 |

## UsuarioService

| ID | Regra | Onde aplicar | Erro sugerido | Origem |
|---|---|---|---|---|
| RN10 | Cadastro exige e-mail corporativo autorizado e todos os campos obrigatórios preenchidos | `cadastrar()` | `CampoObrigatorioAusenteException` / `EmailNaoAutorizadoException` | HU08 C1, C3 |
| RN11 | Login autentica somente com credenciais corretas; mensagem de erro não deve indicar se o problema é usuário ou senha | `autenticar()` | `CredenciaisInvalidasException` (mensagem genérica) | boa prática — não exigido pela HU, mas recomendável |

## DoacaoService ⚠️ pendente de confirmação

| ID | Regra | Onde aplicar | Erro sugerido | Origem |
|---|---|---|---|---|
| RN12 | Toda Bolsa criada deve referenciar uma Doação de origem; `dataColeta` da Bolsa herda da Doação | `gerarBolsa()` | `DoacaoNaoEncontradaException` | inferida — sem HU explícita, confirmar com o squad antes de implementar |

## Observações de implementação

- Regras de validação de campo (RN01, RN03, RN10) devem ser aplicadas na camada de serviço, **não só no banco** — é item explícito do checklist da rubrica ("regras de validade aplicadas nas operações, não apenas no banco").
- RN04–RN07 concentram a lógica de alocação/compatibilidade: bom candidato a isolar num `AlocacaoService` separado do `BolsaService` de CRUD simples, e a aplicar um padrão de projeto (Strategy para critério de priorização, por exemplo) — atende ao critério "ao menos um padrão de projeto aplicado e justificado".
- Cada exceção sugerida deve ter uma mensagem que aponte exatamente qual regra foi violada, para bater com os cenários de aceitação ("deve informar qual dado precisa ser preenchido").
