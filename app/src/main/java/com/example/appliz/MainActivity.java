package com.example.appliz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {
    EditText Correo, Contraseña;
    Button Iniciar;
    ProgressBar barInicio;
    ConexionMySql conexion;
    String nombre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Correo = findViewById(R.id.Correo);
        Contraseña = findViewById(R.id.Contraseña);
        Iniciar = findViewById(R.id.btn_Iniciar);
        barInicio = findViewById(R.id.barInicio);

        barInicio.setVisibility(View.GONE);

        conexion = new ConexionMySql();

        Iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicioSesion sesion = new inicioSesion();
                sesion.execute("");
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
                Intent iniciar = new Intent(getApplicationContext(), MenuPrincipalB.class);
                iniciar.putExtra("NomEmpleado", nombre);
                iniciar.putExtra("IdEmpleado",IdEmpleado);
                startActivity(iniciar);
            }else{
                Toast.makeText(MainActivity.this, msj, Toast.LENGTH_SHORT).show();
            }
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {
            Connection con = conexion.Conectar();
            //Preparamos query a la base de datos
            String query = "select * from empleado where Correo=? and Constrasenia=?";
            if (con != null) {
                try {

                    PreparedStatement ps = con.prepareStatement(query);
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
