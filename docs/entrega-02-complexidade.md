# Entrega 02 — Análise de complexidade

As complexidades abaixo consideram a implementação atual do projeto Rota
Vital, não uma versão genérica.

## Hash de estoque

Chave: `tipoSanguineo + "|" + componenteCanonico`, por exemplo
`O+|HEMACIAS`.

### Implementação Java

A consulta exata usa `Map.get(chave)`. O método `consultar()` copia a lista de
bolsas daquela chave; a consulta integrada repete esse passo para cada tipo
doador compatível (no máximo oito tipos):

- procurar uma chave: `O(1)` esperado, `O(n)` no pior caso de colisão do mapa;
- copiar os candidatos de uma chave com `bᵢ` bolsas: `O(bᵢ)`;
- consultar os até oito tipos compatíveis e combinar `b` resultados:
  `O(1 + b)` esperado, `O(n + b)` no pior caso de colisões.

`reconstruir()` percorre todas as `n` bolsas uma vez: `O(n)`.

### Implementação C

`hashString()` percorre `k` caracteres da chave, portanto custa `O(k)`. A
busca percorre os `m` nós do bucket e compara as chaves completas: `O(k + m)`.
No pior caso, todas as `n` bolsas caem no mesmo bucket, resultando em
`O(k + n)`. A remoção percorre o bucket da mesma forma.

## FEFO por inserção ordenada

Para inserir uma bolsa no lugar correto, a implementação percorre os elementos
anteriores e depois desloca a lista ou o vetor.

- inserção de uma bolsa: `O(n)`;
- ordenar `n` bolsas: `O(n²)`;
- melhor caso da ordenação completa: `O(n²)`, pois cada nova bolsa ainda
  percorre a lista ordenada para encontrar a posição;
- pior caso da ordenação completa: `O(n²)`, com deslocamentos adicionais
  quando as bolsas chegam em ordem inversa.

Em C, o deslocamento é explícito em `resultado[i] = resultado[i - 1]`.
Em Java, `ArrayList.add(indice, bolsa)` causa o mesmo deslocamento internamente.

## Compatibilidade ABO/Rh

A matriz possui oito tipos sanguíneos:

- busca do índice receptor: `O(8)`;
- busca do índice doador: `O(8)`;
- consulta da matriz: `O(1)`.

Como o número de tipos é constante, a operação completa pode ser considerada
`O(1)` na prática.

Sem esse limite fixo, a busca sequencial dos tipos teria `O(t)`, sendo `t` o
tamanho do vetor de tipos.

## Vizinho mais próximo

Seja `V` o número de hospitais e `E` o número de conexões.

A implementação Java percorre uma única vez as listas de adjacência dos
vértices selecionados:

- complexidade: `O(V + E)` esperado, considerando consultas `O(1)` no conjunto
  de visitados;
- não usa fila de prioridade;
- em um grafo pequeno, a solução é simples e didática;
- ela não garante o menor caminho global em todos os grafos.

A versão C usa matriz de adjacência e varre até `V` posições para escolher cada
vizinho, resultando em `O(V²)`.

## Dijkstra

A implementação Java usa `PriorityQueue` e `HashMap`, com atualização dos
predecessores.

- complexidade: `O((V + E) log V)`;
- extração da menor distância: `O(log V)`;
- relaxamento de cada trecho: `O(log V)` no pior caso;
- busca de uma rota: `O((V + E) log V)`.

A versão C usa vetor e varredura completa para encontrar o menor vértice:

- `O(V²)`.

## Fluxo integrado

Para uma requisição com `n` bolsas no sistema, `b` bolsas candidatas e um grafo
com `V` hospitais e `E` trechos:

1. hash consulta os tipos doadores compatíveis para o componente, até oito
   chaves, e produz `b` candidatas;
2. matriz ABO/Rh confirma os candidatos;
3. FEFO ordena `b` bolsas em `O(b²)`;
4. o vizinho mais próximo percorre o grafo em Java em `O(V + E)`;
5. Dijkstra calcula a rota escolhida em `O((V + E) log V)`;
6. a reconstrução da rota percorre no máximo `V` predecessores: `O(V)`.

Complexidade algorítmica geral, sem contabilizar consultas ao banco e
persistência:

`O(b² + V + E + (V + E) log V)`

O formulário e o servidor exigem componente. Para um registro legado sem esse
campo, a consulta retorna uma lista vazia em `O(1)` e não varre o repositório;
o registro precisa ser atualizado para entrar no fluxo de hash e FEFO.
