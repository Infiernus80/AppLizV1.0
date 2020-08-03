package com.example.appliz;

import android.widget.Spinner;

public class SubCategorias {
    String subCategoria;

    public SubCategorias(){}

    public SubCategorias(String subCategoria) {
        setSubCategoria(subCategoria);
    }

    public String toString() {
        return subCategoria;
    }

    public void setSubCategoria(String subCategoria) {
        this.subCategoria = subCategoria;
    }
}
