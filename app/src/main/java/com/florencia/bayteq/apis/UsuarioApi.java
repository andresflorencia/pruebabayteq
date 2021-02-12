package com.florencia.bayteq.apis;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UsuarioApi {

    @FormUrlEncoded
    @POST("login")
    Call<JsonObject> Login(@Field("usuario") String user, @Field("clave") String clave);
}
