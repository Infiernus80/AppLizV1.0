package com.example.appliz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class menu extends AppCompatActivity {
    ImageButton ibAgregar,ibModificar,ibConsultar,ibEliminar;

    int IdEmpleado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ibAgregar = findViewById(R.id.ibAgregar);
        ibModificar = findViewById(R.id.ibModificar);
        ibConsultar = findViewById(R.id.ibConsultar);

        Bundle extra = getIntent().getExtras();
        IdEmpleado = extra.getInt("IdEmpleado");

        //Ir a menu Agregar
        ibAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent agregar = new Intent(getApplicationContext(),AgregarProducto.class);
                agregar.putExtra("IdEmpleado",IdEmpleado);
                startActivity(agregar);
            }
        });

        //Ir a menu Modificar
        ibModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent modificar = new Intent(getApplicationContext(),ModificarProducto.class);
                startActivity(modificar);
            }
        });
        //Ir a menu Consultar
        ibConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent consultar = new Intent(getApplicationContext(),ConsultarProductos.class);
                startActivity(consultar);
            }
        });



    }
}//cierra el menu
