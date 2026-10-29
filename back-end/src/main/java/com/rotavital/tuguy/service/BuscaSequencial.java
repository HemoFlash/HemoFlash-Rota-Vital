package com.rotavital.tuguy.service;

public class BuscaSequencial {
    public static int buscaSequencial(int[] array, int valor) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == valor) {
                return i; // Retorna o índice do elemento encontrado
            }
        }
        return -1; // Retorna -1 se o elemento não for encontrado
    }

    
}
