package com.example.appliz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuPrincipalB extends AppCompatActivity {
    ImageButton misDatos, menuProductos;
    Button btn_Cerrar;
    TextView tvBienvenido;
    String Nombre;
    int IdEmpleado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal_b);

        tvBienvenido = findViewById(R.id.tvBienvenido);
        misDatos = findViewById(R.id.ibtnDatos);
        menuProductos = findViewById(R.id.ibtnProductos);
        btn_Cerrar = findViewById(R.id.btn_Cerrar);
        Bundle extra = getIntent().getExtras();
        Nombre = extra.getString("NomEmpleado");
        IdEmpleado = extra.getInt("IdEmpleado");
        tvBienvenido.setText("BIENVENIDO: "+Nombre);

        misDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent datos = new Intent(getApplicationContext(), MisDatos.class);
                datos.putExtra("Empleado",Nombre);
                datos.putExtra("IdEmpleado",IdEmpleado);
                startActivity(datos);
            }
        });//Termina OnClick de Mis datos*/

        menuProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productos = new Intent(getApplicationContext(), menu.class);
                startActivity(productos);
            }
        });//Termina OnClick de Productos

        btn_Cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cerrar = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(cerrar);
            }
        });
    }//Finaliza onCreate




}
