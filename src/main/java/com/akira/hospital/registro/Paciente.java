package com.akira.hospital.registro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Paciente implements Entidade
{
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("ddMMyyyy");

    private final String nome;
    private final String cpf;

    private LocalDate nascimento;
    private boolean internado = false;
    private List<Consulta> consultas = new ArrayList<>();
    private List<Internacao> internacoes = new ArrayList<>();

    public Paciente(String nome, String cpf, LocalDate nascimento)
    {
        this.nome = nome;
        this.cpf = cpf;
        this.nascimento = nascimento;
    }

    public LocalDate getNascimento() {
        return nascimento;
    }

    public int getIdade() {
        return Period.between(nascimento, LocalDate.now()).getYears();
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String getID() {
        return cpf;
    }

    @Override
    public String paraDado() {

        String linhaConsultas = consultas.stream()
                .map(consulta -> String.valueOf(consulta.getID()))
                .collect(Collectors.joining(";"));

        String linhaInternacoes = internacoes.stream()
                .map(internacao -> String.valueOf(internacao.getID()))
                .collect(Collectors.joining(";"));

        return String.join(",", cpf, nome, nascimento.format(FORMATO_DATA), linhaConsultas, linhaInternacoes);
    }

    public List<Consulta> getConsultas() {
        return consultas;
    }

    public List<Internacao> getInternacoes() {
        return internacoes;
    }

    public void setConsultas(List<Consulta> consultas)
    {
        this.consultas = consultas;
    }

    public void setInternacoes(List<Internacao> internacoes)
    {
        this.internacoes = internacoes;
    }

    public void adicionarConsulta(Consulta consulta)
    {
        consultas.add(consulta);
    }

    public void adicionarInternacao(Internacao internacao)
    {
        internacoes.add(internacao);
        setInternado(true);
    }

    public boolean estaInternado() {
        return internado;
    }

    public void setInternado(boolean f) {
        internado = f;
    }

    public static Paciente converterDado(String line) {
        String[] linha = line.split(",");
        return new Paciente(linha[1], linha[0], LocalDate.parse(linha[2], FORMATO_DATA));
    }

    public static Paciente converterID(String id)
    {
        File f = new File("resources/banks");
        if(!f.exists()) f.mkdirs();

        try (BufferedReader reader = new BufferedReader(new FileReader(new File(f, "pacientes.csv"))))
        {
            reader.readLine();
            String line;
            while((line = reader.readLine()) != null)
            {
                Paciente paciente = converterDado(line);
                if(paciente.getID().equals(id)) return paciente;
            }
        }
        catch (IOException e)
            {
                e.printStackTrace();
            }

        return null;
    }
}
