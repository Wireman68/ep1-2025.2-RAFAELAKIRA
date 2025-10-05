package main.registro;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Internacao implements Entidade
{
    private static final DateTimeFormatter FORMATO_TEMPO = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");

    private final Paciente paciente;
    private final String id;
    private Medico medico;
    private LocalDateTime data;
    private LocalDateTime dataDeSaida;
    private int quarto;
    private double custo;
    private boolean status;

    public Internacao(Paciente paciente, Medico medico, LocalDateTime dataDeEntrada, int quarto, double custo)
    {
        this.paciente = paciente;
        this.medico = medico;
        this.data = dataDeEntrada;
        this.quarto = quarto;
        this.custo = custo;
        this.status = true;
        this.id = paciente.getID().charAt(0)
                + paciente.getID().charAt(1)
                + paciente.getID().charAt(2)
                + medico.getID().charAt(0)
                + medico.getID().charAt(1)
                + medico.getID().charAt(2)
                + data.toString() + quarto + custo;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public int getQuarto() {
        return quarto;
    }

    public boolean getStatus() {
        return status;
    }

    public LocalDateTime getDataDeSaida() {
        return dataDeSaida;
    }

    public void setDataDeSaida(LocalDateTime dataDeSaida) {
        this.dataDeSaida = dataDeSaida;
    }

    @Override
    public String getID() {
        return id;
    }
    @Override
    public String paraDado() {
        return String.join(",", id, paciente.getID(), medico.getID(), data.format(FORMATO_TEMPO), String.valueOf(quarto), String.valueOf(custo));
    }

    public static Internacao converterDado(String line)
    {
        String[] partes = line.split(",");
        return new Internacao(Paciente.converterID(partes[0]), Medico.converterID(partes[1]), LocalDateTime.parse(partes[2], FORMATO_TEMPO), Integer.parseInt(partes[3]), Double.parseDouble(partes[4]));
    }

    public static Internacao converterID(String id)
    {
        try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(Paths.get("internacoes.csv"))))
        {
            reader.readLine();
            String line;
            while((line = reader.readLine()) != null)
            {
                Internacao internacao = converterDado(line);
                if(internacao.getID() == id) return internacao;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void displayDados() {

    }


}
