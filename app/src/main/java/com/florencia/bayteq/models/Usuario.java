package com.florencia.bayteq.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.florencia.bayteq.utils.SQLite;
import com.florencia.bayteq.utils.Utils;

import static android.content.Context.MODE_PRIVATE;

public class Usuario {
    public Integer idusuario;
    public String usuario, clave, apellido, nombre;

    public static SQLiteDatabase sqLiteDatabase;

    public Usuario(){
        this.idusuario = 0;
        this.usuario = "";
        this.clave = "";
        this.apellido = "";
        this.nombre = "";
    }

    public boolean Guardar(){
        try {
            sqLiteDatabase = SQLite.sqlDB.getWritableDatabase();
            sqLiteDatabase.execSQL("INSERT OR REPLACE INTO " +
                            "usuario(idusuario, usuario, clave, apellido, nombre) " +
                            "values(?, ?, ?, ?, ?)",
                    new String[]{
                            this.idusuario.toString(),
                            this.usuario,
                            this.clave,
                            this.nombre,
                            this.apellido,
                            this.nombre
                    }
            );
            sqLiteDatabase.close();
            Log.d("TAG", "USUARIO INGRESADO LOCALMENTE");
            return true;
        } catch (SQLException ex){
            Log.d("TAG", ex.getMessage());
            return false;
        }
    }

    public boolean GuardarSesionLocal (Context context){
        //Crea preferencia
        SharedPreferences sharedPreferences= context.getSharedPreferences("DatosSesion", MODE_PRIVATE);
        String conexionactual = sharedPreferences.getString("conexionactual","");
        SharedPreferences.Editor editor=sharedPreferences.edit()
                .putInt("idUsuario", this.idusuario)
                .putString("usuario", this.usuario)
                .putString("ultimaconexion", conexionactual)
                .putString("conexionactual", Utils.getDateFormat("yyyy-MM-dd HH:mm:ss"));
        return editor.commit();
    }

    public boolean CerrarSesionLocal (Context context){
        //Crea preferencia
        SharedPreferences sharedPreferences= context.getSharedPreferences("DatosSesion", MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit()
                .clear();
        return editor.commit();
    }
}
