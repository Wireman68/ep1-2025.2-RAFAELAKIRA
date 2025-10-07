package com.akira.hospital.core;

import com.akira.hospital.bancos.BancoDeDados;
import org.jetbrains.annotations.Nullable;
import com.akira.hospital.registro.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistroRelatorio
{
    private static final String DIRETORIO = "resources/reports";

    public static void escreverPaciente(BancoDeDados db)
    {
        File file = new File(DIRETORIO, "pacientes.txt");
        if(!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file)))
            {
                writer.println("=============+++Pacientes+++=============");
                writer.println();
                writer.println();

                for(Paciente paciente : db.getPacientes())
                {
                    writer.println("CPF: " + paciente.getID());
                    writer.println("Nome: " + paciente.getNome());
                    writer.println("Idade: " + paciente.getIdade());
                    writer.println("Histórico de consultas:");
                    writer.println();
                    for(Consulta consulta : paciente.getConsultas())
                    {
                        writer.println("Médico: Dr. " + consulta.getMedico().getNome());
                        writer.println("Data: " + consulta.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyyHH:mm")));
                        writer.println("Custo: " + consulta.getCusto());
                        writer.println("Local: " + consulta.getLocal());
                        writer.println("Status: " + switch (consulta.getStatus())
                        {
                            case 0 -> "Agendada";
                            case 1 -> "Finalizada";
                            case 2 -> "Cancelada";
                            default -> throw new IllegalStateException("Valor inesperado: " + consulta.getStatus());
                        });
                        writer.println();
                    }
                    writer.println();
                    writer.println();
                    writer.println("Histórico de internações:");
                    writer.println();
                    for(Internacao internacao : paciente.getInternacoes())
                    {
                        writer.println("Médico: Dr. " + internacao.getMedico().getNome());
                        writer.println("Data de internação: " + internacao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyyHH:mm")));
                        writer.println("Data de saída: " + internacao.getDataDeSaida().format(DateTimeFormatter.ofPattern("dd/MM/yyyyHH:mm")));
                        writer.println("Custo: " + internacao.getCusto());
                        writer.println("Quarto: " + internacao.getQuarto());
                        writer.println("Status: " + (internacao.getStatus() ? "Em Andamento" : "Finalizada / Cancelada"));
                        writer.println();
                    }
                    writer.println();
                    writer.println();
                    writer.println();
                    writer.println();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void escreverMedico(BancoDeDados db)
    {
        File file = new File(DIRETORIO, "medicos.txt");
        if(!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file)))
            {
                writer.println("=============+++Médicos+++=============");
                writer.println();
                writer.println();

                for(Medico medico : db.getMedicos())
                {
                    writer.println("CRM: " + medico.getID());
                    writer.println("Nome: Dr. " + medico.getNome());
                    writer.println("Custo geral de consulta: " + medico.getCustoConsulta());
                    writer.println("Especialidades: ");
                    writer.println();

                    for(Especialidade especialidade : medico.getEspecialidades())
                    {
                        writer.println(especialidade.name());
                    }
                    writer.println();
                    writer.println("Consultas concluídas:" + medico.getConsultasConcluidas());
                    writer.println("Agenda: ");
                    writer.println();
                    medico.getCalendarioConsulta().forEach((data, disponivel) ->
                            writer.println(data.format(DateTimeFormatter.ofPattern("dd/MM/yyyyHH:mm")) + " - " + (disponivel ? "Disponível" : "Ocupado")));
                    writer.println();
                    writer.println();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void escreverConsultas(BancoDeDados db, @Nullable String pId, @Nullable String mId, @Nullable String esp)
    {
        Paciente pacienteF = pId != null ? db.getPacienteId(pId) : null;
        Medico medicoF = mId != null ? db.getMedico(mId) : null;
        Especialidade especialidadeF = esp != null ? Especialidade.valueOf(esp) : null;

        File file = new File(DIRETORIO, "consultas.txt");
        if(!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file)))
            {
                writer.println("=============+++Consultas+++=============");
                writer.println();
                writer.println("Consultas futuras:");
                writer.println();
                writer.println();

                for(Consulta consulta : db.getConsultas().stream().filter(consulta -> consulta.getData().isAfter(LocalDateTime.now())).toList())
                {
                    if (pacienteF != null && !consulta.getPaciente().getID().equals(pacienteF.getID())) continue;
                    if (medicoF != null && !consulta.getMedico().getID().equals(medicoF.getID())) continue;
                    if (especialidadeF != null && !consulta.getMedico().getEspecialidades().contains(especialidadeF)) continue;

                    writer.println("Paciente: " + consulta.getPaciente().getNome());
                    writer.println("Médico: Dr. " + consulta.getMedico().getNome());
                    writer.println("Data: " + consulta.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyyHH:mm")));
                    writer.println("Custo: " + consulta.getCusto());
                    writer.println("Local: " + consulta.getLocal());
                    writer.println("Status: " + switch (consulta.getStatus())
                    {
                        case 0 -> "Agendada";
                        case 1 -> "Finalizada";
                        case 2 -> "Cancelada";
                        default -> throw new IllegalStateException("Valor inesperado: " + consulta.getStatus());
                    });
                    writer.println();
                }
                writer.println();
                writer.println();
                writer.println("Consultas passadas:");
                writer.println();
                writer.println();

                for(Consulta consulta : db.getConsultas().stream()
                        .filter(consulta -> consulta.getData().isBefore(LocalDateTime.now()) || consulta.getData().isEqual(LocalDateTime.now()))
                        .toList())
                {
                    if (pacienteF != null && !consulta.getPaciente().getID().equals(pacienteF.getID())) continue;
                    if (medicoF != null && !consulta.getMedico().getID().equals(medicoF.getID())) continue;
                    if (especialidadeF != null && !consulta.getMedico().getEspecialidades().contains(especialidadeF)) continue;

                    writer.println("Paciente: " + consulta.getPaciente().getNome());
                    writer.println("Médico: Dr. " + consulta.getMedico().getNome());
                    writer.println("Data: " + consulta.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyyHH:mm")));
                    writer.println("Custo: " + consulta.getCusto());
                    writer.println("Local: " + consulta.getLocal());
                    writer.println("Status: " + switch (consulta.getStatus())
                    {
                        case 0 -> "Agendada";
                        case 1 -> "Finalizada";
                        case 2 -> "Cancelada";
                        default -> throw new IllegalStateException("Valor inesperado: " + consulta.getStatus());
                    });
                    writer.println();
                }
                writer.println();
                writer.println();
                writer.println();
                writer.println();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void escreverInternados(BancoDeDados db)
    {
        File file = new File(DIRETORIO, "internados.txt");
        if(!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file)))
            {
                writer.println("=============+++Pacientes Internados+++=============");
                writer.println();
                writer.println();

                for(Internacao internacao : db.getInternacoes())
                {
                    writer.println("Paciente: " + internacao.getPaciente().getNome());
                    writer.println("Médico responsável: " + internacao.getMedico().getNome());
                    writer.println("Tempo de internação: "
                            + Duration.between(internacao.getData(), LocalDateTime.now()).toDays() + " Dias "
                            + Duration.between(internacao.getData(), LocalDateTime.now()).toHours() + " Horas "
                            + Duration.between(internacao.getData(), LocalDateTime.now()).toMinutes() + " Minutos"
                    );
                    writer.println();
                    writer.println();
                }
                writer.println();
                writer.println();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void escreverEstatisticas(BancoDeDados db) {
        File file = new File(DIRETORIO, "planos.txt");
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("=============+++Estatísticas+++=============");
                writer.println();
                writer.println();
                writer.println("Idade média de pacientes: " + db.idadeMediaPacientes());
                writer.println("Porcentagem de pacientes especiais: " + db.porcentagemEspeciais() + "%");
                writer.println("Quantidade total de pacientes: " + db.totalPacientes());
                writer.println("Quantidade total de médicos: " + db.totalMedicos());
                writer.println("Quantidade de consultas agendadas: " + db.consultasAgendadas());
                writer.println("Quantidade de pacientes internados: " + db.totalInternados());
                writer.println("Quantidade total de planos: " + db.totalPlanos());
                writer.println("Médico com mais atendimentos: " + db.maisAtendimentos().getNome());
                writer.println("Especialidade com mais atendimentos: " + db.maisAtendimentosEspecialidade().name());
                writer.println("Especialidade com mais médicos: " + db.maisMedicosEspecialidade().name());
                writer.println("Paciente com mais tempo internado: " + db.maiorTempoInternado().getNome());
                writer.println("Plano de Saúde mais cadastrado: " + db.planoMaisCadastrado().nome() + "  Nível " + db.planoMaisCadastrado().plano());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void escreverPlanos(BancoDeDados db)
    {
        File file = new File(DIRETORIO, "planos.txt");
        if(!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file)))
            {
                writer.println("=============+++Pacientes em um Plano+++=============");
                writer.println();
                writer.println();

                for(PlanoDeSaude planoDeSaude : db.getPlanos().stream().filter(planoDeSaude -> !planoDeSaude.nome().equals("ESPECIALINTERNACAO")).toList())
                {
                    writer.println("Nome: " + planoDeSaude.nome());
                    writer.println("Plano: " + switch (planoDeSaude.plano())
                    {
                        case 0 -> "Bronze";
                        case 1 -> "Prata";
                        case 2 -> "Gold";
                        case 3 -> "Platinum";
                        default -> throw new IllegalStateException("Valor inesperado: " + planoDeSaude.plano());
                    });
                    writer.println("Pacientes com esse plano: ");
                    writer.println();
                    for(Paciente paciente : db.getPacientes().stream()
                            .filter(paciente -> paciente instanceof PacienteEspecial)
                            .map(paciente -> (PacienteEspecial) paciente)
                            .filter(paciente -> paciente.getPlanosDeSaude().contains(planoDeSaude))
                            .toList())
                    {
                        writer.println(paciente.getNome());
                    }
                    writer.println();
                    writer.println();
                }
                writer.println();
                writer.println("Quanto os planos já salvaram em consultas: R$" + db.getSalvoConsultas());
                writer.println("Quanto os planos já salvaram em internações: R$" + db.getSalvoInternacoes());
                writer.println();
                writer.println();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
