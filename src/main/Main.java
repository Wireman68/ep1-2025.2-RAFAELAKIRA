package main;

import main.bancos.BancoDeDados;

import static main.menus.MenuLogico.menu;

public class Main
{
    private static boolean EXIT_CHAVE = false;
    public static BancoDeDados db = new BancoDeDados();

    public static void main(String[] args)
    {
        menu(db);
    }
}