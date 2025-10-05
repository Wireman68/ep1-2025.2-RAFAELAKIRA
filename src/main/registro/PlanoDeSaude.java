package main.registro;

import java.time.YearMonth;

public record PlanoDeSaude(String nome, int plano, YearMonth dataDeValidade, double desconto) implements Entidade
{
    @Override
    public String getID() {
        return nome + plano + dataDeValidade.getYear() + dataDeValidade.getMonthValue();
    }

    @Override
    public String paraDado() {
        return String.join(",", nome, String.valueOf(plano), dataDeValidade.toString(), String.valueOf(desconto));
    }

    public static PlanoDeSaude converterDado(String line)
    {
        String[] partes = line.split(",");
        return new PlanoDeSaude(partes[0], Integer.parseInt(partes[1]), YearMonth.parse(partes[2]), Double.parseDouble(partes[3]));
    }
    @Override
    public void displayDados() {

    }
}
