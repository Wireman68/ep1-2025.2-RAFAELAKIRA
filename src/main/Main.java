package main;

import main.bancos.BancoDeDados;
import main.core.RegistroEventos;
import main.registro.Paciente;

import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        BancoDeDados bancoDeDados = new BancoDeDados();
        RegistroEventos.initBancoDeDados(bancoDeDados);

        Scanner scanner = new Scanner(System.in);

        Paciente pacienteTeste = new Paciente(scanner.nextLine(), scanner.nextLine(), scanner.nextInt());

        bancoDeDados.registrar(pacienteTeste, false);

        System.out.println("dados do paciente:");
        pacienteTeste.displayDados();
    }
}