#include <ctype.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define TOTAL_TIPOS_SANGUINEOS 8
#define TAMANHO_TIPO 8

static const char *TIPOS_SANGUINEOS[TOTAL_TIPOS_SANGUINEOS] = {
    "O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"
};

static const bool MATRIZ_COMPATIBILIDADE[TOTAL_TIPOS_SANGUINEOS][TOTAL_TIPOS_SANGUINEOS] = {
    {true,  false, false, false, false, false, false, false}, // O-
    {true,  true,  false, false, false, false, false, false}, // O+
    {true,  false, true,  false, false, false, false, false}, // A-
    {true,  true,  true,  true,  false, false, false, false}, // A+
    {true,  false, false, false, true,  false, false, false}, // B-
    {true,  true,  false, false, true,  true,  false, false}, // B+
    {true,  false, true,  false, true,  false, true,  false}, // AB-
    {true,  true,  true,  true,  true,  true,  true,  true }  // AB+
};

static void removerEspacos(char *texto) {
    if (texto == NULL) {
        return;
    }

    size_t inicio = 0;
    while (texto[inicio] != '\0' && isspace((unsigned char)texto[inicio])) {
        inicio++;
    }

    if (inicio > 0) {
        memmove(texto, texto + inicio, strlen(texto + inicio) + 1);
    }

    size_t fim = strlen(texto);
    while (fim > 0 && isspace((unsigned char)texto[fim - 1])) {
        texto[fim - 1] = '\0';
        fim--;
    }
}

static void normalizarTipoSanguineo(char *tipo) {
    if (tipo == NULL) {
        return;
    }

    removerEspacos(tipo);
    for (size_t i = 0; tipo[i] != '\0'; i++) {
        tipo[i] = (char)toupper((unsigned char)tipo[i]);
    }
}

static int indiceTipoSanguineo(const char *tipo) {
    if (tipo == NULL) {
        return -1;
    }

    char tipoNormalizado[TAMANHO_TIPO];
    snprintf(tipoNormalizado, sizeof(tipoNormalizado), "%s", tipo);
    normalizarTipoSanguineo(tipoNormalizado);

    for (int i = 0; i < TOTAL_TIPOS_SANGUINEOS; i++) {
        if (strcmp(TIPOS_SANGUINEOS[i], tipoNormalizado) == 0) {
            return i;
        }
    }

    return -1;
}

bool testeABO(const char *tipoRequisicao, const char *tipoBolsa) {
    if (tipoRequisicao == NULL || tipoBolsa == NULL) {
        return false;
    }

    char receptor[TAMANHO_TIPO];
    char doador[TAMANHO_TIPO];

    snprintf(receptor, sizeof(receptor), "%s", tipoRequisicao);
    snprintf(doador, sizeof(doador), "%s", tipoBolsa);

    normalizarTipoSanguineo(receptor);
    normalizarTipoSanguineo(doador);

    if (strlen(receptor) == 0 || strlen(doador) == 0) {
        return false;
    }

    int indiceReceptor = indiceTipoSanguineo(receptor);
    int indiceDoador = indiceTipoSanguineo(doador);

    if (indiceReceptor < 0 || indiceDoador < 0) {
        return false;
    }

    return MATRIZ_COMPATIBILIDADE[indiceReceptor][indiceDoador];
}

int main(void) {
    const char *tipos[] = {"O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"};

    printf("Teste da matriz de compatibilidade ABO/Rh\n");
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            printf("%s -> %s = %s\n",
                   tipos[i],
                   tipos[j],
                   testeABO(tipos[i], tipos[j]) ? "true" : "false");
        }
    }

    printf("\nCasos especiais:\n");
    printf("A+ -> O- = %s\n", testeABO("A+", "O-") ? "true" : "false");
    printf("A+ -> B+ = %s\n", testeABO("A+", "B+") ? "true" : "false");
    printf("AB+ -> O- = %s\n", testeABO("AB+", "O-") ? "true" : "false");
    printf("O- -> AB+ = %s\n", testeABO("O-", "AB+") ? "true" : "false");

    return 0;
}
