package com.example.appliz;

public class Nodo {
    public int  dato; //Se define el elemento de información del objeto
    public Nodo siguiente; //Se define el enlace entre objetos de tipo Nodo

    public Nodo (int dato){
        this.dato = dato;
        this.siguiente = null;
    }

    public int getDato() {
        return dato;
    }

    public void setDato(int dato) {
        this.dato = dato;
    }

}
