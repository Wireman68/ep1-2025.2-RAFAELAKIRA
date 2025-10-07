package com.akira.hospital.core;

import com.akira.hospital.bancos.BancoDeDados;
import org.jetbrains.annotations.Nullable;
import com.akira.hospital.registro.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RegistroComandos
{
    public static void registrarPaciente(BancoDeDados db, Paciente p)
    {
        db.registrar(p, false);
    }

    public static void registrarMedico(BancoDeDados db, Medico m)
    {
        db.registrar(m, false);
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
        i.getPaciente().setInternado(true);
    }

    public static void registrarPlano(BancoDeDados db, PlanoDeSaude p)
    {
        if(p.nome().equalsIgnoreCase("ESPECIALINTERNACAO"))
        {
            System.err.println("Erro: este nome de plano é exclusivo para o runtime e não pode ser registrado.");
            return;
        }
        db.registrar(p, false);
    }

    public static void adicionarPlano(BancoDeDados db, String pId, String psId)
    {
        Paciente paciente = db.getPacienteId(pId);
        PlanoDeSaude planoDeSaude = db.getPlano(psId);

        if(paciente instanceof PacienteEspecial pacienteEspecial) {
            pacienteEspecial.adicionarPlano(planoDeSaude);
        }
    }

    public static List<Medico> procurarDisponivel(BancoDeDados db, @Nullable LocalDateTime data, @Nullable Especialidade especialidade)
    {
        List<Medico> medicos = db.getMedicos();

        return medicos.stream()
                .filter(m -> especialidade == null || m.getEspecialidades().contains(especialidade))
                .filter(m -> {
                    if (data == null) return true;
                    return !m.getCalendarioConsulta().getOrDefault(data, true);
                })
                .toList();
    }

    public static void agendarConsulta(BancoDeDados db, String pId, String mId, LocalDateTime data, String local)
    {
        if(data.isBefore(LocalDateTime.now()))
        {
            System.err.println("Erro: data está no passado");
            return;
        }

        Paciente paciente = db.getPacienteId(pId);
        Medico medico = db.getMedico(mId);

        Consulta consulta = new Consulta(paciente, medico, data, local);
        registrarConsulta(db, consulta);
    }

    public static boolean finalizarConsulta(BancoDeDados db, String mId) throws ConsultaException
    {
        // TRUE = FINALIZADA, FALSE = CANCELADA
        boolean f = true;
        Medico medico = db.getMedico(mId);
        LocalDateTime presente = LocalDateTime.now();
        Optional<Consulta> tempConsulta = db.getConsultas().stream()
                .filter(c -> c.getMedico().getID().equals(mId))
                .filter(c -> c.getStatus() == 0)
                .findFirst();

        if(tempConsulta.isEmpty())
        {
            throw new ConsultaException("Erro: não foi encontrado consulta");
        }

        Consulta consulta = tempConsulta.get();
        LocalDateTime dataConsulta = consulta.getData();
        if (presente.isBefore(dataConsulta)) {
            consulta.setStatus(2);
            f = false;
        } else {
            consulta.setStatus(1);
            medico.adicionarConsultasConcluidas();
            if(consulta.getPaciente() instanceof PacienteEspecial pacienteEspecial)
            {

            }
        }

        medico.setDisponivel(dataConsulta, true);

        Paciente paciente = consulta.getPaciente();

        paciente.getConsultas().add(consulta);
        return f;
    }

    public static void internarPaciente(BancoDeDados db, String pId, String mId, double custo, int quarto)
    {
        Paciente paciente = db.getPacienteId(pId);
        Medico medico = db.getMedico(mId);

        if(paciente.getInternacoes().stream().anyMatch(Internacao::getStatus) || paciente.estaInternado())
        {
            System.err.println("Erro: paciente já está internado.");
            return;
        }

        //NOTA: DATA DA INTERNACAO SERA NO MESMO TEMPO QUE A FUNCAO FOR EXECUTADA
        LocalDateTime dataInternacao = LocalDateTime.now();

        Internacao internacao = new Internacao(paciente, medico, dataInternacao, quarto, custo);
        registrarInternacao(db, internacao);
    }

    public static void liberarInternacao(BancoDeDados db, String pId)
    {
        Paciente paciente = db.getPacienteId(pId);

        Internacao internacao = paciente.getInternacoes().stream()
                .filter(Internacao::getStatus)
                .findFirst()
                .orElse(null);

        assert internacao != null;
        internacao.setDataDeSaida(LocalDateTime.now());
        internacao.setStatus(false);
        paciente.setInternado(false);
        paciente.getInternacoes().add(internacao);
        if(paciente instanceof PacienteEspecial pacienteEspecial && Duration.between(internacao.getData(), internacao.getDataDeSaida()).toDays() < 7)
        {
            PlanoDeSaude planoDeSaude = db.getPlanoNome("ESPECIALINTERNACAO");
            pacienteEspecial.adicionarPlano(planoDeSaude);
            internacao.setCusto(planoDeSaude);
        }
    }
}
