# Entrega 02 — Tradução comentada C/C++ e Java

Este documento relaciona os quatro algoritmos exigidos pela Unidade 2 às suas
implementações C/C++ e Java no projeto Rota Vital.

## 1. Índice de estoque por hash

### Versão C

Arquivo: `back-end/src/main/c/EspalhamentoHash.c`

A chave combina o tipo sanguíneo e o código canônico do componente, por
exemplo `O+|HEMACIAS`. Java aceita rótulos antigos com acento e os converte ao
código; C recebe os códigos canônicos.

A função `hashString()` percorre os caracteres e calcula um valor inteiro.
`indiceBucket()` aplica módulo pelo tamanho do índice.

Cada posição do vetor é uma lista encadeada representada por
`BolsaIndice *proximo`. `inserirBolsa()` aloca memória com `malloc`.
`buscarBolsas()` percorre o bucket e só devolve nós cuja chave completa
corresponde à consulta, mesmo quando há colisões. `removerBolsa()` também
compara código e chave completa e usa ponteiro duplo. `destruirIndice()` chama
`free()` em todas as bolsas encadeadas.

### Versão Java

Arquivo:
`back-end/src/main/java/com/rotavital/tuguy/service/EspalhamentoHash.java`

O `Map<String, List<bolsahemocomponente>>` de Java corresponde ao vetor de
buckets em C. A chave formada por `tipo|componente` corresponde à string
montada por `indiceBucket()`. A lista associada a uma chave contém bolsas com
essa chave exata; o próprio `Map` resolve colisões entre chaves diferentes.
`consultarPorTipos()` consulta cada tipo doador compatível e combina os
resultados.

As diferenças são relacionadas à memória: C exige `malloc`, ponteiros e
`free`; Java cria objetos automaticamente e o coletor libera a memória. A
decisão algorítmica é equivalente.

## 2. Priorização FEFO

### Versão C

Arquivo: `back-end/src/main/c/fefo_prioridade.c`

O vetor `resultado` recebe cada bolsa disponível em sua posição correta.
`compararFefo()` compara primeiro a validade e depois o código.
`inserirOrdenadoFefo()` desloca os elementos para a direita e insere a bolsa.

### Versão Java

Arquivo:
`back-end/src/main/java/com/rotavital/tuguy/service/bolsahemocomponentefefoservice.java`

`ArrayList<bolsahemocomponente>` substitui o vetor C. O método
`inserirOrdenado()` percorre a lista, encontra a posição e chama `add(indice,
bolsa)`. `compararFefo()` realiza as mesmas duas comparações da versão C.

Os dois programas filtram bolsas indisponíveis antes da inserção. Em C o
deslocamento é explícito; em Java o `ArrayList.add()` realiza internamente a
movimentação dos elementos.

## 3. Compatibilidade ABO/Rh

### Versão C

Arquivo: `back-end/src/main/c/CompatibilidadeABO.c`

`TIPOS_SANGUINEOS` é um vetor de nomes e
`MATRIZ_COMPATIBILIDADE` é uma matriz bidimensional booleana.
`indiceTipoSanguineo()` encontra a posição do receptor ou doador.
`testeABO()` consulta `MATRIZ_COMPATIBILIDADE[receptor][doador]`.

### Versão Java

Arquivo:
`back-end/src/main/java/com/rotavital/tuguy/service/CompatibilidadeABOService.java`

O vetor e a matriz C correspondem aos arrays Java
`TIPOS_SANGUINEOS` e `MATRIZ_COMPATIBILIDADE`. A busca pelo índice percorre
o vetor em Java da mesma maneira que em C. Quando ambos os índices são válidos,
a matriz Java retorna o mesmo booleano da matriz C.

## 4. Roteirização

### Versão C

Arquivo:
`back-end/src/main/c/RoteirizacaoVizinhoEDijkstra.c`

Há duas implementações no mesmo programa:

- `vizinhoMaisProximoPath()` escolhe o menor trecho disponível em cada passo e
devolve a sequência encontrada.
`dijkstraPath()` mantém um vetor de predecessores; depois da busca, percorre os
predecessores do destino até a origem e inverte a sequência para produzir o
caminho final.

A matriz `grafo[V][V]` representa os trechos entre os hospitais.

### Versão Java

Arquivos:

- `back-end/src/main/java/com/rotavital/tuguy/service/RoteamentoVizinhoMaisProximoService.java`;
- `back-end/src/main/java/com/rotavital/tuguy/service/PlanejamentoRotaService.java`.

`RoteamentoVizinhoMaisProximoService` usa `Set<String>` para vértices visitados
e `List<Trecho>` para candidatos. Esses elementos correspondem aos vetores
`visitado`, `caminho` e `grafo` da versão C.

`PlanejamentoRotaService` constrói o grafo uma vez e executa os dois métodos:
o resultado do vizinho mais próximo é mostrado como comparação e Dijkstra é
apresentado como a rota escolhida. Dijkstra usa `PriorityQueue`, `HashMap` de
distâncias e predecessores, que substituem os vetores C mantendo a regra de
menor distância global.

## Equivalência do fluxo integrado

Java integra os quatro componentes da seguinte forma:

1. consulta o índice pelas chaves dos tipos doadores compatíveis e componente;
2. confirma compatibilidade pela matriz ABO/Rh;
3. ordena candidatos por FEFO e valida essa ordem na operação de alocação;
4. calcula o vizinho mais próximo e Dijkstra sobre o mesmo grafo;
5. reconstrói e apresenta ambos os caminhos, escolhendo Dijkstra para a rota;
6. valida duração e validade.

Em C, os códigos correspondentes demonstram as mesmas operações de forma
separada, como exigido pela disciplina.
