package com.florencia.bayteq.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.florencia.bayteq.MainActivity;
import com.florencia.bayteq.R;
import com.florencia.bayteq.apis.UsuarioApi;
import com.florencia.bayteq.models.Usuario;
import com.florencia.bayteq.utils.Constants;
import com.florencia.bayteq.utils.SQLite;
import com.florencia.bayteq.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText etUsuario, etClave;
    Button btnLogin;
    TextView lblNuevo;
    private OkHttpClient okHttpClient;
    private ProgressDialog pbProgreso;
    private static final String TAG = "TAG_LOGINACT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            init();
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    private void init() throws Exception{
        etUsuario = findViewById(R.id.etUsuario);
        etClave = findViewById(R.id.etClave);
        btnLogin = findViewById(R.id.btnLogin);
        lblNuevo = findViewById(R.id.lblNuevoUsuario);
        lblNuevo.setText(Html.fromHtml(getResources().getString(R.string.registrar)));

        pbProgreso = new ProgressDialog(this);

        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                Login(v.getContext());
                break;
            case R.id.lblNuevoUsuario:
                break;
        }
    }

    private void Login(final Context context) {
        try {
            Usuario miUser = new Usuario();

            String User = etUsuario.getText().toString().trim();
            String Clave = etClave.getText().toString();
            if (User.equals("")) {
                Utils.showMessage(context,"Ingrese el usuario");
                return;
            }
            if (Clave.equals("")) {
                Utils.showMessage(context, "Ingrese la contraseña");
                return;
            }

            pbProgreso.setTitle("Iniciando sesión");
            pbProgreso.setMessage("Espere un momento...");
            pbProgreso.setCancelable(false);
            pbProgreso.show();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.WEBSERVICE)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
            UsuarioApi miInterface = retrofit.create(UsuarioApi.class);

            Call<JsonObject> call=null;
            call=miInterface.Login(User,Clave);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if(!response.isSuccessful()){
                        Toast.makeText(context,"Code:" + response.code(),Toast.LENGTH_SHORT).show();
                        pbProgreso.dismiss();
                        return;
                    }
                    try {
                        if (response.body() != null) {
                            JsonObject obj = response.body();
                            if (!obj.get("haserror").getAsBoolean()) {
                                Usuario usuario = new Usuario();
                                JsonObject jsonUsuario = obj.getAsJsonObject("usuario");
                                usuario.idusuario = jsonUsuario.get("idusuario").getAsInt();
                                usuario.usuario = jsonUsuario.get("usuario").getAsString();
                                usuario.clave = etClave.getText().toString();
                                usuario.apellido = jsonUsuario.get("apellido").getAsString();
                                usuario.nombre = jsonUsuario.get("nombre").getAsString();

                                if(usuario.Guardar()) {

                                    SQLite.usuario = usuario;
                                    SQLite.usuario.GuardarSesionLocal(context);
                                    pbProgreso.dismiss();
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }else
                                    Utils.showMessage(context, Constants.MSG_DATOS_NO_GUARDADOS);
                            } else
                                Utils.showMessage(context, obj.get("message").getAsString());
                        } else
                            Utils.showMessage(context, Constants.MSG_USUARIO_CLAVE_INCORRECTO);

                    }catch (JsonParseException ex){
                        Log.d(TAG, ex.getMessage());
                    }
                    pbProgreso.dismiss();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Utils.showMessage(context,t.getMessage());
                    Log.d("TAG", t.getMessage());
                    pbProgreso.dismiss();
                }
            });
        }catch (Exception e){
            pbProgreso.dismiss();
            Log.d(TAG, e.getMessage());
        }
    }
}
