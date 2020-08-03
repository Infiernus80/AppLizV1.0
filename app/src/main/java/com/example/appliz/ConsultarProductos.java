package com.example.appliz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class ConsultarProductos extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ConexionMySql conexion;
    EditText BusquedaNC;
    Spinner sCategoria,sSubcategoria;
    ListView lv_Productos;
    ArrayList datos;
    Button Busqueda,btnBuscar;
    String ProductoBus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_productos);
        BusquedaNC = (EditText) findViewById(R.id.BusquedaNC);
        sCategoria = (Spinner) findViewById(R.id.CategoriaB);
        sSubcategoria= (Spinner) findViewById(R.id.SubcategoriaB);
        lv_Productos = (ListView) findViewById(R.id.lv_Productos);
        Busqueda = (Button) findViewById(R.id.btnEscanearB);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);


        conexion = new ConexionMySql();
        datos = new ArrayList();

        Busqueda.setOnClickListener(EscanearModi);
        ConsultarPro consultarPro = new ConsultarPro();
        consultarPro.execute("select NombreProd from producto","todo");
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.Categoria,R.layout.simple_spinner_text_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCategoria.setAdapter(adapter);
        sCategoria.setOnItemSelectedListener(ConsultarProductos.this);


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
                Toast.makeText(this, "Error al escanear el codigo", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int[] subcategorias = {R.array.SubCategoria, R.array.Alimentos, R.array.Abarrotes};
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, subcategorias[position], R.layout.simple_spinner_text_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSubcategoria.setAdapter(adapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void llenarLista(ArrayList listaDatos) {
        ArrayAdapter adaptador = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, listaDatos);
        lv_Productos.setAdapter(adaptador);
    }

    public  class ConsultarPro extends AsyncTask<String,String,String> {
        boolean exito = false;
        String mensaje;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String msj) {
            if(exito){
                llenarLista(datos);
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
                   /* if (strings[1].equals("nom")){
                        ps.setString(1,nombreBuscar);
                    }*/
                    ResultSet rs = ps.executeQuery();

                    if(rs.next()){
                        datos.clear();
                        exito=true;
                        do {
                            datos.add("Nombre del producto: "+rs.getString("NombreProd"));

                        }while (rs.next());

                    }else{
                        mensaje="No hay registros";
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                mensaje= "Error al conectar a la base de datos";
            }

            return mensaje;
        }
    }//Cierre ABM
}