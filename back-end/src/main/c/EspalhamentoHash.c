#include <ctype.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define TAMANHO_INDICE_HASH 64
#define TAMANHO_TIPO 8
#define TAMANHO_COMPONENTE 32
#define TAMANHO_CODIGO 32

typedef struct BolsaIndice {
    char codigo[TAMANHO_CODIGO];
    char tipo[TAMANHO_TIPO];
    char componente[TAMANHO_COMPONENTE];
    struct BolsaIndice *proximo;
} BolsaIndice;

typedef struct {
    BolsaIndice *buckets[TAMANHO_INDICE_HASH];
} IndiceEstoqueHash;

static bool copiarMaiusculas(char *destino, size_t capacidade, const char *origem) {
    if (destino == NULL || capacidade == 0 || origem == NULL) {
        return false;
    }

    while (isspace((unsigned char)*origem)) {
        origem++;
    }
    size_t tamanho = strlen(origem);
    while (tamanho > 0 && isspace((unsigned char)origem[tamanho - 1])) {
        tamanho--;
    }
    if (tamanho == 0 || tamanho >= capacidade) {
        return false;
    }

    for (size_t i = 0; i < tamanho; i++) {
        destino[i] = (char)toupper((unsigned char)origem[i]);
    }
    destino[tamanho] = '\0';
    return true;
}

static bool normalizarChave(const char *tipo, const char *componente,
                            char tipoNormalizado[TAMANHO_TIPO],
                            char componenteNormalizado[TAMANHO_COMPONENTE]) {
    return copiarMaiusculas(tipoNormalizado, TAMANHO_TIPO, tipo)
            && copiarMaiusculas(componenteNormalizado, TAMANHO_COMPONENTE, componente);
}

static unsigned long hashString(const char *chave) {
    unsigned long hash = 0;
    while (*chave != '\0') {
        hash = hash * 31 + (unsigned char)*chave;
        chave++;
    }
    return hash;
}

static int indiceBucket(const char *tipo, const char *componente) {
    char chave[TAMANHO_TIPO + TAMANHO_COMPONENTE + 2];
    snprintf(chave, sizeof(chave), "%s|%s", tipo, componente);
    return (int)(hashString(chave) % TAMANHO_INDICE_HASH);
}

void inicializarIndice(IndiceEstoqueHash *indice) {
    if (indice == NULL) {
        return;
    }
    for (int i = 0; i < TAMANHO_INDICE_HASH; i++) {
        indice->buckets[i] = NULL;
    }
}

bool inserirBolsa(IndiceEstoqueHash *indice, const char *codigo,
                  const char *tipo, const char *componente) {
    char tipoNormalizado[TAMANHO_TIPO];
    char componenteNormalizado[TAMANHO_COMPONENTE];
    if (indice == NULL || codigo == NULL
            || !normalizarChave(tipo, componente, tipoNormalizado, componenteNormalizado)) {
        return false;
    }

    char codigoNormalizado[TAMANHO_CODIGO];
    if (!copiarMaiusculas(codigoNormalizado, sizeof(codigoNormalizado), codigo)) {
        return false;
    }

    int bucket = indiceBucket(tipoNormalizado, componenteNormalizado);
    for (BolsaIndice *atual = indice->buckets[bucket]; atual != NULL; atual = atual->proximo) {
        if (strcmp(atual->codigo, codigoNormalizado) == 0
                && strcmp(atual->tipo, tipoNormalizado) == 0
                && strcmp(atual->componente, componenteNormalizado) == 0) {
            return true;
        }
    }

    BolsaIndice *nova = (BolsaIndice *)malloc(sizeof(BolsaIndice));
    if (nova == NULL) {
        return false;
    }
    snprintf(nova->codigo, sizeof(nova->codigo), "%s", codigoNormalizado);
    snprintf(nova->tipo, sizeof(nova->tipo), "%s", tipoNormalizado);
    snprintf(nova->componente, sizeof(nova->componente), "%s", componenteNormalizado);
    nova->proximo = indice->buckets[bucket];
    indice->buckets[bucket] = nova;
    return true;
}

size_t buscarBolsas(const IndiceEstoqueHash *indice, const char *tipo,
                    const char *componente, BolsaIndice **resultados,
                    size_t capacidade) {
    char tipoNormalizado[TAMANHO_TIPO];
    char componenteNormalizado[TAMANHO_COMPONENTE];
    if (indice == NULL
            || !normalizarChave(tipo, componente, tipoNormalizado, componenteNormalizado)) {
        return 0;
    }

    size_t quantidade = 0;
    int bucket = indiceBucket(tipoNormalizado, componenteNormalizado);
    for (BolsaIndice *atual = indice->buckets[bucket]; atual != NULL; atual = atual->proximo) {
        if (strcmp(atual->tipo, tipoNormalizado) == 0
                && strcmp(atual->componente, componenteNormalizado) == 0) {
            if (resultados != NULL && quantidade < capacidade) {
                resultados[quantidade] = atual;
            }
            quantidade++;
        }
    }
    return quantidade;
}

bool removerBolsa(IndiceEstoqueHash *indice, const char *codigo,
                  const char *tipo, const char *componente) {
    char tipoNormalizado[TAMANHO_TIPO];
    char componenteNormalizado[TAMANHO_COMPONENTE];
    char codigoNormalizado[TAMANHO_CODIGO];
    if (indice == NULL
            || !copiarMaiusculas(codigoNormalizado, sizeof(codigoNormalizado), codigo)
            || !normalizarChave(tipo, componente, tipoNormalizado, componenteNormalizado)) {
        return false;
    }

    int bucket = indiceBucket(tipoNormalizado, componenteNormalizado);
    BolsaIndice **atual = &indice->buckets[bucket];
    while (*atual != NULL) {
        if (strcmp((*atual)->codigo, codigoNormalizado) == 0
                && strcmp((*atual)->tipo, tipoNormalizado) == 0
                && strcmp((*atual)->componente, componenteNormalizado) == 0) {
            BolsaIndice *removida = *atual;
            *atual = removida->proximo;
            free(removida);
            return true;
        }
        atual = &(*atual)->proximo;
    }
    return false;
}

void destruirIndice(IndiceEstoqueHash *indice) {
    if (indice == NULL) {
        return;
    }
    for (int i = 0; i < TAMANHO_INDICE_HASH; i++) {
        BolsaIndice *atual = indice->buckets[i];
        while (atual != NULL) {
            BolsaIndice *removida = atual;
            atual = atual->proximo;
            free(removida);
        }
        indice->buckets[i] = NULL;
    }
}

#ifndef HASH_TEST
int main(void) {
    IndiceEstoqueHash indice;
    inicializarIndice(&indice);
    if (!inserirBolsa(&indice, "ON-1", "O-", "HEMACIAS")
            || !inserirBolsa(&indice, "OP-1", "O+", "HEMACIAS")
            || !inserirBolsa(&indice, "OP-P1", "O+", "PLASMA")) {
        destruirIndice(&indice);
        fputs("Falha ao inserir bolsa no índice.\n", stderr);
        return EXIT_FAILURE;
    }

    BolsaIndice *resultado[8];
    size_t quantidade = buscarBolsas(&indice, "O+", "HEMACIAS", resultado, 8);
    printf("Bolsas O+ / HEMACIAS encontradas: %zu\n", quantidade);
    for (size_t i = 0; i < quantidade && i < 8; i++) {
        printf("%s\n", resultado[i]->codigo);
    }

    bool removida = removerBolsa(&indice, "OP-1", "O+", "HEMACIAS");
    printf("Remoção: %s\n", removida ? "concluída" : "não encontrada");
    destruirIndice(&indice);
    return EXIT_SUCCESS;
}
#endif
