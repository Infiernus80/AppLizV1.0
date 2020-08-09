package com.example.appliz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Ventas extends AppCompatActivity {
    ListView listaPro;
    ArrayList<String> productos = new ArrayList<>();
    Button Consultar,Escanear;
    ConexionMySql conexion;
    String productoBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);
        listaPro = (ListView) findViewById(R.id.lv_productos);
        Escanear = (Button) findViewById(R.id.btnEscanearVentas);
        Consultar = (Button) findViewById(R.id.btnConsultas);

        Escanear.setOnClickListener(EscanearBuscar);

        //productos.add("Prueba");

        llenarLista(productos);
        conexion = new ConexionMySql();

    }
    public void llenarLista(ArrayList listaDatos) {
        ArrayAdapter adaptador = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, listaDatos);
        listaPro.setAdapter(adaptador);
    }
    //Metodo para escanear
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                VentaProductos productos = new VentaProductos();
                productoBuscar = result.getContents();
                System.out.println(productoBuscar);
                productos.execute("select * from Producto where Id_Producto=?");
            } else {
                Toast.makeText(this, "Error al escanear el código", Toast.LENGTH_SHORT).show();
            }

        }
    }//Termina metodo para escanear

    //Metodo del boton escanera
    public View.OnClickListener EscanearBuscar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnEscanearVentas:
                    new IntentIntegrator(Ventas.this).initiateScan();

            }
        }
    };//Termina metodo del boton escanear
    public  class VentaProductos extends AsyncTask<String,String,String> {
        boolean exito = false;
        String mensaje;
        int i;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String msj) {
            if(exito){
                llenarLista(productos);
            }else{
                productos.clear();
                llenarLista(productos);
                Toast.makeText(Ventas.this, msj, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            Connection con = conexion.Conectar();
            if (con != null){
                try {
                    PreparedStatement ps =con.prepareStatement(strings[0]);

                        ps.setString(1,productoBuscar);



                    ResultSet rs = ps.executeQuery();

                    if(rs.next()){
                        productos.clear();

                        exito=true;
                        do {
                            productos.add("Producto: "+rs.getString("NombreProd")+" \nPrecio: "+rs.getString("Precio"));

                        }while (rs.next());

                    }else{
                        mensaje="No hay registros";
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
    }//Cierre VentaProductos
}