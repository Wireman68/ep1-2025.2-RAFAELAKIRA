package com.akira.hospital.menus;

import com.akira.hospital.GUI;

public class MenuHospital
{
    public static void imp(String string)
    {
        System.out.println(string);
    }

    public static void abertura()
    {
        try {
            GUI.cls();
            Thread.sleep(50);
            imp("Carregando dados");
            imp("...");
            Thread.sleep(1000);
            GUI.cls();
            imp("======================");
            imp("Bem vindo ao SGH FCTE!");
            imp("======================");
            imp("O que você deseja fazer?");
            imp("1 - Pacientes");
            imp("2 - Médico");
            imp("3 - Consultas");
            imp("4 - Internações");
            imp("5 - Planos de Saúde");
            imp("6 - Imprimir relatório");
            imp("7 - Outros");
            imp("0 - Finalizar programa");
        } catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static void menuPaciente()
    {
        try {
            GUI.cls();
            Thread.sleep(50);
            imp("======================");
            imp("1 - Cadastrar paciente");
            imp("2 - Aprimorar plano de paciente");
            imp("0 - Voltar");
        } catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static void menuMedico()
    {
        try {
        GUI.cls();
        Thread.sleep(50);
        imp("======================");
        imp("1 - Cadastrar médico");
        imp("2 - Adicionar especialidade para um médico");
        imp("3 - Adicionar data em um calendário de um médico");
        imp("0 - Voltar");
        } catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static void menuConsulta()
    {
        try {
        GUI.cls();
        Thread.sleep(50);
        imp("======================");
        imp("1 - Agendar consulta");
        imp("2 - Procurar médicos disponíveis");
        imp("3 - Finalizar consulta");
        imp("0 - Voltar");
        } catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static void menuInternacao()
    {
        try {
        GUI.cls();
        Thread.sleep(50);
        imp("======================");
        imp("1 - Internar paciente");
        imp("2 - Finalizar internação");
        imp("3 - Listar quartos livres");
        imp("0 - Voltar");
        } catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static void menuPlano()
    {
        try {
        GUI.cls();
        Thread.sleep(50);
        imp("======================");
        imp("1 - Cadastrar plano de saúde");
        imp("2 - Cadastrar plano de saúde para um paciente");
        imp("0 - Voltar");
        } catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static void menuRelatorio()
    {
        try {
        GUI.cls();
        Thread.sleep(50);
        imp("======================");
        imp("1 - Exibir pacientes");
        imp("2 - Exibir médicos");
        imp("3 - Exibir consultas");
        imp("4 - Exibir pacientes internados");
        imp("5 - Exibir estatísicas gerais");
        imp("6 - Exibir pacientes em um plano de saúde");
        imp("0 - Voltar");
        } catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static void menuOutros()
    {
        try {
        GUI.cls();
        Thread.sleep(50);
        imp("======================");
        imp("1 - Remover paciente");
        imp("2 - Remover médico");
        imp("1 - Remover plano de saúde");
        } catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static void menuSaida()
    {
        try {
        GUI.cls();
        Thread.sleep(50);
        imp("======================");
        imp("Até mais!");
        } catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
