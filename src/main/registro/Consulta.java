package main.registro;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Consulta implements Entidade
{
    private static final DateTimeFormatter FORMATO_CSV = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");

    private final Paciente paciente;
    private final String id;
    private Medico medico;
    private LocalDateTime data;
    private double custo;
    // LEMBRETE: 0 = AGENDADO, 1 = COMPLETA, 2 = CANCELADA;
    private int status;

    public Consulta(Paciente paciente, Medico medico, LocalDateTime data)
    {
        this.paciente = paciente;
        this.medico = medico;
        this.data = data;
        this.status = 0;
        this.id =  paciente.getID() + medico.getID() + data.format(FORMATO_CSV);
        if(paciente instanceof  PacienteEspecial pacienteEspecial)
        {
            if(medico.getCustoConsulta() - (medico.getCustoConsulta() * pacienteEspecial.getDescontoTotal()) < 0)
            {
                this.custo = 0;
            }

            else
            {
                this.custo = medico.getCustoConsulta() - (medico.getCustoConsulta() * pacienteEspecial.getDescontoTotal());
            }
        }
        else
        {
            this.custo = medico.getCustoConsulta();
        }
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Medico getMedico()
    {
        return medico;
    }

    public LocalDateTime getData()
    {
        return data;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String paraDado() {
        return String.join(",", paciente.getNome(), medico.getNome(), data.format(FORMATO_CSV));
    }

    @Override
    public void displayDados() {

    }

    public static Consulta converterDado(String line) {
        String[] partes = line.split(",");
        return new Consulta(Paciente.converterID(partes[0]), Medico.converterID(partes[1]), LocalDateTime.parse(partes[2], FORMATO_CSV));
    }

    public static Consulta converterID(String id)
    {
        try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(Paths.get("consultas.csv"))))
        {
            reader.readLine();
            String line;
            while((line = reader.readLine()) != null)
            {
                Consulta consulta = converterDado(line);
                if(consulta.getID() == id) return consulta;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}