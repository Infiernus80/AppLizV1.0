package com.example.appliz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ConsultarProductos extends AppCompatActivity{
    ConexionMySql conexion;
    EditText BusquedaNC;
    Spinner sCategoria,sSubcategoria;
    ListView lv_Productos;
    ArrayList datos;
    String[] idprod = new String[500];
    String[] nombrepro = new String[500];
    int[] precio,existencia;
    Button Busqueda,btnBuscar,limpiar;
    String ProductoBus;
    String nombre;
    int precioT,existenciaT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_productos);
        BusquedaNC = (EditText) findViewById(R.id.BusquedaNC);
        lv_Productos = (ListView) findViewById(R.id.lv_Productos);
        Busqueda = (Button) findViewById(R.id.btnEscanearB);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        limpiar = (Button) findViewById(R.id.reset);
        ConsultarPro consultarPro = new ConsultarPro();
        consultarPro.execute("select * from Producto","todo");

        precio = new int[500];
        existencia = new int[500];


        conexion = new ConexionMySql();
        datos = new ArrayList();
        lv_Productos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(idprod[position]);
                precioT = precio[position];
                existenciaT = existencia[position];
                nombre =nombrepro[position];
                ventanaEmergente();
            }
        });

        Busqueda.setOnClickListener(EscanearModi);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductoBus = BusquedaNC.getText().toString();
                System.out.println(ProductoBus);
                ConsultarPro consultarPro = new ConsultarPro();
                consultarPro.execute("select * from Producto where NombreProd=? or Id_Producto=?","C");
            }
        });
        limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConsultarPro consultarPro = new ConsultarPro();
                consultarPro.execute("select * from Producto","todo");
                BusquedaNC.setText("");
            }
        });

    }
    //Metodo para escanear
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                BusquedaNC.setText(result.getContents());
            } else {
                Toast.makeText(this, "Error al escanear el código", Toast.LENGTH_SHORT).show();
            }

        }
    }//Termina metodo para escanear

    //Metodo del boton escanera
    public View.OnClickListener EscanearModi = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnEscanearB:
                    new IntentIntegrator(ConsultarProductos.this).initiateScan();

            }
        }
    };//Termina metodo del boton escanear

    public void llenarLista(ArrayList listaDatos) {
        ArrayAdapter adaptador = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, listaDatos);
        lv_Productos.setAdapter(adaptador);
    }

    public  class ConsultarPro extends AsyncTask<String,String,String> {
        boolean exito = false;
        String mensaje;
        int i;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String msj) {
            if(exito){
                llenarLista(datos);
                for(int l=0;l<idprod.length;l++){
                    System.out.println(idprod[l]);
                }
            }else{
                datos.clear();
                llenarLista(datos);
                Toast.makeText(ConsultarProductos.this, msj, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            Connection con = conexion.Conectar();
            if (con != null){
                try {
                    PreparedStatement ps =con.prepareStatement(strings[0]);
                    if (strings[1].equals("C")){
                        ps.setString(1,ProductoBus);
                        ps.setString(2,ProductoBus);
                    }


                    ResultSet rs = ps.executeQuery();

                    if(rs.next()){
                        datos.clear();
                        for(int l=0;l<idprod.length;l++){
                            idprod[l]=null;
                        }
                        for(int l=0;l<nombrepro.length;l++){
                            nombrepro[l]=null;
                        }
                        for(int l=0;l<precio.length;l++){
                            precio[l]=0;
                        }
                        for(int l=0;l<existencia.length;l++){
                            existencia[l]=0;
                        }
                        exito=true;
                        do {
                            idprod[i] = rs.getString("Id_Producto");
                            nombrepro[i] = rs.getString("NombreProd");
                            precio[i] = rs.getInt("Precio");
                            existencia[i] = rs.getInt("Existencia");
                            datos.add("Nombre del producto: "+rs.getString("NombreProd"));

                        i++;
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
    }//Cierre ABM

    public void ventanaEmergente(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(ConsultarProductos.this);
        alerta.setMessage("Nombre del producto: "+nombre +
                "\nPrecio del producto: $"+ Html.fromHtml("<font color='#31B404'>"+precioT+"</font>") +
                "\nExistencia: "+existenciaT+" productos")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog titulo = alerta.create();
        titulo.setTitle("Producto");
        titulo.show();


    }



}//fin de la clase