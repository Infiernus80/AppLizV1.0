package com.example.appliz;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AgregarProducto extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ConexionMySql conexion;
    Button btn_Escanear, btn_Agregar;
    Spinner sCategoria, sSubcategoria, sProvedores;
    EditText etCodigoAg, etNombreAg, etPrecioAg,etExistenciaAg, etDescripcionAg;
    String Codigo, Nombre, Categoria, SubCategoria, Descripcion;
    int Existencia,pos;
    double Precio;
    ArrayList<String> Proveedor = new ArrayList<String>();
    int idproveedor[] = new int[500];
    int IdEmpleado,idselect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        btn_Escanear = findViewById(R.id.btn_EscanearAg);
        btn_Agregar = findViewById(R.id.btn_Agregar);
        etCodigoAg = findViewById(R.id.etCodigoAg);
        etNombreAg = findViewById(R.id.etNombreAg);
        etPrecioAg = findViewById(R.id.etPrecioAg);
        sCategoria = findViewById(R.id.sCategoria);
        sProvedores = findViewById(R.id.sProvedores);
        etExistenciaAg = findViewById(R.id.etExistenciaAg);
        etDescripcionAg = findViewById(R.id.etDescripcionAg);
        sSubcategoria = findViewById(R.id.sSubcategoria);
        conexion = new ConexionMySql();
        //Se obtine el ID del empleado
        Bundle extra = getIntent().getExtras();
        IdEmpleado = extra.getInt("IdEmpleado");
        sProvedores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idselect = idproveedor[position];
                System.out.println(idselect);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Se consulta todos los proveedores
        Proveedor proveedor = new Proveedor();
        proveedor.execute("select * from Proveedor", "TODO");
        Proveedor.add("Selecciona un proveedor");
        pos = sProvedores.getSelectedItemPosition();

        //Metodo para spinner enlazados(Categoria)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.Categoría, R.layout.simple_spinner_text_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCategoria.setAdapter(adapter);
        sCategoria.setOnItemSelectedListener(AgregarProducto.this);

        btn_Escanear.setOnClickListener(mOnClickListener);
        //Metodo Agregar producto
        btn_Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validar()){
                    Codigo = etCodigoAg.getText().toString();
                    Nombre = etNombreAg.getText().toString();
                    Precio = Double.parseDouble(etPrecioAg.getText().toString());
                    Categoria = sCategoria.getSelectedItem().toString();
                    SubCategoria = sSubcategoria.getSelectedItem().toString();
                    Existencia = Integer.parseInt(etExistenciaAg.getText().toString());
                    Descripcion = etDescripcionAg.getText().toString();


                    Agregar agregar = new Agregar();
                    agregar.execute("insert into Producto (Id_Producto,NombreProd,Categoria," +
                            "SubCategoria,existencia,Precio,descripcion,Id_Empleado,Id_Proveedor) values (?,?,?,?,?,?,?,?,?)", "g");
                }

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

    //Metodo para agregar productos
    public class Agregar extends AsyncTask<String, String, String> {
        String mensaje = "";
        boolean exito = false;

        @Override
        protected void onPostExecute(String msj) {
            Toast.makeText(AgregarProducto.this, msj, Toast.LENGTH_LONG).show();
            if (exito) {
                etCodigoAg.setText("");
                etNombreAg.setText("");
                etPrecioAg.setText("");
                sCategoria.setSelection(0);
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
            if (con != null) {
                try {
                    PreparedStatement ps = con.prepareStatement(strings[0]);
                    if (strings[1].equals("g")) {
                        ps.setString(1, Codigo);
                        ps.setString(2, Nombre);
                        ps.setString(3, Categoria);
                        ps.setString(4, SubCategoria);
                        ps.setInt(5, Existencia);
                        ps.setDouble(6, Precio);
                        ps.setString(7, Descripcion);
                        ps.setInt(8, IdEmpleado);
                        ps.setInt(9, idselect);
                    }
                    if (ps.executeUpdate() > 0) {
                        exito = true;
                        if (strings[1].equals("g"))
                            mensaje = "Registro guardado";
                    } else {
                        if (strings[1].equals("g")) ;
                        mensaje = "Registro no guardado";
                    }
                } catch (Exception e) {
                    mensaje = e.getMessage();
                }
                try {
                    con.close();
                } catch (SQLException e) {
                    mensaje = e.getMessage();
                }
            } else {
                mensaje = "Error al conectar con la base de datos";
            }

            return mensaje;
        }
    }//cierre de clase agregarProducto

    //Metodo para llenarspinner
    public void llenarspinner(ArrayList listaDatos) {
        ArrayAdapter adaptador = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, listaDatos);
        sProvedores.setAdapter(adaptador);
        pos = sProvedores.getSelectedItemPosition();

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
                Toast.makeText(AgregarProducto.this, msj, Toast.LENGTH_SHORT).show();
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

    //Metodo para Spinner enlazados (Subcategoria)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int[] subcategorias = {R.array.SubCategoría, R.array.Alimentos, R.array.Abarrotes};

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, subcategorias[position], R.layout.simple_spinner_text_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSubcategoria.setAdapter(adapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public boolean validar() {
        boolean retorno = true;
        String Codigo = etCodigoAg.getText().toString();
        String Nombre = etNombreAg.getText().toString();
        Double Precio = Double.parseDouble(etPrecioAg.getText().toString());
        String Categoria = sCategoria.getSelectedItem().toString();
        String SubCategoria = sSubcategoria.getSelectedItem().toString();
        int Existencia = Integer.parseInt(etExistenciaAg.getText().toString());



        if (Codigo.isEmpty()) {
            etCodigoAg.setError("Por favor ingresa un codigo");
            retorno = false;
        }
        if (Nombre.isEmpty()) {
            etNombreAg.setError("Por favor ingresa el nombre del producto");
            retorno = false;
        }if (Precio <= 0){
            etPrecioAg.setError("Por favor ingresa un valor");
            retorno = false;
        }if (Categoria.isEmpty()) {
            Toast.makeText(this, "Por favor selecciona una categoría", Toast.LENGTH_SHORT).show();
            retorno = false;
        }if (SubCategoria.isEmpty()) {
            Toast.makeText(this, "Por favor selecciona una subcategoría", Toast.LENGTH_SHORT).show();
            retorno = false;
        }if (Existencia <= 0 ) {
            etExistenciaAg.setError("Por favor ingresa un valor");
            retorno = false;
        }
        //Falta validacion de proveedor

        return retorno;
    }


}//Fin de la clase
