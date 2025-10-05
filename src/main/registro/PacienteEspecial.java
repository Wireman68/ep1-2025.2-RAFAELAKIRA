package main.registro;

import java.util.HashSet;
import java.util.Set;

public class PacienteEspecial extends Paciente
{
    private Set<PlanoDeSaude> planosDeSaude;

    public PacienteEspecial(String nome, String cpf, int idade) {
        super(nome, cpf, idade);
        planosDeSaude = new HashSet<>();
    }

    public void addPlano(PlanoDeSaude plano)
    {
        planosDeSaude.add(plano);
    }

    public Set<PlanoDeSaude> getPlanosDeSaude()
    {
        return planosDeSaude;
    }

    public double getDescontoTotal()
    {
        if(planosDeSaude == null || planosDeSaude.isEmpty()) return 0.0;

        return planosDeSaude.stream().mapToDouble(PlanoDeSaude::desconto).sum();
    }
}
