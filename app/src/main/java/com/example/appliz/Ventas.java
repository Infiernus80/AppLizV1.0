package com.example.appliz;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
    int i=0,idVentaGeneral=0;
    String ventaP="p01";
    String NombreProd[] = new String[500];
    String idProducto[] = new String[500];
    double PrecioProd[] = new double[500];
    String date =(String)new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    //String fecha = String.valueOf(date);



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
        System.out.println(date);



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
                                total = total-PrecioProd[position];
                                idProducto[position] = null;
                                PrecioPro.setText(String.valueOf("Total: $"+total));
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
                    Venta venta = new Venta();
                    venta.execute("insert into Venta (MetodoPago,Tipo,Total,FechaVenta,Id_Empleado,Id_Cliente) " +
                            "values('Efectivo','Presencial',?,?,1,0)");
                    CambioTotal = Double.parseDouble(Cambio.getText().toString()) - total;
                    productos.clear();
                    Cambio.setText("");
                    for (int e = 0; e <NombreProd.length ; e++) {
                        NombreProd[e] = null;
                    }
                    PrecioPro.setText("Total: $0.0");
                    DialogoPersonalizado();
                    IdVenta presencial = new IdVenta();
                    presencial.execute("select * from Venta where Total=? and FechaVenta=? and Tipo='Presencial'");

                    InsertaTienePresencial presencial1 = new InsertaTienePresencial();
                    //presencial1.execute("insert into VentaPresencial(Id_Venta,CodigoPresencial) values(?,?)");
                    presencial1.execute("insert into Tiene(Id_Venta,Id_Producto) values(?,?)");

                    TablaTiene tiene = new TablaTiene();
                    tiene.execute("UPDATE Producto SET Existencia = Existencia - 1 WHERE Id_Producto = ?");


                    //total = 0.0;
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
               /* TablaTiene tiene = new TablaTiene();
                tiene.execute("insert into Tiene(Id_Venta,Id_Producto) values(?,?)");*/
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

    //Inicia metodo VentaProductos
    public  class VentaProductos extends AsyncTask<String,String,String> {
        boolean exito = false;
        String mensaje;


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String msj) {
            if(exito){
                llenarLista(productos);
                PrecioPro.setText("Total: $"+String.valueOf(total));
                //total = 0.0;
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
                            idProducto[i] = rs.getString("Id_Producto");
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
        String c1 =Cambio.getText().toString();
       if (c1.isEmpty()){
           Cambio.setError("Ingresa el pago del cliente");
           retorno = false;
       }if (productos.isEmpty() && c1.isEmpty()){
           final AlertDialog.Builder builder = new AlertDialog.Builder(Ventas.this);
           builder.setMessage("No se puede hacer sin productos")
                   .setCancelable(false)
                   .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           Cambio.setText("");
                           dialog.cancel();
                       }
                   });
            AlertDialog titulo = builder.create();
            titulo.setTitle("ERROR");
            titulo.show();
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
        txtcambio.setText("$"+String.valueOf(CambioTotal));
        Button btnAceptar = view.findViewById(R.id.btnAceptar);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
                startActivity(getIntent());
            }
        });
    }

    public  class Venta extends AsyncTask<String,String,String> {
        boolean exito = false;
        String mensaje;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String msj) {
            if(exito){
                total = 0.0;
                CambioTotal = 0.0;
               // Cambio = 0.0;
                Toast.makeText(Ventas.this, msj, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(Ventas.this, msj, Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            Connection con = conexion.Conectar();
            if (con != null){
                try {
                    PreparedStatement ps =con.prepareStatement(strings[0]);

                    ps.setDouble(1,total);
                    ps.setString(2, date);


                    if(ps.executeUpdate() > 0){
                        mensaje = "Venta exitosa";
                    }else{
                        mensaje="Venta no exitosa";
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
    }//Cierre Venta

    public  class IdVenta extends AsyncTask<String,String,String> {
        boolean exito = false;
        String mensaje;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String msj) {
            if(exito){

            }else{
                Toast.makeText(Ventas.this, msj, Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            Connection con = conexion.Conectar();
            if (con != null){
                try {
                    PreparedStatement ps =con.prepareStatement(strings[0]);

                    ps.setDouble(1,total);
                    ps.setString(2, date);

                    ResultSet rs = ps.executeQuery();

                    if(rs.next()){
                        do {
                            idVentaGeneral=rs.getInt("Id_Venta");
                            //mensaje = "Entro a la consulta"+idVentaGeneral;
                        }while (rs.next());

                    }else{
                        mensaje="Venta no exitosa";
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
    }//Cierre IdVenta

    public  class InsertaTienePresencial extends AsyncTask<String,String,String> {
        boolean exito = false;
        String mensaje;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String msj) {
            if(exito){

            }else{
                Toast.makeText(Ventas.this, msj, Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            Connection con = conexion.Conectar();
            if (con != null){
                try {
                    for (int j = 0;idProducto[j] != null ; j++) {
                        PreparedStatement ps =con.prepareStatement(strings[0]);

                        ps.setInt(1,idVentaGeneral);
                        ps.setString(2,idProducto[j]);
                        if(ps.executeUpdate() > 0){

                        }else{
                            mensaje="Error";
                        }
                    }


                   // ResultSet rs = ps.executeQuery();



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
    }//Cierre InsertaTienePresencial

    public  class TablaTiene extends AsyncTask<String,String,String> {
        boolean exito = false;
        String mensaje;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String msj) {
            if(exito){
                /*for (int e = 0; e <idProducto.length ; e++) {
                    idProducto[e] = null;
                }*/
            }else{
                Toast.makeText(Ventas.this, msj, Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            Connection con = conexion.Conectar();
            if (con != null){
                try {
                    for (int j = 0; idProducto[j] != null ; j++) {
                        PreparedStatement ps =con.prepareStatement(strings[0]);
                        ps.setString(1, idProducto[j]);

                        // ResultSet rs = ps.executeQuery();

                        if(ps.executeUpdate() > 0){
                            mensaje = "Exitoso";
                        }else{
                            mensaje="Error";
                        }
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
    }//Cierre InsertaTienePresencial




}