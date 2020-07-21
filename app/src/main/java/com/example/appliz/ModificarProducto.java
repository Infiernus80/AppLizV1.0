package com.example.appliz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModificarProducto extends AppCompatActivity {
    ConexionMySql conexion;

    EditText CodigoPro, NombrePro, PrecioPro,CategoriaPro,SubCategoriaPro, ExistenciaPro, DescripcionPro;
    Button btnConsultar,btnModificar;
    String Codigo,Nombre,Categoria,SubCategoria,Descripcion;
    int Existencia;
    double Precio;

/*   int pos = CategoriaSP.getSelectedItemPosition();

    int opcion = 0;
    String Alimentos[] = {"Seleccione un SubMenu", "Quesos y lacteos", "Carnes frias y embutidos", "Bebidas y frituras" +
            "Reposteria"};
    String Abarrotes[] = {"Seleccione un SubMenu", "Hogar y limpieza", "Salud y cuidado personal", "Semillas y cerelas"
            + "Productos diversos"};

    String Opcion[] = {"Selecciona una opción"};
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_producto);
        CodigoPro = (EditText) findViewById(R.id.etModificarCo);
        NombrePro = (EditText) findViewById(R.id.NomberProMd);
        PrecioPro = (EditText) findViewById(R.id.PrecioProMD);
        CategoriaPro = (EditText) findViewById(R.id.etCategoriaAg);
        SubCategoriaPro = (EditText) findViewById(R.id.etSubCategoriaAg);
        DescripcionPro = (EditText) findViewById(R.id.DescripcionProMd);
        ExistenciaPro = (EditText) findViewById(R.id.ExistenciaProMd);
        btnConsultar = (Button) findViewById(R.id.btnConsultaPro);


        conexion = new ConexionMySql();
        //ComprobarCategoria();


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
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_EscanearAg:
                    new IntentIntegrator(ModificarProducto.this).initiateScan();

            }
        }
    };//Termina metodo del boton escanear

    /*public void ComprobarCategoria() {
        switch (pos) {
            case 0:
                SubMenuSP.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item, Opcion));
                break;
            case 1:
                SubMenuSP.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item, Alimentos));
                break;
            case 2:
                SubMenuSP.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item, Abarrotes));
                break;
        }
    }*/

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
                PrecioPro.setText(Precio+"");
                CategoriaPro.setText(Categoria);
                SubCategoriaPro.setText(SubCategoria);
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
}