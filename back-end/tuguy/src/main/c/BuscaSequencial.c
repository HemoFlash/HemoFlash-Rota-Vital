#include <iostream>

int buscaSequencial(int vetor[], int tamanho, int valor){
    for (int i=0; i<tamanho; i++){  //percorre cada indice do vetor
        if(vetor[i] == valor){ //confere se o item estocado no índice corresponde ao valor buscado
            return i;
        } 
    }
    return -1; //se não achar o valor dentro do vetor, retorna -1
}
