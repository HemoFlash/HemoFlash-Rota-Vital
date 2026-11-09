#include <stdio.h>

int buscaSequencial(int vetor[], int tamanho, int valor) {
    for (int i = 0; i < tamanho; i++) {
        if (vetor[i] == valor) {
            return i;
        }
    }
    return -1;
}

int main(void) {
    int vetor[] = {10, 20, 30, 40, 50};
    int tamanho = 5;

    printf("Indice do valor 30: %d\n", buscaSequencial(vetor, tamanho, 30));
    printf("Indice do valor 99: %d\n", buscaSequencial(vetor, tamanho, 99));

    return 0;
}
