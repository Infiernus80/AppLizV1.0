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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModificarProducto extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ConexionMySql conexion;
    Spinner sCategoria, sSubcategoria;
    TextView CategoriaA,SubcategoriaA;
    EditText CodigoPro, NombrePro, PrecioPro,CategoriaPro,SubCategoriaPro, ExistenciaPro, DescripcionPro;
    Button btnConsultar,btnModificar,btnEscanear;
    String Codigo,Nombre,Categoria,SubCategoria,Descripcion;
    int Existencia;
    double Precio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_producto);
        CodigoPro = (EditText) findViewById(R.id.etModificarCo);
        NombrePro = (EditText) findViewById(R.id.NomberProMd);
        PrecioPro = (EditText) findViewById(R.id.PrecioProMD);
        sCategoria = (Spinner) findViewById(R.id.spinnerCategoria);
        sSubcategoria = (Spinner) findViewById(R.id.spinnerSubcategoria);
        DescripcionPro = (EditText) findViewById(R.id.DescripcionProMd);
        ExistenciaPro = (EditText) findViewById(R.id.ExistenciaProMd);
        btnConsultar = (Button) findViewById(R.id.btnConsultaPro);
        btnEscanear = (Button) findViewById(R.id.EscanearBtn);
        btnModificar = (Button) findViewById(R.id.btnModificarPro) ;
        CategoriaA = (TextView) findViewById(R.id.CategoriaActual);
        SubcategoriaA = (TextView) findViewById(R.id.SubcategoriaActual);

        conexion = new ConexionMySql();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this,R.array.Categoria,R.layout.simple_spinner_text_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCategoria.setAdapter(adapter);
        sCategoria.setOnItemSelectedListener(ModificarProducto.this);

        btnEscanear.setOnClickListener(EscanearModi);

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CodigoPro.getText().toString().isEmpty()){
                    Codigo= String.valueOf(-1);
                }else{
                    Codigo = CodigoPro.getText().toString();
                }
                Nombre = NombrePro.getText().toString();
                Consulta consulta = new Consulta();
                consulta.execute("select * from producto where Id_Producto=? or NombreProd=?");
            }
        });//cierre de boton consultar

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sCategoria.getSelectedItemPosition() == 0){
                    if (CodigoPro.getText().toString().isEmpty()){
                        Codigo= String.valueOf(-1);
                    }else{
                        Codigo = CodigoPro.getText().toString();
                    }
                    Nombre = NombrePro.getText().toString();
                    Existencia = Integer.parseInt(ExistenciaPro.getText().toString());
                    Precio = Double.parseDouble(PrecioPro.getText().toString());
                    Descripcion = DescripcionPro.getText().toString();
                    Modificar modificar = new Modificar();
                    modificar.execute("update producto set NombreProd=?,Existencia=?,Precio=?," +
                            "Descripcion=? where Id_Producto=? ","M");

                }else{
                    if (CodigoPro.getText().toString().isEmpty()){
                        Codigo= String.valueOf(-1);
                    }else{
                        Codigo = CodigoPro.getText().toString();
                    }
                    Nombre = NombrePro.getText().toString();
                    Categoria = sCategoria.getSelectedItem().toString();
                    SubCategoria = sSubcategoria.getSelectedItem().toString();
                    Existencia = Integer.parseInt(ExistenciaPro.getText().toString());
                    Precio = Double.parseDouble(PrecioPro.getText().toString());
                    Descripcion = DescripcionPro.getText().toString();
                    Modificar modificar = new Modificar();
                    modificar.execute("update producto set NombreProd=?,Categoria=?,SubCategoria=?,Existencia=?,Precio=?," +
                            "Descripcion=? where Id_Producto=? ","M");
                }
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
                CodigoPro.setText(result.getContents());
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
                case R.id.EscanearBtn:
                    new IntentIntegrator(ModificarProducto.this).initiateScan();

            }
        }
    };//Termina metodo del boton escanear

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int[] subcategorias = {R.array.SubCategoria,R.array.Alimentos,R.array.Abarrotes};
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this,subcategorias[position],R.layout.simple_spinner_text_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSubcategoria.setAdapter(adapter);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class Consulta extends AsyncTask<String, String, String> {
        String mensaje = "";
        boolean exito = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String msj) {
            if (exito) {
                CodigoPro.setText(Codigo + "");
                NombrePro.setText(Nombre);
                CategoriaA.setText("Categoria actual: "+Categoria);
                SubcategoriaA.setText("Subcategoria actual: "+SubCategoria);

                PrecioPro.setText(Precio+"");
                DescripcionPro.setText(Descripcion + "");
                ExistenciaPro.setText(Existencia + "");
            } else {
                Toast.makeText(ModificarProducto.this, msj, Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected String doInBackground(String... strings) {
            Connection con = conexion.Conectar();
            if (con != null) {
                try {
                    PreparedStatement ps = con.prepareStatement(strings[0]);
                    ps.setString(1, Codigo);
                    ps.setString(2, Nombre);

                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        exito = true;
                        Codigo = rs.getString("Id_Producto");
                        Nombre = rs.getString("NombreProd");
                        Precio = rs.getDouble("Precio");
                        Categoria = rs.getString("Categoria");
                        SubCategoria =rs.getString("SubCategoria");
                        Existencia = rs.getInt("Existencia");
                        Descripcion = rs.getString("Descripcion");

                    } else {

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                mensaje = "Error a conectar a la base de datos";
            }
            return mensaje;
        }
    }//Cierre de la subclase consulta

    public class Modificar extends  AsyncTask<String,String,String>{
        String mensaje="";
        boolean exito = false;
        @Override
        protected void onPostExecute(String msj) {

            if (exito){
                Toast.makeText(ModificarProducto.this, msj, Toast.LENGTH_SHORT).show();
                CodigoPro.setText("");
                NombrePro.setText("");
                PrecioPro.setText("");
                CategoriaA.setText("Categoria actual: ");
                SubcategoriaA.setText("Subcategoria actual: ");
                ExistenciaPro.setText("");
                DescripcionPro.setText("");
            }else
                Toast.makeText(ModificarProducto.this, msj, Toast.LENGTH_SHORT).show();

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
                    if (strings[1].equals("M")){
                        if (sCategoria.getSelectedItemPosition() == 0){
                            ps.setString(1,Nombre);
                            ps.setInt(2,Existencia);
                            ps.setDouble(3,Precio);
                            ps.setString(4,Descripcion);
                            ps.setString(5,Codigo);
                        }else{
                            ps.setString(1,Nombre);
                            ps.setString(2,Categoria);
                            ps.setString(3,SubCategoria);
                            ps.setInt(4,Existencia);
                            ps.setDouble(5,Precio);
                            ps.setString(6,Descripcion);
                            ps.setString(7,Codigo);
                        }

                    }
                    if(ps.executeUpdate() > 0){
                        exito = true;
                        if (strings[1].equals("M"))
                            mensaje = "Registro modificado";
                    }
                    else {
                        if (strings[1].equals("M")) ;
                        mensaje = "Registro no modificado";
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
}