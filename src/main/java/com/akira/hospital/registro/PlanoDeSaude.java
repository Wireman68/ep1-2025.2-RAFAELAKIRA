package com.akira.hospital.registro;

import java.time.YearMonth;

public record PlanoDeSaude(String nome, int plano, YearMonth dataDeValidade, double desconto) implements Entidade
{
    @Override
    public String getID() {
        return nome + plano + dataDeValidade.getYear() + dataDeValidade.getMonthValue();
    }

    @Override
    public String nome() {
        return nome;
    }

    @Override
    public String paraDado() {
        return String.join(",", getID(), nome, String.valueOf(plano), dataDeValidade.toString(), String.valueOf(desconto));
    }

    public static PlanoDeSaude converterDado(String line)
    {
        String[] partes = line.split(",");
        return new PlanoDeSaude(partes[1], Integer.parseInt(partes[2]), YearMonth.parse(partes[3]), Double.parseDouble(partes[4]));
    }
}
