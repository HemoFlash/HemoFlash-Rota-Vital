# Entrega 02 — Fluxo integrado de alocação e rota

## Estado do fluxo integrado

O sistema utiliza o índice hash, a compatibilidade ABO/Rh e o FEFO na página
de alocação da requisição.

Arquivo principal:
`back-end/src/main/java/com/rotavital/tuguy/service/bolsahemocomponenteservice.java`

## Sequência implementada

1. A requisição informa tipo sanguíneo, componente e hospital de destino.
2. `CompatibilidadeABOService.tiposDoadoresCompativeis()` consulta a matriz e
   obtém todos os tipos doadores aceitos.
3. `EspalhamentoHash.consultarPorTipos()` busca o componente em cada tipo doador
   compatível, usando os códigos canônicos.
4. `CompatibilidadeABOService.testeABO()` confirma a compatibilidade de cada
   candidata.
5. `bolsahemocomponentefefoservice.listarEmOrdemFefo()` ordena por validade.
6. A página mostra somente bolsas compatíveis e disponíveis.
7. O usuário seleciona uma bolsa na página; `alocar()` rejeita componente
   incorreto e rejeita uma bolsa que não seja a primeira candidata FEFO.
8. Uma vez validada, `alocar()` atualiza status, vínculo e índice.
9. A página permite calcular rota quando existe uma bolsa alocada.
10. `PlanejamentoRotaService.planejar()` usa a mesma matriz de trechos para
   calcular a comparação pelo vizinho mais próximo e a rota escolhida por
   Dijkstra.
11. O planejamento reconstrói os dois caminhos e verifica validade,
    modalidade e duração sintética da rota Dijkstra.

## Atualizações do índice

O índice é reconstruído na inicialização do serviço.

Ele é atualizado ao:

- criar bolsa;
- remover bolsa;
- editar bolsa;
- alocar bolsa;
- trocar alocação;
- liberar uma alocação anterior.

## Requisições sem componente

O formulário exige um componente e o servidor rejeita a criação ou edição de
requisições sem esse campo. Se existir um registro legado sem componente, a
consulta retorna uma lista vazia e a tela informa que falta o hemocomponente.
O sistema não faz uma varredura geral do repositório como alternativa, evitando
contornar o índice hash. O registro legado precisa ser atualizado antes de
participar da alocação.

## Vizinho mais próximo e Dijkstra

`RoteamentoVizinhoMaisProximoService` implementa a busca exigida pela
Unidade 2 em Java e o planejador a executa no grafo real da aplicação.

`RoteirizacaoVizinhoEDijkstra.c` implementa as duas operações em C.

`PlanejamentoRotaService` exibe Dijkstra como menor rota global escolhida e o
vizinho mais próximo como comparação didática. Como o vizinho mais próximo é
guloso, ele pode ficar sem saída embora exista outra rota; nesse caso Dijkstra
continua fornecendo o resultado da aplicação.
