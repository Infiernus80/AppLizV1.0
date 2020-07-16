package com.example.appliz;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AgregarProducto extends AppCompatActivity {
    ConexionMySql conexion;
    Button btn_Escanear, btn_Agregar;
    EditText etCodigoAg, etNombreAg, etPrecioAg, etCategoriaAg, etExistenciaAg, etDescripcionAg;
    String Nombre,Categoria,Descripcion;
    int Existencia;
    double Precio;
    float Codigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        btn_Escanear = findViewById(R.id.btn_EscanearAg);
        btn_Agregar = findViewById(R.id.btn_Agregar);
        etCodigoAg = findViewById(R.id.etCodigoAg);
        etNombreAg = findViewById(R.id.etNombreAg);
        etPrecioAg = findViewById(R.id.etPrecioAg);
        etCategoriaAg = findViewById(R.id.etDescripcionAg);
        etExistenciaAg = findViewById(R.id.etExistenciaAg);
        etDescripcionAg = findViewById(R.id.etDescripcionAg);
        conexion = new ConexionMySql();

        btn_Escanear.setOnClickListener(mOnClickListener);
        btn_Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Codigo = Float.parseFloat(etCodigoAg.getText().toString());
                Nombre = etNombreAg.getText().toString();
                Precio = Double.parseDouble(etPrecioAg.getText().toString());
                Categoria = etCategoriaAg.getText().toString();
                Existencia = Integer.parseInt(etExistenciaAg.getText().toString());
                Descripcion = etDescripcionAg.getText().toString();


                Agregar agregar = new Agregar();
                agregar.execute("insert into producto (Id_Producto,NombreProd,Categoria,existencia,Precio,descripcion) values (?,?,?,?,?,?)","g");

            }
        });


    }//Fin del metodo onCreate

    //Metodo para escanear
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                etCodigoAg.setText(result.getContents());
            } else {
                Toast.makeText(this, "Error al escanear el codigo", Toast.LENGTH_SHORT).show();
            }

        }
    }//Termina metodo para escanear

    //Metodo del boton escanera
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_EscanearAg:
                    new IntentIntegrator(AgregarProducto.this).initiateScan();

            }
        }
    };//Termina metodo del boton escanear

    public class Agregar extends  AsyncTask<String,String,String>{
        String mensaje="";
        boolean exito = false;
        @Override
        protected void onPostExecute(String msj) {
            Toast.makeText(AgregarProducto.this, msj, Toast.LENGTH_SHORT).show();
            if (exito){
                etCodigoAg.setText("");
                etNombreAg.setText("");
                etPrecioAg.setText("");
                etCategoriaAg.setText("");
                etExistenciaAg.setText("");
                etDescripcionAg.setText("");
            }

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {
            Connection con = conexion.Conectar();
            if (con != null){
                try {
                    PreparedStatement ps =con.prepareStatement(strings[0]);
                    if (strings[1].equals("g")){
                        ps.setFloat(1,Codigo);
                        ps.setString(2,Nombre);
                        ps.setString(3,Categoria);
                        ps.setInt(4,Existencia);
                        ps.setDouble(5,Precio);
                        ps.setString(6,Descripcion);
                    }
                    if(ps.executeUpdate() > 0){
                        exito = true;
                        if (strings[1].equals("g"))
                            mensaje = "Registro guardado";
                    }
                    else {
                        if (strings[1].equals("g")) ;
                        mensaje = "Registro no guardado";
                    }
                } catch (Exception e) {
                   mensaje = e.getMessage();
                }
                try {
                    con.close();
                } catch (SQLException e) {
                    mensaje=e.getMessage();
                }
            }else {
                mensaje = "Error al conectar con la base de datos";
            }

            return mensaje;
        }
    }
    /*public class OperaABM extends AsyncTask<String, String, String> {
        String mensaje = "";
        boolean exito = false;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String msj) {
            Toast.makeText(AgregarProducto.this, msj, Toast.LENGTH_SHORT).show();

            if (exito) {

                etCodigo.setText("");
                etContra.setText("");
                etNombre.setText("");
                etPuesto.setText("");
                etSueldo.setText("");
                etUsuario.setText("");

            }
        }


        @Override
        protected String doInBackground(String... strings) {
            Connection con = conexion.Conectar();
            if (con != null) {
                try {
                    PreparedStatement ps = con.prepareStatement(strings[0]);
                    if (strings[1].equals("g")) {
                        ps.setFloat(1, Codigo);
                        ps.setString(2, Nombre);
                        ps.setInt(3,Precio);
                        ps.setString(4, Categoria);
                        ps.setInt(5, Existencia);
                        ps.setString(6, Descripcion);


                    }

                    if (strings[1].equals("M")) {
                        ps.setString(1, nombre);
                        ps.setString(2, puesto);
                        ps.setDouble(3, sueldo);
                        ps.setString(4, usuario);
                        ps.setString(5, pass);
                        ps.setInt(6, codigo);

                    }
                    if (strings[1].equals("E")) {
                        ps.setInt(1, codigo);
                    }

                    if (ps.executeUpdate() > 0) {

                        exito = true;
                        if (strings[1].equals("g")) ;
                        mensaje = "Registro guardado";
                        if (strings[1].equals("M"))
                            mensaje = "Registro modificado";
                        if (strings[1].equals("E"))
                            mensaje = "Registro eliminado";

                    } else {

                        if (strings[1].equals("g")) ;
                        mensaje = "Registro no guardado";
                        if (strings[1].equals("M"))
                            mensaje = "Registro no modificado";
                        if (strings[1].equals("E"))
                            mensaje = "Registro no eliminado";

                    }


                } catch (SQLException e) {
                    Toast.makeText(AgregarProducto.this, "Error en la operación", Toast.LENGTH_SHORT).show();
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
                            */

}//Fin de la clase
