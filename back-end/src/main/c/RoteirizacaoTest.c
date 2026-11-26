#define ROUTE_TEST
#include "RoteirizacaoVizinhoEDijkstra.c"

#include <assert.h>

int main(void) {
    const double grafo[V][V] = {
        {0, 2, 8, 0, 0},
        {2, 0, 3, 10, 0},
        {8, 3, 0, 1, 0},
        {0, 10, 1, 0, 2},
        {0, 0, 0, 2, 0}
    };
    int caminho[V];
    double distancia = 0;

    size_t tamanho = dijkstraPath(grafo, 0, 4, caminho, V, &distancia);
    assert(tamanho == 5);
    assert(caminho[0] == 0 && caminho[1] == 1 && caminho[2] == 2
            && caminho[3] == 3 && caminho[4] == 4);
    assert(distancia == 8.0);

    tamanho = dijkstraPath(grafo, 2, 2, caminho, V, &distancia);
    assert(tamanho == 1 && caminho[0] == 2 && distancia == 0.0);

    const double desconexo[V][V] = {
        {0, 1, 0, 0, 0},
        {1, 0, 0, 0, 0},
        {0, 0, 0, 1, 0},
        {0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0}
    };
    tamanho = dijkstraPath(desconexo, 0, 4, caminho, V, &distancia);
    assert(tamanho == 0);

    const double becoSemSaida[V][V] = {
        {0, 1, 2, 0, 0},
        {1, 0, 0, 0, 0},
        {2, 0, 0, 1, 0},
        {0, 0, 1, 0, 1},
        {0, 0, 0, 1, 0}
    };
    tamanho = vizinhoMaisProximoPath(becoSemSaida, 0, 4, caminho, V, &distancia);
    assert(tamanho == 0);
    tamanho = dijkstraPath(becoSemSaida, 0, 4, caminho, V, &distancia);
    assert(tamanho == 4 && caminho[0] == 0 && caminho[1] == 2
            && caminho[2] == 3 && caminho[3] == 4);
    assert(distancia == 4.0);

    puts("Roteamento C: caminho ótimo, origem=destino e desconexão passaram.");
    return 0;
}
