package com.example.sistemadegerenciamento.models;

import java.io.Serializable;

/**
 * Classe responsável por armazenar dados e comportamentos da peça.
 * */
public class Peca implements Serializable {

    private String nome;
    private double valor;

    //construtor
    public Peca(String nome, double valor){
        this.nome = nome;
        this.valor = valor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
