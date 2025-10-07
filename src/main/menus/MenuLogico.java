package main.menus;

import main.bancos.BancoDeDados;
import main.core.RegistroComandos;
import main.core.RegistroEventos;
import main.core.RegistroRelatorio;
import main.registro.*;

import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static main.menus.MenuHospital.imp;

public class MenuLogico
{
    private static final DateTimeFormatter FORMATO_PRINT = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");

    public static void menu(BancoDeDados db)
    {
        boolean menu1 = true;

        while(menu1) {
            MenuHospital.abertura();
            Scanner cs = new Scanner(System.in);
            int i1 = cs.nextInt();
            cs.nextLine();
            switch (i1) {
                case 1 -> {
                    boolean menup = true;
                    while(menup) {
                        MenuHospital.menuPaciente();
                        int i = cs.nextInt();
                        cs.nextLine();
                        switch (i) {
                            case 1 -> {
                                imp("Digite o nome completo do paciente.");
                                String nome = cs.nextLine();
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = cs.nextLine();
                                imp("Digite a data de nascimento do paciente. (DD/MM/AAAA)");
                                String dataS = cs.nextLine();
                                LocalDate data = LocalDate.parse(dataS, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                                Paciente paciente = new Paciente(nome, cpf, data);
                                RegistroComandos.registrarPaciente(db, paciente);
                                imp("Paciente cadastrado!");
                            }

                            case 2 -> {
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = cs.nextLine();
                                Paciente paciente = db.getPacienteId(cpf);

                                if (!(paciente instanceof PacienteEspecial)) {
                                    PacienteEspecial pacienteEspecial = new PacienteEspecial(paciente.getNome(), paciente.getID(), paciente.getNascimento());
                                    pacienteEspecial.setConsultas(new ArrayList<>(paciente.getConsultas()));
                                    pacienteEspecial.setInternacoes(new ArrayList<>(paciente.getInternacoes()));

                                    db.remover(paciente);
                                    db.registrar(pacienteEspecial, false);
                                    imp("Plano do paciente aprimorado!");
                                }
                            }

                            case 0 ->
                            {
                                menup = false;
                            }

                            default -> System.err.println("Caracter inválido.");
                        }
                    }
                }
                case 2 -> {
                    boolean menum = true;
                    while(menum) {
                        MenuHospital.menuMedico();
                        int i = cs.nextInt();
                        cs.nextLine();
                        switch (i) {
                            case 1 -> {
                                imp("Digite o nome completo do médico.");
                                String nome = cs.nextLine();
                                imp("Digite o crm do médico.");
                                String crm = cs.nextLine();
                                imp("Digite a especialidade principal do médico. Outras especialidades poderão ser registradas depois.");
                                String esp = cs.nextLine();
                                imp("Digite o custo de consulta desse médico, em R$.");
                                double custo = cs.nextDouble();

                                Especialidade especialidade = Especialidade.valueOf(esp.toUpperCase());

                                Medico medico = new Medico(nome, crm, especialidade, custo);
                                RegistroComandos.registrarMedico(db, medico);
                                imp("Médico cadastrado!");
                            }

                            case 2 -> {
                                imp("Digite o crm do médico.");
                                String crm = cs.nextLine();
                                Medico medico = db.getMedico(crm);

                                imp("Digite a especialidade deseja a adicionar.");
                                String esp = cs.nextLine();

                                Especialidade especialidade = Especialidade.valueOf(esp.toUpperCase());
                                medico.adicionarEspecialidade(especialidade);
                                imp("Especialidade adicionada!");
                            }

                            case 3 -> {
                                imp("Digite o crm do médico.");
                                String crm = cs.nextLine();
                                Medico medico = db.getMedico(crm);

                                imp("Digite a data da agenda. (DD/MM/AAAA)");
                                String data = cs.nextLine();

                                imp("Digite o horário da agenda. (HH:MM)");
                                String hora = cs.nextLine();

                                LocalDateTime agenda = LocalDateTime.parse(data + hora, DateTimeFormatter.ofPattern("dd/MM/yyyyHH:mm"));

                                medico.adicionarDataConsulta(agenda);

                                imp("Data adicionada!");
                            }

                            case 0 ->
                            {
                                menum = false;
                            }

                            default -> System.err.println("Caracter inválido.");
                        }
                    }
                }
                case 3 -> {
                    boolean menuc = true;
                    while (menuc) {
                        MenuHospital.menuConsulta();
                        int i = cs.nextInt();
                        cs.nextLine();
                        switch (i) {
                            case 1 -> {
                                PlanoDeSaude plano = null;
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = cs.nextLine();
                                imp("Digite o crm do médico.");
                                String crm = cs.nextLine();
                                imp("Digite a data da consulta. (DD/MM/AAAA)");
                                String data = cs.nextLine();

                                imp("Digite o horário da consulta. (HH:MM)");
                                String hora = cs.nextLine();

                                LocalDateTime horario = LocalDateTime.parse(data + hora, DateTimeFormatter.ofPattern("dd/MM/yyyyHH:mm"));

                                imp("Informe o local desejado para a consulta.");
                                String local = cs.nextLine();

                                RegistroComandos.agendarConsulta(db, cpf, crm, horario, local);
                                imp("Consulta agendada!");
                            }

                            case 2 -> {
                                imp("Procurar por:");
                                imp("1 - Nenhum dos critérios");
                                imp("2 - Por horário");
                                imp("3 - Por especialidade");
                                imp("4 - Todos os critérios");
                                int i2 = cs.nextInt();
                                switch(i2)
                                {
                                    case 1 -> {
                                        for(Medico m: RegistroComandos.procurarDisponivel(db, null, null))
                                        {
                                            imp("Dr. " + m.getNome());
                                        }
                                    }
                                    case 2 -> {
                                        imp("Digite a data. (DD/MM/AAAA)");
                                        String data = cs.nextLine();

                                        imp("Digite o horário. (HH:MM)");
                                        String hora = cs.nextLine();

                                        LocalDateTime horario = LocalDateTime.parse(data + hora, DateTimeFormatter.ofPattern("dd/MM/yyyyHH:mm"));

                                        for(Medico m: RegistroComandos.procurarDisponivel(db, horario, null))
                                        {
                                            imp("Dr. " + m.getNome());
                                        }
                                    }
                                    case 3 -> {
                                        imp("Digite a especialidade desejada.");
                                        String esp = cs.nextLine();

                                        Especialidade especialidade = Especialidade.valueOf(esp.toUpperCase());

                                        for(Medico m: RegistroComandos.procurarDisponivel(db, null, especialidade))
                                        {
                                            imp("Dr. " + m.getNome());
                                        }
                                    }
                                    case 4 -> {
                                        imp("Digite a data. (DD/MM/AAAA)");
                                        String data = cs.nextLine();

                                        imp("Digite o horário. (HH:MM)");
                                        String hora = cs.nextLine();

                                        imp("Digite a especialidade desejada.");
                                        String esp = cs.nextLine();

                                        LocalDateTime horario = LocalDateTime.parse(data + hora, DateTimeFormatter.ofPattern("dd/MM/yyyyHH:mm"));

                                        Especialidade especialidade = Especialidade.valueOf(esp.toUpperCase());

                                        for(Medico m: RegistroComandos.procurarDisponivel(db, horario, especialidade))
                                        {
                                            imp("Dr. " + m.getNome());
                                        }
                                    }

                                    default -> System.err.println("Caracter inválido.");
                                }


                            }

                            case 3 -> {
                                imp("Digite o crm do médico responsável.");
                                String crm = cs.nextLine();
                                boolean f = RegistroComandos.finalizarConsulta(db, crm);

                                if(f)
                                {
                                    try {
                                        imp("Consulta finalizada!");
                                        imp("Criando folha para diagnóstico / prescrição...");
                                        RegistroEventos.criarDiagnostico(db, crm);
                                        Thread.sleep(3000);
                                        imp("Folha criada com sucesso!");
                                        imp("Para acessar e editar, vá em: resources/diagnostics/SEU CRM/CPF DO PACIENTE + DATA DA CONSULTA");
                                    } catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                else
                                {
                                    imp("Consulta cancelada!");
                                }
                            }

                            case 0 -> {
                                menuc = false;
                            }

                            default -> System.err.println("Caracter inválido.");
                        }
                    }
                }
                case 4 -> {
                    boolean menui = true;
                    while(menui) {
                        MenuHospital.menuInternacao();
                        int i = cs.nextInt();
                        cs.nextLine();
                        switch (i) {
                            case 1 -> {
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = cs.nextLine();
                                imp("Digite o crm do médico responsável.");
                                String crm = cs.nextLine();
                                imp("Digite o número do quarto.");
                                int quarto = cs.nextInt();
                                imp("Digite o custo da internação.");
                                double custo = cs.nextDouble();

                                RegistroComandos.internarPaciente(db, cpf, crm, custo, quarto);

                                imp("Paciente internado com sucesso!");
                            }

                            case 2 -> {
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = cs.nextLine();
                                RegistroComandos.liberarInternacao(db, cpf);

                                imp("Paciente liberado!");
                            }

                            case 3 -> {
                                imp("Quartos disponíveis:");
                                for(int q : db.getQuartosDisponiveis())
                                {
                                    System.out.println(q);
                                }
                            }

                            case 0 ->
                            {
                                menui = false;
                            }

                            default -> System.err.println("Caracter inválido.");
                        }
                    }
                }
                case 5 -> {
                    boolean menup = true;
                    while(menup) {
                        MenuHospital.menuPlano();
                        int i = cs.nextInt();
                        cs.nextLine();
                        switch(i) {
                            case 1 -> {
                                imp("Digite o nome do plano.");
                                String nome = cs.nextLine();
                                imp("Digite a data de validade do plano. (MM/AAA)");
                                String dataS = cs.nextLine();
                                YearMonth data = YearMonth.parse(dataS, DateTimeFormatter.ofPattern("MM/yyyy"));
                                imp("Informe o desconto, em %, que este plano oferece.");
                                double desconto = cs.nextDouble() / 100;

                                for(int j = 0; j <= 3; j++)
                                {
                                    PlanoDeSaude planoDeSaude = new PlanoDeSaude(nome, j, data, desconto);
                                    db.registrar(planoDeSaude, false);
                                }

                                imp("Planos de saúde, de bronze a platinum, do tipo " + nome + ", foram registrados!");
                            }

                            case 2 -> {
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = cs.nextLine();
                                imp("Digite o ID do plano.");
                                imp("(O ID do plano é NOME + NUMERO DE PLANO + ANO + MES, juntos)");
                                String id = cs.nextLine();

                                PlanoDeSaude planoDeSaude = db.getPlano(id);
                                Paciente paciente = db.getPacienteId(cpf);

                                if(paciente instanceof PacienteEspecial pacienteEspecial)
                                {
                                    pacienteEspecial.adicionarPlano(planoDeSaude);
                                    imp("Paciente está dentro do plano " + planoDeSaude.nome() + "!");
                                }
                                else
                                {
                                    System.err.println("Erro: paciente não pode ter plano de saúde pois não é especial.");
                                }
                            }

                            case 0 ->
                            {
                                menup = false;
                            }

                            default -> System.err.println("Caracter inválido.");
                        }
                    }
                }
                case 6 -> {
                    boolean menur = true;
                    while(menur) {
                        MenuHospital.menuRelatorio();
                        int i = cs.nextInt();
                        cs.nextLine();
                        switch(i)
                        {
                            case 1 -> {
                                RegistroRelatorio.escreverPaciente(db);
                                imp("Relatório impresso!");
                            }
                            case 2 -> {
                                RegistroRelatorio.escreverMedico(db);
                                imp("Relatório impresso!");
                            }
                            case 3 -> {
                                imp("(Para pular o filtro, pressione ENTER.)");
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = cs.nextLine().isEmpty() ? null : cs.nextLine();
                                imp("Digite o crm do médico.");
                                String crm = cs.nextLine().isEmpty() ? null : cs.nextLine();
                                imp("Digite a especialidade desejada.");
                                String esp = cs.nextLine().isEmpty() ? null : cs.nextLine();
                                RegistroRelatorio.escreverConsultas(db, cpf, crm, esp);
                                imp("Relatório impresso!");
                            }
                            case 4 -> {
                                RegistroRelatorio.escreverInternados(db);
                                imp("Relatório impresso!");
                            }
                            case 5 -> {
                                RegistroRelatorio.escreverEstatisticas(db);
                                imp("Relatório impresso!");
                            }
                            case 6 -> {
                                RegistroRelatorio.escreverPlanos(db);
                                imp("Relatório impresso!");
                            }
                            case 0 ->
                            {
                                menur = false;
                            }

                            default -> System.err.println("Caracter inválido.");

                        }
                    }
                }

                case 7 -> {
                    boolean menuo = true;
                    while(menuo)
                    {
                        MenuHospital.menuRelatorio();
                        int i = cs.nextInt();
                        cs.nextLine();
                        switch(i) {
                            case 1 -> {
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = cs.nextLine();

                                Paciente paciente = db.getPacienteId(cpf);

                                db.remover(paciente);
                                imp("Paciente removido.");
                            }
                            case 2 -> {
                                imp("Digite o crm do médico.");
                                String crm = cs.nextLine();

                                Medico medico = db.getMedico(crm);

                                db.remover(medico);
                                imp("Médico removido.");
                            }
                            case 3 -> {
                                imp("Digite o ID do plano.");
                                imp("(O ID do plano é NOME + NUMERO DE PLANO + ANO + MES, juntos)");
                                String id = cs.nextLine();

                                PlanoDeSaude planoDeSaude = db.getPlano(id);

                                db.getPacientes().stream()
                                        .filter(paciente -> paciente instanceof PacienteEspecial)
                                        .map(paciente -> (PacienteEspecial) paciente)
                                        .toList()
                                        .forEach(paciente -> paciente.removerPlano(planoDeSaude));

                                db.remover(planoDeSaude);
                                imp("Plano removido.");
                            }

                            case 0 ->
                            {
                                menuo = false;
                            }

                            default -> System.err.println("Caracter inválido.");
                        }
                    }
                }

                case 0 -> {
                    System.out.println("Salvando dados...");
                    RegistroEventos.salvarDeferidos(db);
                    try
                    {
                        Thread.sleep(1000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    MenuHospital.menuSaida();
                    menu1 = false;
                    System.exit(0);
                }
                default -> System.err.println("Caracter inválido.");
            }
        }
    }
}
