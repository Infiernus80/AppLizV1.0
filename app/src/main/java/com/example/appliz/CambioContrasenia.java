package com.example.appliz;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CambioContrasenia extends AppCompatActivity {
    EditText Ncontraseña,CNcontraseña;
    Button CambioContra;
    ConexionMySql conexion;
    String Nueva,Empleado;
    int IdEmpleado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_contrasenia);
        Ncontraseña = (EditText) findViewById(R.id.Ncontraseña);
        CNcontraseña = (EditText) findViewById(R.id.CNcontraseña);
        CambioContra = (Button) findViewById(R.id.btnCambiarContra);
        conexion = new ConexionMySql();
        //Se obtiene el nombre del empleado
        Bundle extra = getIntent().getExtras();
        Empleado = extra.getString("NombreEmpleado");
        IdEmpleado =extra.getInt("IdEmpleado");
        CambioContra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nueva = Ncontraseña.getText().toString();

                OperaABM opera = new OperaABM();
                opera.execute("Update empleado set Constrasenia=? where Id_Empleado="+IdEmpleado, "M");

            }
        });//Cierre del boton Cambio contraseña
    }//Cierre de onCreate

    public class OperaABM extends AsyncTask<String, String, String> {
        String mensaje = "";
        boolean exito = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String msj) {


            if (exito) {
                //Toast.makeText(CambioContrasenia.this, msj, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alerta = new AlertDialog.Builder(CambioContrasenia.this);
                alerta.setMessage("Porfavor vuelve a iniciar sesión").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent regreso = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(regreso);
                    }
                });
                AlertDialog titulo = alerta.create();
                titulo.setTitle("La contraseña se modifico correctamente");
                titulo.show();
            }
            else {
                Toast.makeText(CambioContrasenia.this, msj, Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected String doInBackground(String... strings) {
            Connection con = conexion.Conectar();
            if (con != null) {
                try {
                    PreparedStatement ps = con.prepareStatement(strings[0]);

                    if (strings[1].equals("M")) {
                        ps.setString(1, Nueva);

                    }
                    if (ps.executeUpdate() > 0) {
                        exito = true;
                        if (strings[1].equals("M"))
                            mensaje = "Contraseña modificada";
                    } else {

                        if (strings[1].equals("M"))
                            mensaje = "Contraseña no modificada";


                    }


                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                mensaje = "Error al conectar con la base de datos";
            }
            return mensaje;
        }
    }//Cierre de la clase ABM
}