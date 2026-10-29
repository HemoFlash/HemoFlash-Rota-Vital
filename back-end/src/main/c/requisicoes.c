#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct Requisicao {
    int id;
    char titulo[100];
    char descricao[500];
    char prioridade[20];
};

struct No {
    struct Requisicao requisicao;
    struct No *proximo;
};

struct Fila {
    struct No *inicio;
    struct No *fim;
};

void inicializarFila(struct Fila *fila) {
    fila->inicio = NULL;
    fila->fim = NULL;
}

int filaVazia(struct Fila *fila) {
    return fila->inicio == NULL;
}

void enfileirar(struct Fila *fila, struct Requisicao requisicao) {
    struct No *novoNo = (struct No *)malloc(sizeof(struct No));

    if (novoNo == NULL) {
        printf("memoria insuficiente.\n");
        return;
    }

    novoNo->requisicao = requisicao;
    novoNo->proximo = NULL;

    if (filaVazia(fila)) {
        fila->inicio = novoNo;
        fila->fim = novoNo;
    } else {
        fila->fim->proximo = novoNo;
        fila->fim = novoNo;
    }
}

int desenfileirar(struct Fila *fila, struct Requisicao *requisicaoSaida) {
    if (filaVazia(fila)) {
        return 0;
    }

    struct No *noAtual = fila->inicio;
    *requisicaoSaida = noAtual->requisicao;

    fila->inicio = noAtual->proximo;

    if (fila->inicio == NULL) {
        fila->fim = NULL;
    }

    free(noAtual);
    return 1;
}

void imprimirFila(struct Fila *fila) {
    struct No *atual = fila->inicio;

    printf("Fila de requisicoes:\n");

    while (atual != NULL) {
        printf("ID: %d | Titulo: %s | Prioridade: %s\n",
               atual->requisicao.id,
               atual->requisicao.titulo,
               atual->requisicao.prioridade);
        atual = atual->proximo;
    }
}

int main(void) {
    struct Fila fila;
    inicializarFila(&fila);

    struct Requisicao r1 = {1, "Solicitacao 1", "Pedido de material", "Alta"};
    struct Requisicao r2 = {2, "Solicitacao 2", "Pedido de insumo", "Media"};
    struct Requisicao r3 = {3, "Solicitacao 3", "Pedido de medicamento", "Baixa"};

    enfileirar(&fila, r1);
    enfileirar(&fila, r2);
    enfileirar(&fila, r3);

    imprimirFila(&fila);

    struct Requisicao retirada;
    if (desenfileirar(&fila, &retirada)) {
        printf("Retirada da fila: %d - %s\n", retirada.id, retirada.titulo);
    }

    imprimirFila(&fila);

    return 0;
}
