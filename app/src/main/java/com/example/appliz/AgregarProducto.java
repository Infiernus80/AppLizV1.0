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
    String Codigo,Nombre,Categoria,Descripcion;
    int Existencia,Precio;

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
                Codigo = etCodigoAg.getText().toString();
                Nombre = etNombreAg.getText().toString();
                Precio = Integer.parseInt(etPrecioAg.getText().toString());
                Categoria = etCategoriaAg.getText().toString();
                Existencia = Integer.parseInt(etExistenciaAg.getText().toString());
                Descripcion = etDescripcionAg.getText().toString();


                OperaABM opera = new OperaABM();
                opera.execute("insert into producto (codigo,nombre,fechacad,precio,categoria,existencia,descripcion) values(?,?,?,?,?,?,?)","G");

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


    public class OperaABM extends AsyncTask<String, String, String> {
        String mensaje = "";
        boolean exito = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String msj) {
            Toast.makeText(getApplicationContext(), msj, Toast.LENGTH_SHORT).show();

            if (exito) {
                etCodigoAg.setText("");
                etNombreAg.setText("");
                etPrecioAg.setText("");
                etCategoriaAg.setText("");
                etExistenciaAg.setText("");
                etDescripcionAg.setText("");
            }
        }


        @Override
        protected String doInBackground(String... strings) {
            Connection con = conexion.Conectar();
            if (con != null) {
                try {
                    PreparedStatement ps = con.prepareStatement(strings[0]);
                    if (strings[1].equals("G")) {
                        ps.setString(1, Codigo);
                        ps.setString(2, Nombre);
                        ps.setInt(3, Precio);
                        ps.setString(4, Categoria);
                        ps.setInt(5, Existencia);
                        ps.setString(6, Descripcion);


                    }
                    if (ps.executeUpdate() > 0) {

                        exito = true;
                        if (strings[1].equals("G")) ;
                        mensaje = "Registro guardado";

                    } else {

                        if (strings[1].equals("G")) ;
                        mensaje = "Registro no guardado";

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


}//Fin de la clase
