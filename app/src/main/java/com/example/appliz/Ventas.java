package com.example.appliz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
    ArrayAdapter adaptador;
    Button Consultar,Escanear,Pagar;
    EditText Cambio;
    ConexionMySql conexion;
    String productoBuscar;
    TextView PrecioPro;
    double CambioTotal;
    double total = 0.0;
    String NombreProd[] = new String[500];
    double PrecioProd[] = new double[500];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);
        listaPro = (ListView) findViewById(R.id.lv_productos);
        Escanear = (Button) findViewById(R.id.btnEscanearVentas);
        Consultar = (Button) findViewById(R.id.btnConsultas);
        Cambio = (EditText) findViewById(R.id.eCambio);
        Pagar = (Button) findViewById(R.id.btnPagar);
        PrecioPro = (TextView) findViewById(R.id.txtTotal);
        Escanear.setOnClickListener(EscanearBuscar);



        listaPro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(Ventas.this);
                alerta.setMessage("Deseas eliminar el producto: '"+NombreProd[position]+"'")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                productos.remove(position);
                                adaptador.notifyDataSetChanged();
                                CambioTotal = PrecioProd[position]-CambioTotal;
                                System.out.println(CambioTotal);
                                PrecioPro.setText(String.valueOf("Total: $"+CambioTotal));
                                dialog.cancel();
                            }

                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog titulo = alerta.create();
                titulo.setTitle("Eliminar");
                titulo.show();
            }
        });

        Pagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validar()){
                    CambioTotal = Double.parseDouble(Cambio.getText().toString()) - total;
                    productos.clear();
                    Cambio.setText("");
                    for (int e = 0; e <NombreProd.length ; e++) {
                        NombreProd[e] = null;
                    }
                    PrecioPro.setText("Total: $0.0");
                    DialogoPersonalizado();
                }
            }
        });

        llenarLista(productos);
        conexion = new ConexionMySql();

    }
    void llenarLista(ArrayList listaDatos) {
        adaptador = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, listaDatos);
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
        int i=0;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String msj) {
            if(exito){
                llenarLista(productos);
                System.out.println(total);
                for (int j = 0; j <NombreProd.length ; j++) {
                    System.out.println(NombreProd[j]);
                }
                for (int j = 0; j <PrecioProd.length ; j++) {
                    System.out.println(NombreProd[j]);
                }
                PrecioPro.setText("Total: $"+String.valueOf(total));
            }else{
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
                        exito=true;
                            total = total+rs.getDouble("Precio");
                            productos.add("Producto: "+rs.getString("NombreProd")+
                                    " \nPrecio: "+rs.getDouble("Precio"));
                            PrecioProd[i]= rs.getDouble("Precio");
                            NombreProd[i] = rs.getString("NombreProd");
                        i++;


                    }else{
                        mensaje="No se encontro el producto";
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

    boolean validar(){
        boolean retorno=true;
        double c1 =Double.parseDouble(Cambio.getText().toString());
       if (c1 == 0.0){
           Cambio.setError("Ingresa el pago del cliente");
           retorno = false;
       }

       return retorno;
    }

    void DialogoPersonalizado(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Ventas.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pagototal,null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView txtcambio = view.findViewById(R.id.TXTCAMBIO);
        txtcambio.setText(String.valueOf(CambioTotal));
        Button btnAceptar = view.findViewById(R.id.btnAceptar);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }




}