# include <iostream>
# define TAM 10  // TROCAR

int tabelaHash[TAM]; // inicializa o vetor hash com o tamanho específico

int hash (int chave) { // algoritmo de geração do indice hash
    return chave % TAM; // TROCAR  
}

void inserir (int chave){ // calcula o indice hash daquele valor e insere nesse indice dentro do vetor
    int indice = hash(chave); 
    tabelaHash[indice] = chave;
}

int buscar(int chave){ // para um valor inserido, calcula o indice hash de accordo com as regras da função, e verifica se o valor é equivalente dentro daquele indice
    int indice = hash(chave);
    if (tabelaHash[indice] == chave)
        return indice;
    else 
        return -1;
}