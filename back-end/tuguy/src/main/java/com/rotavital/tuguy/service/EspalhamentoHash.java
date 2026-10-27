package com.rotavital.tuguy.service;

public class EspalhamentoHash {
    public static int espalhamentoHash(int[] array, int valor) {
        int tamanho = array.length;
        int indice = valor % tamanho; // Calcula o índice usando o operador módulo
        return indice; // Retorna o índice calculado
    }

    
    
}
