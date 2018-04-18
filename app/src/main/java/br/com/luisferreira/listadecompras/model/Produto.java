package br.com.luisferreira.listadecompras.model;

import java.util.HashMap;
import java.util.Map;

public class Produto {

    private String id;
    private String nome;
    private String codigoDeBarras;
    private int quantidade = 0;
    private double precoUnitario = 0.00;
    private double precoTotalItem = 0.00;

    public Produto() {

    }

    public Produto(String id, String nome, String codigoDeBarras, int quantidade, double precoUnitario, double precoTotalItem) {
        this.id = id;
        this.nome = nome;
        this.codigoDeBarras = codigoDeBarras;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.precoTotalItem = precoTotalItem;
    }

    public Produto(String nome, String codigoDeBarras, int quantidade, double precoUnitario, double precoTotalItem) {
        this.nome = nome;
        this.codigoDeBarras = codigoDeBarras;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.precoTotalItem = precoTotalItem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigoDeBarras() {
        return codigoDeBarras;
    }

    public void setCodigoDeBarras(String codigoDeBarras) {
        this.codigoDeBarras = codigoDeBarras;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public double getPrecoTotalItem() {
        return precoTotalItem;
    }

    public void setPrecoTotalItem(double precoTotalItem) {
        this.precoTotalItem = precoTotalItem;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("nome", this.nome);
        result.put("codigoDeBarras", this.codigoDeBarras);
        result.put("quantidade", this.quantidade);
        result.put("precoUnitario", this.precoUnitario);
        result.put("precoTotalItem", this.precoTotalItem);

        return result;
    }
}