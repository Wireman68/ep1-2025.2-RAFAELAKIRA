package com.akira.hospital.menus;

import com.akira.hospital.bancos.BancoDeDados;
import com.akira.hospital.GUI;
import com.akira.hospital.core.RegistroComandos;
import com.akira.hospital.core.RegistroEventos;
import com.akira.hospital.core.RegistroRelatorio;
import com.akira.hospital.registro.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static com.akira.hospital.menus.MenuHospital.imp;

public class MenuLogico
{
    private static final DateTimeFormatter FORMATO_PRINT = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");

    public static void menu(BancoDeDados db)
    {
        MenuHospital.carregando();
        boolean menu1 = true;

        while(menu1) {
            MenuHospital.abertura();
            int i1 = GUI.nextInt();
            switch (i1) {
                case 1 -> {
                    boolean menup = true;
                    while(menup) {
                        MenuHospital.menuPaciente();
                        int i = GUI.nextInt();
                        switch (i) {
                            case 1 -> {
                                imp("Digite o nome completo do paciente.");
                                String nome = GUI.nextLine();
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = GUI.nextLine();
                                imp("Digite a data de nascimento do paciente. (DD/MM/AAAA)");
                                String dataS = GUI.nextLine();
                                LocalDate data = LocalDate.parse(dataS, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                                Paciente paciente = new Paciente(nome, cpf, data);
                                RegistroComandos.registrarPaciente(db, paciente);
                                imp("Paciente " + paciente.getNome() + " cadastrado!");
                                GUI.nextLine();
                            }

                            case 2 -> {
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = GUI.nextLine();
                                Paciente paciente = db.getPacienteId(cpf);

                                if (!(paciente instanceof PacienteEspecial)) {
                                    PacienteEspecial pacienteEspecial = new PacienteEspecial(paciente.getNome(), paciente.getID(), paciente.getNascimento());
                                    pacienteEspecial.setConsultas(new ArrayList<>(paciente.getConsultas()));
                                    pacienteEspecial.setInternacoes(new ArrayList<>(paciente.getInternacoes()));

                                    db.remover(paciente);
                                    db.registrar(pacienteEspecial, false);
                                    imp("Plano do paciente " + paciente.getNome() + " aprimorado!");
                                }
                                GUI.nextLine();
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
                        int i = GUI.nextInt();
                        switch (i) {
                            case 1 -> {
                                imp("Digite o nome completo do médico.");
                                String nome = GUI.nextLine();
                                imp("Digite o crm do médico.");
                                String crm = GUI.nextLine();
                                imp("Digite a especialidade principal do médico. Outras especialidades poderão ser registradas depois.");
                                String esp = GUI.nextLine();
                                imp("Digite o custo de consulta desse médico, em R$.");
                                double custo = GUI.nextDouble();

                                Especialidade especialidade = Especialidade.valueOf(esp.toUpperCase());

                                Medico medico = new Medico(nome, crm, especialidade, custo);
                                RegistroComandos.registrarMedico(db, medico);
                                imp("Médico " + medico.getNome() + " cadastrado!");
                                GUI.nextLine();
                            }

                            case 2 -> {
                                imp("Digite o crm do médico.");
                                String crm = GUI.nextLine();
                                Medico medico = db.getMedico(crm);

                                imp("Digite a especialidade deseja a adicionar.");
                                String esp = GUI.nextLine();

                                Especialidade especialidade = Especialidade.valueOf(esp.toUpperCase());
                                medico.adicionarEspecialidade(especialidade);
                                imp("Especialidade " + esp + " adicionada!");
                                GUI.nextLine();
                            }

                            case 3 -> {
                                imp("Digite o crm do médico.");
                                String crm = GUI.nextLine();
                                Medico medico = db.getMedico(crm);

                                imp("Digite a data da agenda. (DD/MM/AAAA)");
                                String data = GUI.nextLine();

                                imp("Digite o horário da agenda. (HH:MM)");
                                String hora = GUI.nextLine();

                                LocalDateTime agenda = LocalDateTime.parse(data + hora, DateTimeFormatter.ofPattern("dd/MM/yyyyHH:mm"));

                                medico.adicionarDataConsulta(agenda);

                                imp("Data adicionada!");
                                GUI.nextLine();
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
                        int i = GUI.nextInt();
                        switch (i) {
                            case 1 -> {
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = GUI.nextLine();
                                imp("Digite o crm do médico.");
                                String crm = GUI.nextLine();
                                imp("Digite a data da consulta. (DD/MM/AAAA)");
                                String data = GUI.nextLine();

                                imp("Digite o horário da consulta. (HH:MM)");
                                String hora = GUI.nextLine();

                                LocalDateTime horario = LocalDateTime.parse(data + hora, DateTimeFormatter.ofPattern("dd/MM/yyyyHH:mm"));

                                imp("Informe o local desejado para a consulta.");
                                String local = GUI.nextLine();

                                RegistroComandos.agendarConsulta(db, cpf, crm, horario, local);
                                imp("Consulta agendada com " + db.getMedico(crm).getNome() + " para " + horario.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")) + "!");
                                GUI.nextLine();
                            }

                            case 2 -> {
                                imp("Procurar por:");
                                imp("1 - Nenhum dos critérios");
                                imp("2 - Por horário");
                                imp("3 - Por especialidade");
                                imp("4 - Todos os critérios");
                                int i2 = GUI.nextInt();
                                switch(i2)
                                {
                                    case 1 -> {
                                        for(Medico m: RegistroComandos.procurarDisponivel(db, null, null))
                                        {
                                            imp("Dr. " + m.getNome());
                                        }
                                        GUI.nextLine();
                                    }
                                    case 2 -> {
                                        imp("Digite a data. (DD/MM/AAAA)");
                                        String data = GUI.nextLine();

                                        imp("Digite o horário. (HH:MM)");
                                        String hora = GUI.nextLine();

                                        LocalDateTime horario = LocalDateTime.parse(data + hora, DateTimeFormatter.ofPattern("dd/MM/yyyyHH:mm"));

                                        for(Medico m: RegistroComandos.procurarDisponivel(db, horario, null))
                                        {
                                            imp("Dr. " + m.getNome());
                                        }
                                        GUI.nextLine();
                                    }
                                    case 3 -> {
                                        imp("Digite a especialidade desejada.");
                                        String esp = GUI.nextLine();

                                        Especialidade especialidade = Especialidade.valueOf(esp.toUpperCase());

                                        for(Medico m: RegistroComandos.procurarDisponivel(db, null, especialidade))
                                        {
                                            imp("Dr. " + m.getNome());
                                        }
                                        GUI.nextLine();
                                    }
                                    case 4 -> {
                                        imp("Digite a data. (DD/MM/AAAA)");
                                        String data = GUI.nextLine();

                                        imp("Digite o horário. (HH:MM)");
                                        String hora = GUI.nextLine();

                                        imp("Digite a especialidade desejada.");
                                        String esp = GUI.nextLine();

                                        LocalDateTime horario = LocalDateTime.parse(data + hora, DateTimeFormatter.ofPattern("dd/MM/yyyyHH:mm"));

                                        Especialidade especialidade = Especialidade.valueOf(esp.toUpperCase());

                                        for(Medico m: RegistroComandos.procurarDisponivel(db, horario, especialidade))
                                        {
                                            imp("Dr. " + m.getNome());
                                        }
                                        GUI.nextLine();
                                    }

                                    default -> System.err.println("Caracter inválido.");
                                }


                            }

                            case 3 -> {
                                imp("Digite o crm do médico responsável.");
                                String crm = GUI.nextLine();
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
                                GUI.nextLine();
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
                        int i = GUI.nextInt();
                        switch (i) {
                            case 1 -> {
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = GUI.nextLine();
                                imp("Digite o crm do médico responsável.");
                                String crm = GUI.nextLine();
                                imp("Digite o número do quarto.");
                                int quarto = GUI.nextInt();
                                imp("Digite o custo da internação.");
                                double custo = GUI.nextDouble();

                                RegistroComandos.internarPaciente(db, cpf, crm, custo, quarto);

                                imp("Paciente " + db.getPacienteId(cpf).getNome() + " internado com sucesso!");
                                GUI.nextLine();
                            }

                            case 2 -> {
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = GUI.nextLine();
                                RegistroComandos.liberarInternacao(db, cpf);

                                imp("Paciente " + db.getPacienteId(cpf).getNome() + " liberado!");
                                GUI.nextLine();
                            }

                            case 3 -> {
                                imp("Quartos disponíveis:");
                                for(int q : db.getQuartosDisponiveis())
                                {
                                    System.out.println(q);
                                }
                                GUI.nextLine();
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
                        int i = GUI.nextInt();
                        switch(i) {
                            case 1 -> {
                                imp("Digite o nome do plano.");
                                String nome = GUI.nextLine();
                                imp("Digite a data de validade do plano. (MM/AAA)");
                                String dataS = GUI.nextLine();
                                YearMonth data = YearMonth.parse(dataS, DateTimeFormatter.ofPattern("MM/yyyy"));
                                imp("Informe o desconto, em %, que este plano oferece.");
                                double desconto = GUI.nextDouble() / 100;

                                for(int j = 0; j <= 3; j++)
                                {
                                    PlanoDeSaude planoDeSaude = new PlanoDeSaude(nome, j, data, desconto);
                                    db.registrar(planoDeSaude, false);
                                }

                                imp("Planos de saúde, de bronze a platinum, do tipo " + nome + ", foram registrados!");
                                GUI.nextLine();
                            }

                            case 2 -> {
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = GUI.nextLine();
                                imp("Digite o ID do plano.");
                                imp("(O ID do plano é NOME + NUMERO DE PLANO + ANO + MES, juntos)");
                                String id = GUI.nextLine();

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
                                GUI.nextLine();
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
                        int i = GUI.nextInt();
                        switch(i)
                        {
                            case 1 -> {
                                RegistroRelatorio.escreverPaciente(db);
                                imp("Relatório impresso!");
                                GUI.nextLine();
                            }
                            case 2 -> {
                                RegistroRelatorio.escreverMedico(db);
                                imp("Relatório impresso!");
                                GUI.nextLine();
                            }
                            case 3 -> {
                                imp("(Para pular o filtro, pressione ENTER.)");
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = GUI.nextLine().isEmpty() ? null : GUI.nextLine();
                                imp("Digite o crm do médico.");
                                String crm = GUI.nextLine().isEmpty() ? null : GUI.nextLine();
                                imp("Digite a especialidade desejada.");
                                String esp = GUI.nextLine().isEmpty() ? null : GUI.nextLine();
                                RegistroRelatorio.escreverConsultas(db, cpf, crm, esp);
                                imp("Relatório impresso!");
                                GUI.nextLine();
                            }
                            case 4 -> {
                                RegistroRelatorio.escreverInternados(db);
                                imp("Relatório impresso!");
                                GUI.nextLine();
                            }
                            case 5 -> {
                                RegistroRelatorio.escreverEstatisticas(db);
                                imp("Relatório impresso!");
                                GUI.nextLine();
                            }
                            case 6 -> {
                                RegistroRelatorio.escreverPlanos(db);
                                imp("Relatório impresso!");
                                GUI.nextLine();
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
                        MenuHospital.menuOutros();
                        int i = GUI.nextInt();
                        switch(i) {
                            case 1 -> {
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = GUI.nextLine();

                                Paciente paciente = db.getPacienteId(cpf);

                                db.remover(paciente);
                                imp("Paciente removido.");
                                GUI.nextLine();
                            }
                            case 2 -> {
                                imp("Digite o crm do médico.");
                                String crm = GUI.nextLine();

                                Medico medico = db.getMedico(crm);

                                db.remover(medico);
                                imp("Médico removido.");
                                GUI.nextLine();
                            }
                            case 3 -> {
                                imp("Digite o ID do plano.");
                                imp("(O ID do plano é NOME + NUMERO DE PLANO + ANO + MES, juntos)");
                                String id = GUI.nextLine();

                                PlanoDeSaude planoDeSaude = db.getPlano(id);

                                db.getPacientes().stream()
                                        .filter(paciente -> paciente instanceof PacienteEspecial)
                                        .map(paciente -> (PacienteEspecial) paciente)
                                        .toList()
                                        .forEach(paciente -> paciente.removerPlano(planoDeSaude));

                                db.remover(planoDeSaude);
                                imp("Plano removido.");
                                GUI.nextLine();
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
