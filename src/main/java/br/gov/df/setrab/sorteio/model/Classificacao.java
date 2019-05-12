package br.gov.df.setrab.sorteio.model;

import java.io.Serializable;

public class Classificacao implements Serializable {

    private String capacitacao;
    private String categoria;
    private int classificacao;
    private String nome;
    private String cpf;

    public Classificacao(String line) {
        String[] fields = line.split(",");

        setCapacitacao(fields[0]);
        setCategoria(fields[1]);
        setClassificacao(Integer.parseInt(fields[2]));
        setNome(fields[3]);
        setCpf(fields[4]);
    }

    public String getCapacitacao() {
        return capacitacao;
    }

    public void setCapacitacao(String capacitacao) {
        this.capacitacao = capacitacao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(int classificacao) {
        this.classificacao = classificacao;
    }

    public String getNome() {
        if ("Adolescentes em conflito com a lei".equals(categoria)) {
            return sanitizeNome();
        }
        return nome;
    }

    private String sanitizeNome() {
        StringBuilder builder = new StringBuilder();

        String[] tokens = nome.split(" ");

        for (String s: tokens) {
            builder.append(s.substring(0,1));
        }

        return builder.toString();
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void print() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        return "Classificacao{" +
                "capacitacao='" + capacitacao + '\'' +
                ", categoria='" + categoria + '\'' +
                ", classificacao=" + classificacao +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                '}';
    }
}
