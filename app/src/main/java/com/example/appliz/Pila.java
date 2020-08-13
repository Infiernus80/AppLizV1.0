package com.example.appliz;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class Pila {
    public Nodo crearNodo(int dato) {
        Nodo nodo = new Nodo(dato);
        return nodo;
    }

    public boolean listaVacia(Nodo lista) {
        if (lista == null)
            return true;

        return false;
    }

    public Nodo push(Nodo lista, int dato) {
        if (listaVacia(lista)) {
            lista = crearNodo(dato);
        } else {
            Nodo auxiliar = lista;
            while (auxiliar.siguiente != null) {
                auxiliar = auxiliar.siguiente;
            }
            auxiliar.siguiente = crearNodo(dato);
        }
        return lista;
    }

    public void imprimirLista(Nodo lista) {

        if (!listaVacia(lista)) {
            Nodo auxiliar = lista;

            while (auxiliar.siguiente != null) {
                System.out.print(auxiliar.getDato());
                auxiliar = auxiliar.siguiente;
                System.out.print(" -> ");
            }

            System.out.println(auxiliar.getDato());

        } else
            System.out.println("Lista Vacia");

    }

    //Borrar ultimo elemento de la pila
    public Nodo pop(Nodo lista, ArrayList list, ArrayAdapter adapter) {
        Nodo auxiliar = lista;
        Nodo nodo;
        Nodo anterior = lista;
        if (!listaVacia(lista)) {
            //si solo hay un nodo en la lista
            if (auxiliar.siguiente == null){
                list.remove(lista.getDato());
                adapter.notifyDataSetChanged();
                lista = auxiliar.siguiente;
            }


            else {
                // Vamos saltando los nodos que no coincidan hasta
                // que encontremos uno o se acabe la lista
                while (auxiliar.siguiente != null) {
                    anterior = auxiliar;
                    auxiliar = auxiliar.siguiente;
                }
                nodo = anterior.siguiente;
                list.remove(nodo.getDato());
                adapter.notifyDataSetChanged();
                anterior.siguiente = null;
            }

        } else
            System.out.println("Lista Vacia");


        return lista;
    }

}
