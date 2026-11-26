#define HASH_TEST
#include "EspalhamentoHash.c"

#include <assert.h>

int main(void) {
    IndiceEstoqueHash indice;
    inicializarIndice(&indice);

    int chaveA = -1;
    int chaveB = -1;
    char tipoA[16];
    char tipoB[16];
    for (int i = 0; i < 1000 && chaveB < 0; i++) {
        snprintf(tipoA, sizeof(tipoA), "T%d", i);
        int bucketA = indiceBucket(tipoA, "HEMACIAS");
        for (int j = i + 1; j < 1000; j++) {
            snprintf(tipoB, sizeof(tipoB), "T%d", j);
            if (indiceBucket(tipoB, "HEMACIAS") == bucketA) {
                chaveA = i;
                chaveB = j;
                break;
            }
        }
    }
    assert(chaveA >= 0 && chaveB >= 0);
    snprintf(tipoA, sizeof(tipoA), "T%d", chaveA);
    snprintf(tipoB, sizeof(tipoB), "T%d", chaveB);

    assert(inserirBolsa(&indice, "BOLSA-SHARED", tipoA, "HEMACIAS"));
    assert(inserirBolsa(&indice, "BOLSA-SHARED", tipoB, "HEMACIAS"));
    assert(inserirBolsa(&indice, "BOLSA-A2", tipoA, "HEMACIAS"));

    BolsaIndice *resultado[4];
    size_t quantidade = buscarBolsas(&indice, tipoA, "HEMACIAS", resultado, 4);
    assert(quantidade == 2);
    assert(strcmp(resultado[0]->tipo, tipoA) == 0);
    assert(strcmp(resultado[1]->tipo, tipoA) == 0);

    quantidade = buscarBolsas(&indice, tipoB, "HEMACIAS", resultado, 4);
    assert(quantidade == 1);
    assert(strcmp(resultado[0]->tipo, tipoB) == 0);

    assert(removerBolsa(&indice, "BOLSA-SHARED", tipoA, "HEMACIAS"));
    quantidade = buscarBolsas(&indice, tipoA, "HEMACIAS", resultado, 4);
    assert(quantidade == 1);
    assert(strcmp(resultado[0]->codigo, "BOLSA-A2") == 0);
    quantidade = buscarBolsas(&indice, tipoB, "HEMACIAS", resultado, 4);
    assert(quantidade == 1);
    assert(strcmp(resultado[0]->codigo, "BOLSA-SHARED") == 0);

    destruirIndice(&indice);
    puts("Hash C: colisão, busca exata e remoção passaram.");
    return 0;
}
