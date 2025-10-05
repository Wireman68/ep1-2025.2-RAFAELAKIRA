package main.bancos;

import main.registro.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class BancoDeDados
{
    private static final String DIRETORIO = "resources/banks";
    private static final String[] NOMEBANCOS = {"pacientes", "medicos", "consultas", "internacoes", "planos"};

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

    public Set<PlanoDeSaude> getPlanoDeSaudes() {
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

    public Paciente getPatient(String cpf)
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

    private <T extends Entidade> void checarRegistro(T entidade, Collection<T> entidades, int i, boolean inicializacao)
    {
        if(entidades.contains(entidade)) throw new IllegalArgumentException("Nao pode ter uma copia de " + entidade.toString() + ".");

        entidades.forEach(e ->
        {
            if(e.getID() == entidade.getID()) throw new IllegalArgumentException("Nao pode ter uma copia de ID de " + entidade.getID() + ".");
        });

        entidades.add(entidade);

        if(!inicializacao) {
            try (FileWriter writer = new FileWriter(DIRETORIO + "/" + NOMEBANCOS[i] + ".csv", true)) {
                writer.write(entidade.paraDado() + "\n");
            } catch (IOException e) {
                System.err.println("Erro tentando salvar " + entidade.getClass().getSimpleName() + " dentro do sistema: " + e.getMessage());
            }
        }
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
