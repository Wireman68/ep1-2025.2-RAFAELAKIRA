package main.registro;

import main.core.ConsultaException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Medico implements Entidade
{
    private static final DateTimeFormatter FORMATO_TEMPO = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");

    private final String nome;
    private final String crm;
    private List<Especialidade> especialidades;

    private double custoConsulta;
    private Map<LocalDateTime, Boolean> calendarioConsulta;

    public Medico(String nome, String crm, List<Especialidade> especialidades)
    {
        this.nome = nome;
        this.crm = crm;
        this.especialidades = especialidades;
        this.calendarioConsulta = new HashMap<>();
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String getID() {
        return crm;
    }

    public void adicionarEspecialidade(Especialidade especialidade)
    {
        especialidades.add(especialidade);
    }

    @Override
    public String paraDado() {
        String linhaEspecialidade = especialidades.stream().map(Especialidade::name).collect(Collectors.joining(";"));

        String linhaCalendario = calendarioConsulta.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + (entry.getValue() ? "disponivel" : "ocupado"))
                .collect(Collectors.joining(";"));

        return String.join(",", nome, String.valueOf(crm), linhaEspecialidade, linhaCalendario);
    }

    @Override
    public void displayDados() {

    }

    public void adicionarDataConsulta(LocalDateTime data)
    {
        calendarioConsulta.put(data, true);
    }

    public boolean dataOcupada(LocalDateTime data)
    {
        return calendarioConsulta.getOrDefault(data, false);
    }

    public void agendar(LocalDateTime data) throws ConsultaException
    {
        if(calendarioConsulta.containsKey(data) && calendarioConsulta.get(data))
        {
            calendarioConsulta.put(data, false);
        }
        else throw new ConsultaException("Data não está disponível.");
    }

    public double getCustoConsulta() {
        return custoConsulta;
    }

    public void setCustoConsulta(double d) {
        this.custoConsulta = d;
    }

    public static Medico converterDado(String line) {
        String[] partes = line.split(",");

        String[] linhaEspecialidades = partes[2].split(";");
        List<Especialidade> tempEspecialidade = Arrays.stream(linhaEspecialidades).map(String::trim).map(Especialidade::valueOf).toList();
        Medico medico = new Medico(partes[0], partes[1], tempEspecialidade);

        if (partes.length > 3 && !partes[3].isBlank()) {
            for (String s : partes[3].split(";")) {
                String[] subPartes = s.split(":");
                LocalDateTime horario = LocalDateTime.parse(subPartes[0], FORMATO_TEMPO);
                boolean disponivel = subPartes[1].equals("disponivel");
                medico.calendarioConsulta.put(horario, disponivel);
            }
        }

        return medico;
    }

    public static Medico converterID(String id)
    {
        try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(Paths.get("medicos.csv"))))
        {
            reader.readLine();
            String line;
            while((line = reader.readLine()) != null)
            {
                Medico medic = converterDado(line);
                if(medic.getID() == id) return medic;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
