package com.example.appliz;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
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
import java.util.ArrayList;

public class ModificarProducto extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ConexionMySql conexion;
    Spinner sCategoria, sSubcategoria,sProveedor;
    TextView CategoriaA,SubcategoriaA,txtProveedor;
    EditText CodigoPro, NombrePro, PrecioPro,CategoriaPro,SubCategoriaPro, ExistenciaPro, DescripcionPro;
    Button btnConsultar,btnModificar,btnEscanear;
    String Codigo,Nombre,Categoria,SubCategoria,Descripcion,ProveedorA;
    int Existencia;
    double Precio;
    ArrayList<String> Proveedor = new ArrayList<String>();
    int idproveedor[] = new int[500];
    int IdEmpleado,idselect;

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
        sProveedor = (Spinner) findViewById(R.id.sProveedorMD);
        txtProveedor = (TextView) findViewById(R.id.txtProveedor);
        SubcategoriaA = (TextView) findViewById(R.id.SubcategoriaActual);

        conexion = new ConexionMySql();
        Bundle extra = getIntent().getExtras();
        IdEmpleado = extra.getInt("IdEmpleado");

        System.out.println(IdEmpleado);

        //Se consulta todos los proveedores
        Proveedor proveedor = new Proveedor();
        proveedor.execute("SELECT * FROM Proveedor", "TODO");
        Proveedor.add("Selecciona un proveedor");
        //Se obtine el id del proveedor
        sProveedor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idselect = idproveedor[position];
                System.out.println(idselect);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this,R.array.Categoría,R.layout.simple_spinner_text_item);
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
                consulta.execute("select p.*,pr.Nombre_Agente from Producto as p join Proveedor as pr" +
                        " where p.Id_proveedor = pr.Id_Proveedor and (Id_Producto=? or NombreProd=?)","C");

            }
        });//cierre de boton consultar

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(sCategoria.getSelectedItemPosition());
                System.out.println(sProveedor.getSelectedItemPosition());
                if (validar()){
                    if (sCategoria.getSelectedItemPosition() == 0 && sProveedor.getSelectedItemPosition() ==0){
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
                        modificar.execute("update Producto set NombreProd=?,Existencia=?,Precio=?," +
                                "Descripcion=?,Id_Empleado=?  where Id_Producto=? ","M");

                    }else if(sCategoria.getSelectedItemPosition() != 0 && sProveedor.getSelectedItemPosition()==0){
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
                        modificar.execute("update Producto set NombreProd=?,Categoria=?,SubCategoria=?,Existencia=?,Precio=?," +
                                "Descripcion=?,Id_Empleado=? where Id_Producto=? ","M");
                    }else if (sProveedor.getSelectedItemPosition() != 0 && sCategoria.getSelectedItemPosition()==0){
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
                        modificar.execute("update Producto set NombreProd=?,Existencia=?,Precio=?," +
                                "Descripcion=?,Id_Empleado=?,Id_Proveedor=? where Id_Producto=? ","M");

                    }else if(sCategoria.getSelectedItemPosition() != 0 && sProveedor.getSelectedItemPosition() !=0){
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
                        modificar.execute("update Producto set NombreProd=?,Categoria=?,SubCategoria=?,Existencia=?,Precio=?," +
                                "Descripcion=?,Id_Empleado=?,Id_Proveedor=? where Id_Producto=? ","M");
                    }
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
        int[] subcategorias = {R.array.SubCategoría,R.array.Alimentos,R.array.Abarrotes};
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
                txtProveedor.setText("Proveedor actual: "+ProveedorA);
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
                        ProveedorA = rs.getString("Nombre_Agente");


                    } else {
                        mensaje = "No existe el producto en la base de datos";
                    }
                }  catch (SQLException e) {
                    mensaje = e.getMessage();
                }
                try {
                    con.close();
                } catch (SQLException e) {
                    mensaje = e.getMessage();
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
        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(String msj) {

            if (exito){
                Toast.makeText(ModificarProducto.this, msj, Toast.LENGTH_SHORT).show();
                CodigoPro.setText("");
                NombrePro.setText("");
                PrecioPro.setText("");
                CategoriaA.setText("Categoria actual: ");
                sCategoria.setSelection(0);
                sProveedor.setSelection(0);
                txtProveedor.setText("");
                SubcategoriaA.setText("Subcategoria actual: ");
                ExistenciaPro.setText("");
                DescripcionPro.setText("");
            }else
                Toast.makeText(ModificarProducto.this, msj, Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onPreExecute() {

        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {
            Connection con = conexion.Conectar();
            if (con != null){
                try {
                    PreparedStatement ps =con.prepareStatement(strings[0]);
                    if (strings[1].equals("M")){
                        if (sCategoria.getSelectedItemPosition() == 0 && sProveedor.getSelectedItemPosition()==0){
                            ps.setString(1,Nombre);
                            ps.setInt(2,Existencia);
                            ps.setDouble(3,Precio);
                            ps.setString(4,Descripcion);
                            ps.setInt(5, IdEmpleado);
                            ps.setString(6,Codigo);
                        }else if(sCategoria.getSelectedItemPosition() != 0 && sProveedor.getSelectedItemPosition()==0){
                            ps.setString(1,Nombre);
                            ps.setString(2,Categoria);
                            ps.setString(3,SubCategoria);
                            ps.setInt(4,Existencia);
                            ps.setDouble(5,Precio);
                            ps.setString(6,Descripcion);
                            ps.setInt(7, IdEmpleado);
                            ps.setString(8,Codigo);
                        }else if(sProveedor.getSelectedItemPosition() != 0 && sCategoria.getSelectedItemPosition()==0){
                            ps.setString(1,Nombre);
                            ps.setInt(2,Existencia);
                            ps.setDouble(3,Precio);
                            ps.setString(4,Descripcion);
                            ps.setInt(5, IdEmpleado);
                            ps.setInt(6, idselect);
                            ps.setString(7,Codigo);
                        }else if(sCategoria.getSelectedItemPosition() != 0 && sProveedor.getSelectedItemPosition() !=0){
                            ps.setString(1,Nombre);
                            ps.setString(2,Categoria);
                            ps.setString(3,SubCategoria);
                            ps.setInt(4,Existencia);
                            ps.setDouble(5,Precio);
                            ps.setString(6,Descripcion);
                            ps.setInt(7, IdEmpleado);
                            ps.setInt(8, idselect);
                            ps.setString(9,Codigo);
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

    //Metodo para llenarspinner
    public void llenarspinner(ArrayList listaDatos) {
        ArrayAdapter adaptador = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, listaDatos);
        sProveedor.setAdapter(adaptador);

    }//Cirre para llenar spinner

    //Consultar proveedores
    public class Proveedor extends AsyncTask<String, String, String> {
        boolean exito = false;
        String mensaje;
        int i=1;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String msj) {
            if (exito) {
                llenarspinner(Proveedor);
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
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        exito = true;
                        do {

                            idproveedor[i] =rs.getInt("Id_proveedor");
                            Proveedor.add("Proveedor: " + rs.getString("Nombre_Agente"));
                            i++;
                        } while (rs.next());

                    } else {
                        mensaje = "No hay proveedores";
                    }

                } catch (SQLException e) {
                    mensaje = e.getMessage();
                }
                try {
                    con.close();
                } catch (SQLException e) {
                    mensaje = e.getMessage();
                }
            } else {
                mensaje = "Error al conectar a la base de datos";
            }

            return mensaje;
        }
    }//Cierre Proveedor

    public boolean validar() {
        boolean retorno = true;

        if (CodigoPro.getText().toString().isEmpty()){
            CodigoPro.setError("Ingresa un codigo");
            retorno = false;
        }
        if (NombrePro.getText().toString().isEmpty()){
            NombrePro.setError("Ingresa un Nombre");
            retorno = false;
        }if (ExistenciaPro.getText().toString().isEmpty()){
            ExistenciaPro.setError("Ingresa existencia de producto");
            retorno = false;
        }
        if (PrecioPro.getText().toString().isEmpty()){
            PrecioPro.setError("Ingresa el precio del producto");
            retorno = false;
        }

        return retorno;
    }
}