package main.registro;

import main.bancos.BancoDeDados;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class PacienteEspecial extends Paciente
{
    private Set<PlanoDeSaude> planosDeSaude;

    public PacienteEspecial(String nome, String cpf, LocalDate nascimento) {
        super(nome, cpf, nascimento);
        planosDeSaude = new HashSet<>();
    }

    public void adicionarPlano(PlanoDeSaude plano)
    {
        planosDeSaude.add(plano);
    }

    public void removerPlano(PlanoDeSaude planoDeSaude)
    {
        planosDeSaude.remove(planoDeSaude);
    }

    public Set<PlanoDeSaude> getPlanosDeSaude()
    {
        return planosDeSaude;
    }

    public double getDescontoTotal()
    {
        if(planosDeSaude == null || planosDeSaude.isEmpty()) return 0.0;

        return planosDeSaude.stream().mapToDouble(PlanoDeSaude::desconto).sum() * (this.getIdade() >= 60 ? 1.5 : 1);
    }

    public static void converterPlano(String line, BancoDeDados db)
    {
        String[] partes = line.split(",", 2);
        String pId = partes[0];
        Paciente paciente = db.getPacienteId(pId);

        if(paciente instanceof PacienteEspecial pacienteEspecial) {
            if (partes.length > 1 && !partes[1].isBlank()) {
                for (String s : partes[1].split(";")) {
                    PlanoDeSaude planoDeSaude = db.getPlanoNome(s);
                    if (planoDeSaude != null) pacienteEspecial.adicionarPlano(planoDeSaude);
                }
            }
        }
    }
}
