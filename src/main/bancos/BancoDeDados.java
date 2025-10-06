package main.bancos;

import main.registro.*;

import java.util.*;
import java.util.stream.Collectors;

public class BancoDeDados
{
    private static final String DIRETORIO = "resources/banks";
    private static final String[] NOMEBANCOS = {"pacientes", "medicos", "consultas", "internacoes", "planos"};
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
            if(p.getID() == cpf) return p;
        }

        return null;
    }

    public Medico getMedico(int i)
    {
        return medicos.get(i);
    }

    public Medico getMedico(String crm)
    {
        for(Medico m : medicos)
        {
            if(m.getID() == crm) return m;
        }

        return null;
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
}
