package main;

import main.bancos.BancoDeDados;
import main.core.RegistroEventos;

import static main.menus.MenuLogico.menu;

public class Hospital
{
    public static BancoDeDados db = new BancoDeDados();

    public static void main(String[] args)
    {
        RegistroEventos.initBancoDeDados(db);
        RegistroEventos.registrarPlanoEspecial(db);
        RegistroEventos.criarPastasConsulta(db);
        RegistroEventos.criarPastasRelatorios(db);
        menu(db);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> RegistroEventos.salvarDeferidos(db)));
    }
}