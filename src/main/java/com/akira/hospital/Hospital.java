package com.akira.hospital;

import com.akira.hospital.bancos.BancoDeDados;
import com.akira.hospital.core.RegistroEventos;

import static com.akira.hospital.menus.MenuLogico.menu;

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