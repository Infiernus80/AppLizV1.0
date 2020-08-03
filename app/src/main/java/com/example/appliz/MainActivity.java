package com.example.appliz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText Correo, Contraseña;
    Button Iniciar;
    ProgressBar barInicio;
    Spinner sTipoEmpleado;
    ConexionMySql conexion;
    String nombre;
    int pos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Correo = findViewById(R.id.Correo);
        sTipoEmpleado = findViewById(R.id.sTipoEmpleado);
        Contraseña = findViewById(R.id.Contraseña);
        Iniciar = findViewById(R.id.btn_Iniciar);
        barInicio = findViewById(R.id.barInicio);

        barInicio.setVisibility(View.GONE);

        conexion = new ConexionMySql();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this,R.array.TipoEmpleado,R.layout.simple_spinner_text_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sTipoEmpleado.setAdapter(adapter);

        Iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicioSesion sesion = new inicioSesion();
                pos = sTipoEmpleado.getSelectedItemPosition();
                //System.out.println(pos);
                if (pos == 0)
                    sesion.execute("select * from empleado where Correo=? and Constrasenia=? and Tipo='Cajero' or Tipo=" +
                            "'ADMIN'");
                else if (pos == 1)
                    sesion.execute("select * from empleado where Correo=? and Constrasenia=? and" +
                            " Tipo='Almacen' or Tipo='ADMIN'");
            }
        });//Termina onClick iniciar
    }//Termina onCreate

    public class inicioSesion extends AsyncTask<String, String, String> {
        String mensaje = "";
        boolean exito = false;
        int IdEmpleado;
        String usuario = Correo.getText().toString();
        String pass = Contraseña.getText().toString();


        @Override
        protected void onPreExecute() {
            barInicio.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String msj) {

            barInicio.setVisibility(View.GONE);

            if (exito) {
                if (pos == 0) {
                    Intent Ventas = new Intent(getApplicationContext(), Ventas.class);
                    Ventas.putExtra("NomEmpleado", nombre);
                    Ventas.putExtra("IdEmpleado", IdEmpleado);
                    startActivity(Ventas);
                } else if (pos == 1) {
                    Intent Bodega = new Intent(getApplicationContext(), MenuPrincipalB.class);
                    Bodega.putExtra("NomEmpleado", nombre);
                    Bodega.putExtra("IdEmpleado", IdEmpleado);
                    startActivity(Bodega);
                }

            } else {
                Toast.makeText(MainActivity.this, msj, Toast.LENGTH_SHORT).show();
            }
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {
            Connection con = conexion.Conectar();
            //Preparamos query a la base de datos

            if (con != null) {
                try {

                    PreparedStatement ps = con.prepareStatement(strings[0]);
                    ps.setString(1, usuario);
                    ps.setString(2, pass);

                    //ejecutar la instruccion (query) y prepara la respuesta de la base de datos
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        exito = true;
                        //mensaje = "Bienvenido: " + rs.getString("nombre");
                        nombre = rs.getString("Nombre");
                        IdEmpleado = rs.getInt("Id_Empleado");


                    } else {
                        mensaje = "Usuario o contraseña incorrectos";
                    }


                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //Cierre de sesión
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                mensaje = "Error al conectar a la base de datos";
            }


            return mensaje;
        }
    }//Termina metodo Iniciar Sesion
}//Termina class ManinActivity
