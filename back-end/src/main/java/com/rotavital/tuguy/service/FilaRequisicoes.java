package com.rotavital.tuguy.service;
/* em c++ é necessario que uma struct do tipo requisição seja criada para definir 
os valores que serão gravados, mas em java essa definição de que tipos de dados estão
inclusos na estrutura são definidos no model "requisicao" por meio de um construtor, e 
essa definição é importada no documento atual a partir da pasta de models usando o import abaixo. 
*/


import com.rotavital.tuguy.model.requisicao;

/* Lógica feita por fila, de First In, First Out (FIFO),  */


public class FilaRequisicoes {
    /*em java, a classe FilaRequisicoes cumpre a função da struct fila em c++, que estoca
    os endereços dos nós de início e de final */
    private No inicio;
    private No fim;

    /*a definição do nó segue a mesma lógica da versão em c++, 
    mas em java é necessário que a struct seja definida como uma classe privada (encapsulamento)
    dentro da classe FilaRequisicoes, e não como uma struct separada. */
    private static class No {
        private requisicao requisicao;
        private No proximo;

        public No(requisicao requisicao) {
            this.requisicao = requisicao;
        }
    }

    /*"inicializarFila", que é usado em c++ para setar início e fim como NULL não tem uma 
    contraparte em java pois ao criar um "new filaRequisicoes" o objeto já é iniciado (inicio e fim ja começam como null) */
    

    /* "enfileirar" segue a mesma logica nas duas versoes. Em C, "malloc" reserva
    memoria; em Java, "new No(requisicao)" cria o nó. Se a fila estiver vazia, ele
    se torna inicio e fim. Caso contrario, o antigo fim aponta para o novo no e fim
    e atualizado. A requisicao entra no final, preservando a ordem. */
    public void enfileirar(requisicao requisicao) {
        No novoNo = new No(requisicao);

        if (inicio == null) {
            inicio = novoNo;
            fim = novoNo;
            return;
        }

        fim.proximo = novoNo;
        fim = novoNo;
    }

    /*"desenfileirar" remove o no do inicio e avanca inicio. Se a fila ficar vazia,
    fim tambem e atualizado. Em C, retorna 0 se vazia, copia a requisicao para o
    parametro de saida e libera o no com "free". Em Java, retorna null se vazia e
    devolve o objeto retirado; a memoria e gerenciada automaticamente. C usa
    "inicializarFila"; os campos Java comecam como null.*/
    public requisicao desenfileirar() {
        if (inicio == null) {
            return null;
        }

        requisicao requisicaoRetirada = inicio.requisicao;
        inicio = inicio.proximo;

        if (inicio == null) {
            fim = null;
        }

        return requisicaoRetirada;
    }

    public requisicao consultarInicio() {
        if (inicio == null) {
            return null;
        }

        return inicio.requisicao;
    }

    //"filaVazia" em c++ tem como equivalente "estaVazia" em java, e elas checam se a fila está vazia ou não.
    public boolean estaVazia() {
        return inicio == null;
    }
}

/* a versão em c++ tem um main e funções de imprimir com o proposito de teste, a versão em java
não tem pois sua funcionalidade deve ser ligada ao jpa e o banco. */ 