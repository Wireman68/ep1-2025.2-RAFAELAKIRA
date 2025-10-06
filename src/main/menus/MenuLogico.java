package main.menus;

import main.bancos.BancoDeDados;
import main.core.RegistroComandos;
import main.core.RegistroEventos;
import main.registro.Especialidade;
import main.registro.Medico;
import main.registro.Paciente;
import main.registro.PacienteEspecial;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static main.menus.MenuHospital.imp;

public class MenuLogico
{
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
                                menup = false;
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
                                menup = false;
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

                                Especialidade especialidade = Especialidade.valueOf(esp.toUpperCase());

                                List<Especialidade> l1 = new ArrayList<>();
                                l1.add(especialidade);

                                Medico medico = new Medico(nome, crm, l1);
                                RegistroComandos.registrarMedico(db, medico);
                                imp("Médico cadastrado!");
                                menum = false;
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
                                menum = false;
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
                                menum = false;
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
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = cs.nextLine();
                                imp("Digite o crm do médico.");
                                String crm = cs.nextLine();
                                imp("Digite a data da consulta. (DD/MM/AAAA)");
                                String data = cs.nextLine();

                                imp("Digite o horário da consulta. (HH:MM)");
                                String hora = cs.nextLine();

                                LocalDateTime horario = LocalDateTime.parse(data + hora, DateTimeFormatter.ofPattern("dd/MM/yyyyHH:mm"));

                                RegistroComandos.agendarConsulta(db, cpf, crm, horario);
                                imp("Consulta agendada!");
                                menuc = false;
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
                                imp("Digite o crm do médico.");
                                String crm = cs.nextLine();
                                RegistroComandos.finalizarConsulta(db, crm);

                                imp("Consulta finalizada!");
                                menuc = false;
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
                                menui = false;
                            }

                            case 2 -> {
                                imp("Digite o cpf do paciente. (XXX.XXX.XXX-XX)");
                                String cpf = cs.nextLine();
                                RegistroComandos.liberarInternacao(db, cpf);

                                imp("Paciente liberado!");
                                menui = false;
                            }

                            case 3 -> {
                                imp("Quartos disponíveis:");
                                for(int q : db.getQuartosDisponiveis())
                                {
                                    System.out.println(q);
                                }
                                menui = false;
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
                    MenuHospital.menuPlano();
                }
                case 6 -> {
                    MenuHospital.menuRelatorio();
                }
                case 0 -> {
                    MenuHospital.menuSaida();
                    menu1 = false;
                    System.exit(0);
                    Runtime.getRuntime().addShutdownHook(new Thread(() ->
                    {
                        System.out.println("Salvando dados...");
                        RegistroEventos.salvarDeferidos(db);
                    }));
                }
                default -> System.err.println("Caracter inválido.");
            }
        }
    }
}
