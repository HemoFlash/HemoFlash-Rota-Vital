# Entrega 01 — Ajustes Java na compatibilidade ABO e FEFO

## Objetivo

A primeira mudança aplicada foi ajustar os pontos da lógica Java que já existiam mas não estavam implementados conforme o método exigido pela entrega:

- compatibilidade ABO/Rh por matriz de compatibilidade
- ordenação FEFO por inserção ordenada

## Alterações realizadas

### 1) Compatibilidade ABO/Rh via matriz

Arquivo: `back-end/src/main/java/com/rotavital/tuguy/service/CompatibilidadeABOService.java`

- a regra de compatibilidade deixou de ser montada com `switch` + `Set.of(...)`
- foi implementada uma matriz bidimensional fixa em que:
  - linhas = tipo sanguíneo do receptor
  - colunas = tipo sanguíneo do doador
  - o valor booleano indica se a transfusão é compatível
- a validação continua normalizando entrada (`trim`, `toUpperCase`) e rejeitando valores nulos, vazios ou inválidos

Exemplo da matriz: as linhas e colunas seguem a ordem `O-, O+, A-, A+, B-, B+, AB-, AB+`.

### 2) Ordenação FEFO com inserção ordenada

Arquivo: `back-end/src/main/java/com/rotavital/tuguy/service/bolsahemocomponentefefoservice.java`

- a ordenação anterior era feita por `stream().sorted(...)`
- a lógica foi reescrita para usar inserção ordenada manual em uma lista nova
- a regra de prioridade continua sendo:
  1. menor data de validade primeiro
  2. código da bolsa como desempate
  3. ignorar bolsas indisponíveis
- o comportamento do serviço permanece compatível com o restante da aplicação, incluindo `selecionarProximaFefo()` e `listarPorComponente()`

## Observações de revisão

Para validar o comportamento, basta verificar os testes focados já existentes:

- `CompatibilidadeABOServiceTest`
- `bolsahemocomponentefefoserviceTest`

Esses testes cobrem:

- matriz completa de compatibilidade ABO/Rh para hemácias
- rejeição de tipos inválidos
- normalização de espaços e acentos/caixa
- priorização das bolsas mais próximas do vencimento
- desempate por código
- ignorar bolsas não disponíveis

## Arquivos alterados

- `back-end/src/main/java/com/rotavital/tuguy/service/CompatibilidadeABOService.java`
- `back-end/src/main/java/com/rotavital/tuguy/service/bolsahemocomponentefefoservice.java`
