package main.core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import main.bancos.BancoDeDados;
import main.registro.*;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class RegistroEventos
{
    // NOME DO DIRETÓRIO ONDE FICARÃO SALVO OS DADOS
    private static final String DIRETORIO = "resources/banks";
    //NOME DO DIRETÓRIO ONDE FICARÃO AS FICHAS DE CONSULTA
    private static final String DIRETORIO2 = "resources/diagnostics";
    //NOME DO DIRETÓRIO ONDE FICARÃO OS RELATÓRIOS
    private static final String DIRETORIO3 = "resources/reports";
    // NOMES DE CADA BANCO DE DADOS
    private static final String[] NOMEBANCOS = {"pacientes", "medicos", "consultas", "internacoes", "planos", "especiais"};

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
                        case "consultas" -> writer.println("IDpaciente,IDmedico,data,status,local");
                        case "internacoes" -> writer.println("IDpaciente,IDmedico,data,quarto,custo");
                        case "planos" -> writer.println("IDplano,nome,plano,dataDeValidade,desconto");
                        case "especiais" -> writer.println("cpf,IDplanos");
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
                        case "especiais" -> PacienteEspecial.converterPlano(line, db);
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

    public static void registrarPlanoEspecial(BancoDeDados db)
    {
        PlanoDeSaude planoDeSaude = new PlanoDeSaude("ESPECIALINTERNACAO", 0, YearMonth.now().plusYears(3), 1);
        db.registrar(planoDeSaude, false);
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
                            writer.println("cpf,nome,dataNascimento,historicoConsulta,historicaInternacao");
                            db.getPacientes().forEach(e -> writer.println(e.paraDado()));
                        }
                        case "medicos" -> {
                            writer.println("crm,nome,especialidades,custoConsulta,agenda");
                            db.getMedicos().forEach(e -> writer.println(e.paraDado()));
                        }
                        case "consultas" -> {
                            writer.println("IDpaciente,IDmedico,data,status,local");
                            db.getConsultas().forEach(e -> writer.println(e.paraDado()));
                        }
                        case "internacoes" -> {
                            writer.println("IDpaciente,IDmedico,data");
                            db.getInternacoes().forEach(e -> writer.println(e.paraDado()));
                        }
                        case "planos" -> {
                            writer.println("IDplano,nome,plano,dataDeValidade,desconto");
                            for(PlanoDeSaude e : db.getPlanos())
                            {
                                if("ESPECIALINTERNACAO".equalsIgnoreCase(e.nome()))
                                {
                                    continue;
                                }
                                writer.println(e.paraDado());
                            }
                        }
                        case "especiais" ->
                        {
                            writer.println("IDpaciente,IDplanos");
                            db.getPacientes().forEach(paciente ->
                            {
                                if(paciente instanceof PacienteEspecial pacienteEspecial)
                                {
                                    Set<PlanoDeSaude> planoDeSaudes =  pacienteEspecial.getPlanosDeSaude();
                                    String linhaPlanos = planoDeSaudes.stream()
                                            .map(planoDeSaude -> String.valueOf(planoDeSaude.getID()))
                                            .collect(Collectors.joining(";"));

                                    if(!linhaPlanos.isEmpty())
                                    {
                                        writer.println(String.join(",", pacienteEspecial.getID(), linhaPlanos));
                                    }
                                }
                            });
                        }

                    }
                } catch (IOException e) {
                    System.out.println("Erro:" + e.getMessage());
                }
            }
        }
    }

    public static void criarPastasConsulta(BancoDeDados db)
    {
        File f = new File(DIRETORIO2);
        if(!f.exists()) f.mkdirs();

        for(Medico medico : db.getMedicos())
        {
            File mf = new File(f, medico.getID());
            if(!mf.exists()) f.mkdirs();
        }
    }

    public static void criarPastasRelatorios(BancoDeDados db)
    {
        File f = new File(DIRETORIO3);
        if(!f.exists()) f.mkdirs();
    }

    public static void criarDiagnostico(BancoDeDados db, String mId)
    {
        Consulta consulta = db.getConsultaMedico(mId);
        Medico medico = db.getMedico(mId);

        assert consulta != null;
        assert medico != null;

        File f = new File(DIRETORIO2 + medico.getID());
        if(!f.exists()) f.mkdirs();

        File diagnostico = new File(f, consulta.getPaciente().getID() + " - " + consulta.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")) + ".txt");

        try(PrintWriter writer = new PrintWriter(new FileWriter(diagnostico)))
        {
            writer.println("Consulta " + consulta.getID());
            writer.println("==========================================");
            writer.println("Paciente: " + consulta.getPaciente().getNome());
            writer.println("Médico responsável: Dr. " + medico.getNome());
            writer.println("Data: " + consulta.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")));
            writer.println("Diagnóstico: ");
            writer.println("Prescrição: ");
            writer.println();
            writer.println();
            writer.println();
            writer.println();
            writer.println("Assinatura do médico: ");
            writer.println();
            writer.print("==========================================");
        } catch (IOException e) {
            System.out.println("Erro:" + e.getMessage());
        }
    }
}
