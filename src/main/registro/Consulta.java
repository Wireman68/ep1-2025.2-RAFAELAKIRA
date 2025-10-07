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
    private final Medico medico;
    private final String local;
    private LocalDateTime data;
    private double custo;
    private final double salvo;
    // LEMBRETE: 0 = AGENDADO, 1 = COMPLETA, 2 = CANCELADA;
    private int status;

    public Consulta(Paciente paciente, Medico medico, LocalDateTime data, String local)
    {
        this.paciente = paciente;
        this.medico = medico;
        this.data = data;
        this.local = local;
        this.status = 0;
        this.id = paciente.getID() + medico.getID() + data.format(FORMATO_CSV);
        if(paciente instanceof PacienteEspecial pacienteEspecial)
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

        this.salvo = medico.getCustoConsulta() - custo;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getLocal() {
        return local;
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

    public void mudarData(LocalDateTime data) {
        this.data = data;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String paraDado() {
        return String.join(",", paciente.getID(), medico.getID(), data.format(FORMATO_CSV), String.valueOf(status), local);
    }

    @Override
    public void displayDados() {

    }

    public double getCusto() {
        return custo;
    }

    public void setCusto(double custo) {
        this.custo = custo;
    }

    public double getSalvo() {
        return salvo;
    }

    public static Consulta converterDado(String line) {
        String[] partes = line.split(",");
        return new Consulta(Paciente.converterID(partes[0]), Medico.converterID(partes[1]), LocalDateTime.parse(partes[2], FORMATO_CSV), partes[3]);
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
                if(consulta.getID().equals(id)) return consulta;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}