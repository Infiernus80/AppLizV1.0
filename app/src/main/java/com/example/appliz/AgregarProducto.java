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
    EditText etCodigoAg, etNombreAg, etPrecioAg, etCategoriaAg,etSubcategoria, etExistenciaAg, etDescripcionAg;
    String Codigo,Nombre,Categoria,SubCategoria,Descripcion;
    int Existencia;
    double Precio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        btn_Escanear = findViewById(R.id.btn_EscanearAg);
        btn_Agregar = findViewById(R.id.btn_Agregar);
        etCodigoAg = findViewById(R.id.etCodigoAg);
        etNombreAg = findViewById(R.id.etNombreAg);
        etPrecioAg = findViewById(R.id.etPrecioAg);
        etCategoriaAg = findViewById(R.id.etCategoriaAg);
        etExistenciaAg = findViewById(R.id.etExistenciaAg);
        etDescripcionAg = findViewById(R.id.etDescripcionAg);
        etSubcategoria = findViewById(R.id.etSubCategoriaAg);
        conexion = new ConexionMySql();

        btn_Escanear.setOnClickListener(mOnClickListener);
        btn_Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Codigo = etCodigoAg.getText().toString();
                Nombre = etNombreAg.getText().toString();
                Precio = Double.parseDouble(etPrecioAg.getText().toString());
                Categoria = etCategoriaAg.getText().toString();
                SubCategoria = etSubcategoria.getText().toString();
                Existencia = Integer.parseInt(etExistenciaAg.getText().toString());
                Descripcion = etDescripcionAg.getText().toString();


                Agregar agregar = new Agregar();
                agregar.execute("insert into producto (Id_Producto,NombreProd,Categoria,SubCategoria,existencia,Precio,descripcion) values (?,?,?,?,?,?,?)","g");

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
                etSubcategoria.setText("");
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
                        ps.setString(1,Codigo);
                        ps.setString(2,Nombre);
                        ps.setString(3,Categoria);
                        ps.setString(4,SubCategoria);
                        ps.setInt(5,Existencia);
                        ps.setDouble(6,Precio);
                        ps.setString(7,Descripcion);
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
    }//cierre de clase agregarProducto
/*
    public class Consulta extends AsyncTask<String, String, String> {
        String mensaje = "";
        boolean exito = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String msj) {
            if (exito){
                etCodigo.setText(codigo+"");
                etNombre.setText(nombre);
                etPuesto.setText(puesto);
                etSueldo.setText(sueldo+"");
                etUsuario.setText(usuario+"");
                etContra.setText(pass+"");
            }else{
                Toast.makeText(MainActivity2.this, msj, Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected String doInBackground(String... strings) {
            Connection con = conexion.Conectar();
            if(con!=null){
                try {
                    PreparedStatement ps = con.prepareStatement(strings[0]);
                    ps.setString(1,nombre);
                    ps.setInt(2,codigo);

                    ResultSet rs = ps.executeQuery();
                    if(rs.next()){
                        exito=true;
                        codigo = rs.getInt("codigo");
                        nombre = rs.getString("nombre");
                        puesto = rs.getString("puesto");
                        sueldo = rs.getDouble("sueldo");
                        usuario = rs.getString("usuario");
                        pass = rs.getString("contrasenia");


                    }else{

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                mensaje = "Error a conectar a la base de datos";
            }
            return mensaje;
        }
    }//Cierre de la subclase consulta*/

}//Fin de la clase
