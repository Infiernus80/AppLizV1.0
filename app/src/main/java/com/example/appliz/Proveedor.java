package com.example.appliz;

public class Proveedor {

    private int id;
    private String Nombre;
    private String Nombre_Agente;

    public Proveedor(int id, String nombre, String nombre_Agente) {
        this.id = id;
        Nombre = nombre;
        Nombre_Agente = nombre_Agente;
    }

    public Proveedor() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getNombre_Agente() {
        return Nombre_Agente;
    }

    public void setNombre_Agente(String nombre_Agente) {
        Nombre_Agente = nombre_Agente;
    }
}
