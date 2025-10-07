package com.akira.hospital.registro;

// INTERFACE COMPARTILHADA POR TODAS AS ENTIDADES REGISTRAVEIS
public interface Entidade
{
    String getID();
    String paraDado();

    void displayDados();
}
