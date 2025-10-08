package com.akira.hospital.registro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Internacao implements Entidade
{
    private static final DateTimeFormatter FORMATO_CSV = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");

    private final Paciente paciente;
    private final String id;
    private final Medico medico;
    private final LocalDateTime data;
    private LocalDateTime dataDeSaida;
    private int quarto;
    private double custo;
    private final double salvo;
    private boolean status;

    public Internacao(Paciente paciente, Medico medico, LocalDateTime dataDeEntrada, int quarto, double custo)
    {
        this.paciente = paciente;
        this.medico = medico;
        this.data = dataDeEntrada;
        this.quarto = quarto;
        if(paciente instanceof PacienteEspecial pacienteEspecial)
        {
            if(custo - (custo * pacienteEspecial.getDescontoTotal()) < 0)
            {
                this.custo = 0;
            }

            else
            {
                this.custo = custo - (custo * pacienteEspecial.getDescontoTotal());
            }
        }
        else
        {
            this.custo = custo;
        }
        this.salvo = custo - this.custo;
        this.status = true;
        this.id = paciente.getID().charAt(0)
                + paciente.getID().charAt(1)
                + paciente.getID().charAt(2)
                + medico.getID().charAt(0)
                + medico.getID().charAt(1)
                + medico.getID().charAt(2)
                + data.format(FORMATO_CSV) + quarto + custo;
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

    public void setQuarto(int quarto) {
        this.quarto = quarto;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public LocalDateTime getData() {
        return data;
    }

    public LocalDateTime getDataDeSaida() {
        return dataDeSaida;
    }

    public double getCusto() {
        return custo;
    }

    public double getSalvo() {
        return salvo;
    }

    public void setDataDeSaida(LocalDateTime dataDeSaida) {
        this.dataDeSaida = dataDeSaida;
    }

    public void setCusto(PlanoDeSaude planoDeSaude)
    {
        this.custo = custo - (custo * planoDeSaude.desconto());
    }

    @Override
    public String getID() {
        return id;
    }
    @Override
    public String paraDado() {
        return String.join(",", id, paciente.getID(), medico.getID(), data.format(FORMATO_CSV), String.valueOf(quarto), String.valueOf(custo), status ? "ANDAMENTO" : "FINALIZADA");
    }

    public static Internacao converterDado(String line)
    {
        String[] partes = line.split(",");
        Internacao internacao = new Internacao(Paciente.converterID(partes[1]), Medico.converterID(partes[2]), LocalDateTime.parse(partes[3], FORMATO_CSV), Integer.parseInt(partes[4]), Double.parseDouble(partes[5]));
        internacao.setStatus(partes[5].equals("ANDAMENTO"));

        return internacao;
    }
}
