package com.akira.hospital.registro;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.YearMonth;

//Lembrete: int plano: 0 - bronze, 1 - prata, 2 - gold, 3 - platinum
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

    public static PlanoDeSaude converterID(String id)
    {
        try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(Paths.get("planos.csv"))))
        {
            reader.readLine();
            String line;
            while((line = reader.readLine()) != null)
            {
                PlanoDeSaude plano = converterDado(line);
                if(plano.getID().equals(id)) return plano;
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
