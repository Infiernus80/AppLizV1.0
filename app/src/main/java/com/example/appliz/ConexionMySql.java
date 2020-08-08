package com.example.appliz;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionMySql {
    //10.0.2.2 ip para trabajar de manera local con android
    private String ip = "204.44.192.72:3306"; // Ip de mi mi maquina o del servidor
    private String bd = "creme498_plataforma_ventas";
    private String usuarioBD = "creme498_admin";
    private String passBD = "S@ndoval29";
    private String url = "jdbc:mysql://" + ip + "/" + bd;


    public Connection Conectar() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(url, usuarioBD, passBD);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return con;
    }
}
