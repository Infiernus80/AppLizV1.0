package com.example.appliz;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MisDatos extends AppCompatActivity {
    EditText etNombre, etApellido,etApellidoM, etFechaNac, etTelefono, etCorreo, etContraseña, etSuledo;
    Button btnContraseña;
    String empleado;
    ConexionMySql conexion;
    String Nombre,Apellido,ApellidoM,FechaNac,Telefono,Correo,Contraseña;
    double Sueldo;
    int IdEmpleado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_datos);

        //Declaracion de variables
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etFechaNac = findViewById(R.id.etFechaNac);
        etTelefono = findViewById(R.id.etTelefono);
        etCorreo = findViewById(R.id.etCorreo);
        etSuledo = findViewById(R.id.etSueldo);
        etApellidoM = findViewById(R.id.etApellido2);
        btnContraseña = findViewById(R.id.btnModificarContraseña);

        conexion = new ConexionMySql();
        //Se obtiene el nombre del empleado
        Bundle extra = getIntent().getExtras();
        empleado = extra.getString("Empleado");
        IdEmpleado = extra.getInt("IdEmpleado");
        OperaABM datos = new OperaABM();
        datos.execute("select * from Empleado where nombre=?");

        btnContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent modifcar = new Intent(MisDatos.this, CambioContrasenia.class);
                modifcar.putExtra("NombreEmpleado",empleado);
                modifcar.putExtra("IdEmpleado",IdEmpleado);
                System.out.println(empleado);
                startActivity(modifcar);
            }
        });//Termina metodo onClick modifcar contraseña
    }//Termina onCreate


    public class OperaABM extends AsyncTask<String, String, String> {
        String mensaje = "";
        boolean exito = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String msj) {
            if(exito){
                etNombre.setText(Nombre);
                etApellido.setText(Apellido);
                etApellidoM.setText(ApellidoM);
                etFechaNac.setText(FechaNac);
                etTelefono.setText(Telefono);
                etCorreo.setText(Correo);
                etSuledo.setText(""+Sueldo);

            }else{
                Toast.makeText(MisDatos.this, msj, Toast.LENGTH_SHORT).show();
            }

        }


        @Override
        protected String doInBackground(String... strings) {
            Connection con = conexion.Conectar();
            if (con != null){
                try {
                    PreparedStatement ps =con.prepareStatement(strings[0]);
                    ps.setString(1,empleado);
                    ResultSet rs = ps.executeQuery();

                    if(rs.next()){
                        exito = true;
                        Nombre = rs.getString("Nombre");
                        Apellido = rs.getString("ApellidoP");
                        ApellidoM = rs.getString("ApellidoM");
                        FechaNac = rs.getString("FechaNac");
                        Telefono = rs.getString("Telefono");
                        Correo = rs.getString("Correo");
                        Sueldo = rs.getDouble("Sueldo");
                    }

                } catch (SQLException e) {
                    mensaje = e.getMessage();
                }
                try {
                    con.close();
                } catch (SQLException e) {
                    mensaje = e.getMessage();
                }
            }else{
                mensaje= "Error al conectar a la base de datos";
            }

            return mensaje;
        }
    }//Cierre de la clase ABM*/

}//Termina class Mis datos
