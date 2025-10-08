package com.akira.hospital.registro;

import com.akira.hospital.core.ConsultaException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Medico implements Entidade
{
    private static final DateTimeFormatter FORMATO_CSV = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");

    private final String nome;
    private final String crm;
    private final List<Especialidade> especialidades;
    private int consultasConcluidas = 0;

    private final double custoConsulta;

    private Map<LocalDateTime, Boolean> calendarioConsulta;

    public Medico(String nome, String crm, Especialidade especialidadePrimaria, double custoConsulta)
    {
        this.nome = nome;
        this.crm = crm;
        this.especialidades = new ArrayList<>();
        this.especialidades.add(especialidadePrimaria);
        this.custoConsulta = custoConsulta;
        this.calendarioConsulta = new HashMap<>();
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String getID() {
        return crm;
    }

    public List<Especialidade> getEspecialidades()
    {
        return especialidades;
    }

    public void adicionarEspecialidade(Especialidade especialidade)
    {
        especialidades.add(especialidade);
    }

    public Map<LocalDateTime, Boolean> getCalendarioConsulta() {
        return calendarioConsulta;
    }

    @Override
    public String paraDado() {
        String linhaEspecialidade = especialidades.stream().map(Especialidade::name).collect(Collectors.joining(";"));

        String linhaCalendario = calendarioConsulta.entrySet().stream()
                .map(entry -> entry.getKey().format(FORMATO_CSV) + ":" + (entry.getValue() ? "disponivel" : "ocupado"))
                .collect(Collectors.joining(";"));

        return String.join(",", String.valueOf(crm), nome, linhaEspecialidade, String.valueOf(custoConsulta), linhaCalendario);
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
        if(calendarioConsulta.containsKey(data))
        {
            if(calendarioConsulta.containsValue(false))
            {
                throw new ConsultaException("Data já está agendada com este médico.");
            }
            if(calendarioConsulta.get(data))
            {
                calendarioConsulta.put(data, false);
            }
        }
        else throw new ConsultaException("Data não está disponível.");
    }

    public void setDisponivel(LocalDateTime data, boolean disponivel)
    {
        calendarioConsulta.put(data, disponivel);
    }

    public double getCustoConsulta() {
        return custoConsulta;
    }

    public static Medico converterDado(String line) {
        String[] partes = line.split(",");

        String[] linhaEspecialidades = partes[2].split(";");
        List<Especialidade> tempEspecialidade = Arrays.stream(linhaEspecialidades).map(String::trim).map(Especialidade::valueOf).toList();
        Especialidade especialidade1 = tempEspecialidade.getFirst();
        Medico medico = new Medico(partes[1], partes[0], especialidade1, Double.parseDouble(partes[3]));
        tempEspecialidade.stream().skip(1).toList().forEach(medico::adicionarEspecialidade);

        if (partes.length > 4 && !partes[4].isBlank()) {
            for (String s : partes[4].split(";")) {
                String[] subPartes = s.split(":");
                LocalDateTime horario = LocalDateTime.parse(subPartes[0], FORMATO_CSV);
                boolean disponivel = subPartes[1].equals("disponivel");
                medico.calendarioConsulta.put(horario, disponivel);
            }
        }

        return medico;
    }

    public static Medico converterID(String id)
    {
        File f = new File("resources/banks");
        if(!f.exists()) f.mkdirs();

        try (BufferedReader reader = new BufferedReader(new FileReader(new File(f, "medicos.csv"))))
        {
            reader.readLine();
            String line;
            while((line = reader.readLine()) != null)
            {
                Medico medic = converterDado(line);
                if(medic.getID().equals(id)) return medic;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public int getConsultasConcluidas() {
        return consultasConcluidas;
    }

    public void adicionarConsultasConcluidas() {
        this.consultasConcluidas = consultasConcluidas + 1;
    }
}
