package com.florencia.bayteq.models;

public class Usuario {
    public Integer idusuario;
    public String usuario, clave, apellido, nombre;

    public Usuario(){
        this.idusuario = 0;
        this.usuario = "";
        this.clave = "";
        this.apellido = "";
        this.nombre = "";
    }
}
