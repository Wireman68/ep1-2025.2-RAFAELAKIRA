package main.bancos;

import main.registro.*;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class BancoDeDados
{
    private static final int[] QUARTOS =
            {101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310};

    protected List<Paciente> pacientes = new ArrayList<>();
    protected List<Medico> medicos = new ArrayList<>();
    protected List<Consulta> consultas = new ArrayList<>();
    protected List<Internacao> internacoes = new ArrayList<>();
    protected Set<PlanoDeSaude> planoDeSaudes = new HashSet<>();

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    public List<Medico> getMedicos() {
        return medicos;
    }

    public List<Consulta> getConsultas() {
        return consultas;
    }

    public List<Internacao> getInternacoes() {
        return internacoes;
    }

    public Set<PlanoDeSaude> getPlanos() {
        return planoDeSaudes;
    }

    public Paciente getPaciente(int i)
    {
        return pacientes.get(i);
    }

    public Paciente getPaciente(String nome)
    {
        for(Paciente p : pacientes)
        {
            if(p.getNome().equalsIgnoreCase(nome))
            {
                return p;
            }
        }
        return null;
    }

    public Paciente getPacienteId(String cpf)
    {
        for(Paciente p : pacientes)
        {
            if(p.getID().equals(cpf)) return p;
        }

        return null;
    }

    public double getSalvoConsultas() {
        return getConsultas().stream().mapToDouble(Consulta::getSalvo).sum();
    }

    public double getSalvoInternacoes() {
        return getInternacoes().stream().mapToDouble(Internacao::getSalvo).sum();
    }

    public Medico getMedico(int i)
    {
        return medicos.get(i);
    }

    public Medico getMedico(String crm)
    {
        for(Medico m : medicos)
        {
            if(m.getID().equals(crm)) return m;
        }

        return null;
    }

    public Consulta getConsultaMedico(String crm)
    {
         return consultas.stream()
                .filter(consulta -> consulta.getMedico().getID().equals(crm))
                .toList()
                .getFirst();
    }

    public PlanoDeSaude getPlano(String id)
    {
        for(PlanoDeSaude p : planoDeSaudes)
        {
            if(p.getID().equals(id)) return p;
        }

        return null;
    }

    public PlanoDeSaude getPlanoNome(String nome)
    {
        for(PlanoDeSaude p : planoDeSaudes)
        {
            if(p.nome().equals(nome)) return p;
        }

        return null;
    }



    public int[] getQuartosDisponiveis() {

        Set<Integer> quartosOcupados = this.getInternacoes().stream()
                .filter(Internacao::getStatus)
                .map(Internacao::getQuarto)
                .collect(Collectors.toSet());

        return Arrays.stream(QUARTOS)
                .filter(q -> !quartosOcupados.contains(q))
                .toArray();
    }

    public void registrar(Entidade entidade, boolean inicializacao) throws IllegalArgumentException
    {
        switch(entidade)
        {
            case Paciente p -> checarRegistro(p, pacientes, 0, inicializacao);
            case Medico m -> checarRegistro(m, medicos, 1, inicializacao);
            case Consulta a -> checarRegistro(a, consultas, 2, inicializacao);
            case Internacao i -> checarRegistro(i, internacoes, 3, inicializacao);
            case PlanoDeSaude s -> checarRegistro(s, planoDeSaudes, 4, inicializacao);
            default -> throw new IllegalArgumentException("Entidade invalida.");
        }
    }

    private <T extends Entidade> void checarRegistro(T entidade, Collection<T> entidades, int i, boolean inicializacao) throws IllegalArgumentException
    {
        for(T e : entidades)
        {
            if(e.equals(entidade) || e.getID().equals(entidade.getID())) throw new IllegalArgumentException("Nao pode ter uma copia de " + entidade + ".");
        }

        entidades.add(entidade);
    }

    public void remover(Entidade entidade)
    {
        switch(entidade)
        {
            case Paciente p -> pacientes.remove(p);
            case Medico m -> medicos.remove(m);
            case Consulta a -> consultas.remove(a);
            default -> throw new IllegalArgumentException("Entidade invalida");
        }
    }



    //Estatísticas

    public double idadeMediaPacientes()
    {
        return pacientes.stream().mapToDouble(Paciente::getIdade).sum() / pacientes.size();
    }

    public double porcentagemEspeciais()
    {
        return (double) (pacientes.stream().filter(paciente -> paciente instanceof PacienteEspecial).toList().size() / pacientes.size()) * 100;
    }

    public int totalPacientes()
    {
        return pacientes.size();
    }

    public int totalMedicos()
    {
        return medicos.size();
    }

    public int totalPlanos()
    {
        return planoDeSaudes.size();
    }

    public int totalInternados()
    {
        return internacoes.stream().filter(Internacao::getStatus).toList().size();
    }

    public int consultasAgendadas()
    {
        return consultas.stream().filter(consulta -> consulta.getStatus() == 0).toList().size();
    }

    public Medico maisAtendimentos()
    {
        return consultas.stream()
                .collect(Collectors.groupingBy(Consulta::getMedico, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public Especialidade maisAtendimentosEspecialidade() {
        return consultas.stream()
                .flatMap(consulta -> consulta.getMedico().getEspecialidades().stream())
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public Especialidade maisMedicosEspecialidade() {
        Map<Especialidade, Long> quantidade = medicos.stream()
                .flatMap(medico -> medico.getEspecialidades().stream())
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        return quantidade.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public Paciente maiorTempoInternado() {
        Map<Paciente, Long> duracaoTotal = new HashMap<>();
        for (Internacao internacao : internacoes) {
            if (internacao.getDataDeSaida() != null) {
                long duracao = Duration.between(internacao.getData(), internacao.getDataDeSaida()).toHours();
                duracaoTotal.merge(internacao.getPaciente(), duracao, Long::sum);
            }
        }
        return duracaoTotal.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public PlanoDeSaude planoMaisCadastrado() {

        List<PacienteEspecial> pacienteEspeciais = pacientes.stream()
                .filter(paciente -> paciente instanceof PacienteEspecial)
                .map(paciente -> (PacienteEspecial) paciente)
                .toList();

        Map<PlanoDeSaude, Long> counts = new HashMap<>();
        for (PacienteEspecial pacienteEspecial : pacienteEspeciais) {
            for (PlanoDeSaude plano : pacienteEspecial.getPlanosDeSaude()) {
                counts.merge(plano, 1L, Long::sum);
            }
        }

        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
