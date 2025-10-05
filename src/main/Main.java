package main;

import main.bancos.BancoDeDados;
import main.core.RegistroEventos;
import main.registro.Paciente;

import java.util.Scanner;

public class Main
{
    private static boolean EXIT_CHAVE = false;

    public static void main(String[] args)
    {
        BancoDeDados bancoDeDados = new BancoDeDados();
        RegistroEventos.initBancoDeDados(bancoDeDados);
        do {

            Scanner scanner = new Scanner(System.in);

            Paciente pacienteTeste = new Paciente(scanner.nextLine(), scanner.nextLine(), scanner.nextInt());

            bancoDeDados.registrar(pacienteTeste, false);

            System.out.println("dados do paciente:");
            pacienteTeste.displayDados();

            System.out.println("0 para continuar, outro para acabar");
            EXIT_CHAVE = scanner.nextInt() != 0;

        } while(!EXIT_CHAVE);

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            System.out.println("Salvando dados...");
            RegistroEventos.salvarDeferidos(bancoDeDados);
        }));
    }
}