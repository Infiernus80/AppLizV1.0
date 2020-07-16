package com.example.appliz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class menu extends AppCompatActivity {
    private Button Cerrar;
    ImageButton ibAgregar,ibModificar,ibConsultar,ibEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        Cerrar = (Button) findViewById(R.id.btnVolver);
        ibAgregar = findViewById(R.id.ibAgregar);
        ibModificar = findViewById(R.id.ibModificar);
        ibConsultar = findViewById(R.id.ibConsultar);

        //Ir a menu Agregar
        ibAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent agregar = new Intent(getApplicationContext(),AgregarProducto.class);
                startActivity(agregar);
            }
        });
/*
        //Ir a menu Modificar
        ibModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent modificar = new Intent(getApplicationContext(),MenuModificar.class);
                startActivity(modificar);
            }
        });
        //Ir a menu Consultar
        ibConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent consultar = new Intent(getApplicationContext(),MenuConsultar.class);
                startActivity(consultar);
            }
        });

*/
        Cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MenuPrincipalB.class);
                startActivity(intent);
            }
        });

    }
}//cierra el menu
