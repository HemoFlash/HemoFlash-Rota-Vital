#include <float.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>

#define V 5

static const double GRAFO_EXEMPLO[V][V] = {
    {0.0, 1.4, 2.1, 1.2, 1.6},
    {1.4, 0.0, 1.2, 1.8, 2.2},
    {2.1, 1.2, 0.0, 2.4, 2.8},
    {1.2, 1.8, 2.4, 0.0, 1.1},
    {1.6, 2.2, 2.8, 1.1, 0.0}
};

static int verticeNaoVisitadoComMenorDistancia(const double distancia[V],
                                                const bool visitado[V]) {
    int candidato = -1;
    double menor = DBL_MAX;
    for (int i = 0; i < V; i++) {
        if (!visitado[i] && distancia[i] < menor) {
            menor = distancia[i];
            candidato = i;
        }
    }
    return candidato;
}

static size_t reconstruirCaminho(int origem, int destino, const int anterior[V],
                                 int caminho[V], size_t capacidade) {
    int reverso[V];
    size_t tamanho = 0;
    int atual = destino;

    while (atual != -1 && tamanho < V) {
        reverso[tamanho++] = atual;
        if (atual == origem) {
            break;
        }
        atual = anterior[atual];
    }
    if (tamanho == 0 || tamanho > capacidade || reverso[tamanho - 1] != origem) {
        return 0;
    }

    for (size_t i = 0; i < tamanho; i++) {
        caminho[i] = reverso[tamanho - i - 1];
    }
    return tamanho;
}

size_t dijkstraPath(const double grafo[V][V], int origem, int destino,
                    int caminho[V], size_t capacidade, double *distanciaFinal) {
    if (grafo == NULL || caminho == NULL || distanciaFinal == NULL || capacidade == 0
            || origem < 0 || origem >= V || destino < 0 || destino >= V) {
        return 0;
    }

    double distancia[V];
    int anterior[V];
    bool visitado[V] = {false};
    for (int i = 0; i < V; i++) {
        distancia[i] = DBL_MAX;
        anterior[i] = -1;
    }
    distancia[origem] = 0.0;

    for (int contador = 0; contador < V; contador++) {
        int atual = verticeNaoVisitadoComMenorDistancia(distancia, visitado);
        if (atual == -1) {
            break;
        }
        if (atual == destino) {
            break;
        }
        visitado[atual] = true;

        for (int vizinho = 0; vizinho < V; vizinho++) {
            double trecho = grafo[atual][vizinho];
            if (!visitado[vizinho] && trecho > 0.0
                    && distancia[atual] + trecho < distancia[vizinho]) {
                distancia[vizinho] = distancia[atual] + trecho;
                anterior[vizinho] = atual;
            }
        }
    }

    if (distancia[destino] == DBL_MAX) {
        return 0;
    }

    size_t tamanho = reconstruirCaminho(origem, destino, anterior, caminho, capacidade);
    if (tamanho == 0) {
        return 0;
    }
    *distanciaFinal = distancia[destino];
    return tamanho;
}

size_t vizinhoMaisProximoPath(const double grafo[V][V], int origem, int destino,
                              int caminho[V], size_t capacidade, double *distanciaFinal) {
    if (grafo == NULL || caminho == NULL || distanciaFinal == NULL || capacidade == 0
            || origem < 0 || origem >= V || destino < 0 || destino >= V) {
        return 0;
    }

    bool visitado[V] = {false};
    int atual = origem;
    size_t tamanho = 0;
    double distancia = 0.0;
    caminho[tamanho++] = origem;
    visitado[origem] = true;

    while (atual != destino) {
        int vizinhoEscolhido = -1;
        double menorTrecho = DBL_MAX;
        for (int vizinho = 0; vizinho < V; vizinho++) {
            if (!visitado[vizinho] && grafo[atual][vizinho] > 0.0
                    && grafo[atual][vizinho] < menorTrecho) {
                menorTrecho = grafo[atual][vizinho];
                vizinhoEscolhido = vizinho;
            }
        }
        if (vizinhoEscolhido == -1 || tamanho >= capacidade || tamanho >= V) {
            return 0;
        }

        atual = vizinhoEscolhido;
        visitado[atual] = true;
        caminho[tamanho++] = atual;
        distancia += menorTrecho;
    }

    *distanciaFinal = distancia;
    return tamanho;
}

#ifndef ROUTE_TEST
static void imprimirCaminho(const char *algoritmo, const int caminho[V],
                            size_t tamanho, double distancia) {
    if (tamanho == 0) {
        printf("%s: caminho não encontrado.\n", algoritmo);
        return;
    }
    printf("%s: ", algoritmo);
    for (size_t i = 0; i < tamanho; i++) {
        printf("%d%s", caminho[i], i + 1 < tamanho ? " -> " : "");
    }
    printf(" | distância = %.2f km\n", distancia);
}

int main(void) {
    int caminho[V];
    double distancia = 0.0;
    size_t tamanho = vizinhoMaisProximoPath(
            GRAFO_EXEMPLO, 0, 4, caminho, V, &distancia);
    imprimirCaminho("Vizinho mais próximo", caminho, tamanho, distancia);

    tamanho = dijkstraPath(GRAFO_EXEMPLO, 0, 4, caminho, V, &distancia);
    imprimirCaminho("Dijkstra", caminho, tamanho, distancia);
    return EXIT_SUCCESS;
}
#endif
