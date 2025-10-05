package main.registro;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Paciente implements Entidade
{
    private final String nome;
    private final String cpf;

    private int idade;
    private boolean internado = false;
    private List<Consulta> consultas = new ArrayList<>();
    private List<Internacao> internacoes = new ArrayList<>();

    public Paciente(String nome, String cpf, int idade)
    {
        this.nome = nome;
        this.cpf = cpf;
        this.idade = idade;
    }

    public int getIdade() {
        return idade;
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

        return String.join(",", nome, cpf, String.valueOf(idade), linhaConsultas, linhaInternacoes);
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

    @Override
    public void displayDados()
    {
        System.out.println("Nome: " + nome + ", CPF: " + cpf + ", idade: " + idade);
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
        return new Paciente(linha[0], linha[1], Integer.parseInt(linha[2]));
    }

    public void converterHistoricos(String line)
    {
        String[] linha = line.split(",");

        List<Consulta> tempConsultas = new ArrayList<>();
        if (linha.length > 3 && !linha[3].isBlank()) {
            for (String s : linha[3].split(";")) {
                Consulta consulta = Consulta.converterID(s);
                if (consulta != null) tempConsultas.add(consulta);
            }
        }

        List<Internacao> tempInternacoes = new ArrayList<>();
        if (linha.length > 4 && !linha[4].isBlank()) {
            for (String s : linha[4].split(";")) {
                Internacao internacao = Internacao.converterID(s);
                if (internacao != null) tempInternacoes.add(internacao);
            }
        }

        this.setConsultas(tempConsultas);
        this.setInternacoes(tempInternacoes);
    }

    public static Paciente converterID(String id)
    {
        try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(Paths.get("pacientes.csv"))))
        {
            reader.readLine();
            String line;
            while((line = reader.readLine()) != null)
            {
                Paciente paciente = converterDado(line);
                if(paciente.getID() == id) return paciente;
            }
        }
        catch (IOException e)
            {
                e.printStackTrace();
            }

        return null;
    }
}
