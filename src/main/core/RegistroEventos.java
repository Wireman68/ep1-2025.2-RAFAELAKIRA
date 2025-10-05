package main.core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import main.bancos.BancoDeDados;
import main.registro.*;

import java.util.Comparator;

public class RegistroEventos
{
    // NOME DO DIRETÓRIO QUE FICARÁ SALVO OS DADOS
    private static final String DIRETORIO = "resources/banks";
    // NOMES DE CADA BANCO DE DADOS
    private static final String[] NOMEBANCOS = {"pacientes", "medicos", "consultas", "internacoes", "planos"};

    //LEMBRETE DE ORDEM DE REGISTRO: 1 - Pacientes, 2 - Medicos, 3 - Consultas, 4 - Internacoes, 5 - Planos De Saude, 6 - Listas dentro de pacientes
    public static void initBancoDeDados(BancoDeDados db)
    {
        File f = new File(DIRETORIO);
        if(!f.exists()) f.mkdirs();

        for(String s : NOMEBANCOS)
        {
            File file = new File(f, s + ".csv");

            if(!file.exists())
            {
                try(PrintWriter writer = new PrintWriter(new FileWriter(file)))
                {
                    switch(s)
                    {
                        case "pacientes" -> writer.println("cpf,nome,idade,historicoConsulta,historicaInternacao");
                        case "medicos" -> writer.println("crm,nome,especialidades,custoConsulta");
                        case "consultas" -> writer.println("IDpaciente,IDmedico,data,");
                        case "internacoes" -> writer.println("IDpaciente,IDmedico,data");
                        case "planos" -> writer.println("nome,plano,dataDeValidade,desconto");
                    }
                } catch (IOException e)
                {
                    System.out.println("Error:" + e.getMessage());
                }
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file)))
            {
                reader.readLine();
                String line;
                while((line = reader.readLine()) != null)
                {
                    if(line.trim().isEmpty()) continue;
                    switch(s)
                    {
                        case "pacientes" -> db.registrar(Paciente.converterDado(line), true);
                        case "medicos" -> db.registrar(Medico.converterDado(line), true);
                        case "consultas" -> db.registrar(Consulta.converterDado(line), true);
                        case "internacoes" -> db.registrar(Internacao.converterDado(line), true);
                        case "planos" -> db.registrar(PlanoDeSaude.converterDado(line), true);
                    }

                    for(Paciente paciente : db.getPacientes())
                    {
                        paciente.getConsultas().clear();
                        paciente.getInternacoes().clear();
                        paciente.converterHistoricos(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void resetBancoDeDados()
    {
        try
        {
            Files.walk(Path.of(DIRETORIO))
                    .sorted(Comparator.reverseOrder())
                    .forEach(path ->
                            {
                                try
                                {
                                    Files.delete(path);
                                }
                                catch (IOException e)
                                {
                                    System.out.println("Erro: não foi possível deletar um dos bancos:" + e.getMessage());
                                }
                            }

                    );
            System.out.println("Todos os bancos foram limpos com sucesso.");
        } catch (IOException e)
        {
            System.out.println("Erro limpando os bancos:" + e.getMessage());
        }
    }

    public static void salvarDeferidos(BancoDeDados db)
    {
        resetBancoDeDados();
        File f = new File(DIRETORIO);
        if(!f.exists()) f.mkdirs();

        for(String s : NOMEBANCOS) {
            File file = new File(f, s + ".csv");

            if (!file.exists()) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    switch (s) {
                        case "pacientes" -> {
                            writer.println("cpf,nome,idade,historicoConsulta,historicaInternacao");
                            db.getPacientes().forEach(e -> writer.println(e.paraDado() + "\n"));
                        }
                        case "medicos" -> {
                            writer.println("crm,nome,especialidades,custoConsulta");
                            db.getMedicos().forEach(e -> writer.println(e.paraDado() + "\n"));
                        }
                        case "consultas" -> {
                            writer.println("IDpaciente,IDmedico,data,");
                            db.getConsultas().forEach(e -> writer.println(e.paraDado() + "\n"));
                        }
                        case "internacoes" -> {
                            writer.println("IDpaciente,IDmedico,data");
                            db.getInternacoes().forEach(e -> writer.println(e.paraDado() + "\n"));
                        }
                        case "planos" -> {
                            writer.println("nome,plano,dataDeValidade,desconto");
                            db.getPlanos().forEach(e -> writer.println(e.paraDado() + "\n"));
                        }

                    }
                } catch (IOException e) {
                    System.out.println("Erro:" + e.getMessage());
                }
            }
        }
    }

    public static void registrarConsulta(BancoDeDados db, Consulta c)
    {
        db.registrar(c, false);
        c.getPaciente().adicionarConsulta(c);
        c.getMedico().agendar(c.getData());
    }

    public static void registrarInternacao(BancoDeDados db, Internacao i) throws IllegalArgumentException
    {
        db.getInternacoes().forEach(internacao ->
        {
            if(!internacao.getStatus() && internacao.getQuarto() == i.getQuarto()) throw new IllegalArgumentException("Erro: quarto está ocupado.");
        });
        db.registrar(i, false);
        i.getPaciente().adicionarInternacao(i);
    }
}
