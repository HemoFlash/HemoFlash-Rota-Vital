#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_CODIGO 32
#define MAX_COMPONENTE 32
#define MAX_STATUS 16
#define MAX_BOLSAS 32

typedef struct {
    char codigo[MAX_CODIGO];
    char componente[MAX_COMPONENTE];
    char status[MAX_STATUS];
    int dataValidade;
    bool disponivel;
} BolsaHemocomponente;

static int compararFefo(const BolsaHemocomponente *bolsaAtual, const BolsaHemocomponente *novaBolsa) {
    if (bolsaAtual == NULL || novaBolsa == NULL) {
        return 0;
    }

    if (bolsaAtual->dataValidade != novaBolsa->dataValidade) {
        return bolsaAtual->dataValidade < novaBolsa->dataValidade ? -1 : 1;
    }

    return strcmp(bolsaAtual->codigo, novaBolsa->codigo);
}

static void inserirOrdenadoFefo(BolsaHemocomponente *resultado, int *quantidade, BolsaHemocomponente bolsa, int capacidade) {
    if (resultado == NULL || quantidade == NULL || capacidade <= 0) {
        return;
    }

    int indice = 0;
    while (indice < *quantidade && compararFefo(&resultado[indice], &bolsa) <= 0) {
        indice++;
    }

    if (*quantidade >= capacidade) {
        return;
    }

    for (int i = *quantidade; i > indice; i--) {
        resultado[i] = resultado[i - 1];
    }

    resultado[indice] = bolsa;
    (*quantidade)++;
}

static bool estaDisponivel(const BolsaHemocomponente *bolsa) {
    if (bolsa == NULL) {
        return false;
    }

    if (!bolsa->disponivel) {
        return false;
    }

    return strcmp(bolsa->status, "DISPONIVEL") == 0;
}

int listarEmOrdemFefo(const BolsaHemocomponente *bolsas, int total, BolsaHemocomponente *resultado, int capacidade) {
    if (resultado == NULL || capacidade <= 0) {
        return 0;
    }

    int quantidade = 0;

    for (int i = 0; i < total; i++) {
        if (bolsas == NULL || !estaDisponivel(&bolsas[i])) {
            continue;
        }

        inserirOrdenadoFefo(resultado, &quantidade, bolsas[i], capacidade);
    }

    return quantidade;
}

BolsaHemocomponente selecionarProximaFefo(const BolsaHemocomponente *bolsas, int total) {
    BolsaHemocomponente vazia = {"", "", "", 0, false};
    BolsaHemocomponente ordenadas[MAX_BOLSAS];
    int quantidade = listarEmOrdemFefo(bolsas, total, ordenadas, MAX_BOLSAS);

    if (quantidade == 0) {
        return vazia;
    }

    return ordenadas[0];
}

int main(void) {
    BolsaHemocomponente bolsas[] = {
        {"B3", "Hemacias", "DISPONIVEL", 30, true},
        {"B1", "Hemacias", "DISPONIVEL", 5, true},
        {"B2", "Hemacias", "DISPONIVEL", 15, true},
        {"C1", "Hemacias", "ALOCADA", 1, false},
        {"A1", "Hemacias", "DISPONIVEL", 10, true}
    };

    BolsaHemocomponente resultado[MAX_BOLSAS];
    int quantidade = listarEmOrdemFefo(bolsas, 5, resultado, MAX_BOLSAS);

    printf("Lista FEFO:\n");
    for (int i = 0; i < quantidade; i++) {
        printf("%d - codigo=%s validade=%d\n", i + 1, resultado[i].codigo, resultado[i].dataValidade);
    }

    BolsaHemocomponente proxima = selecionarProximaFefo(bolsas, 5);
    printf("\nProxima bolsa FEFO: %s\n", proxima.codigo);

    return 0;
}
