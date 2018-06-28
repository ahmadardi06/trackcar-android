package com.ansyah.ardi.trackcar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ansyah.ardi.trackcar.Api.ApiService;
import com.ansyah.ardi.trackcar.Config.Aplikasi;
import com.ansyah.ardi.trackcar.Config.Utils;
import com.ansyah.ardi.trackcar.Model.RelayModel;
import com.ansyah.ardi.trackcar.Model.UserLoginModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    public static final String PREF_USER_FIRST_TIME = "user_first_time";
    Button btnLogin;
    EditText editEmail, editPassword;
    boolean isUserFirstTime;
    String isIdMobil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        isUserFirstTime = Boolean.valueOf(Utils.readSharedSetting(LoginActivity.this, PREF_USER_FIRST_TIME, "true"));
        isIdMobil = String.valueOf(Utils.readSharedSetting(LoginActivity.this, MainActivity.PREF_USER_ID_MOBIL, ""));

        Intent introIntent = new Intent(LoginActivity.this, PagerActivity.class);
        introIntent.putExtra(PREF_USER_FIRST_TIME, isUserFirstTime);

        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
        loginIntent.putExtra(MainActivity.PREF_USER_ID_MOBIL, isIdMobil);

        if (isUserFirstTime) {
            startActivity(introIntent);
        }
        else {
            if(!isIdMobil.isEmpty())
                startActivity(loginIntent);

            editEmail = (EditText) findViewById(R.id.editEmail);
            editPassword = (EditText) findViewById(R.id.editPassword);

            btnLogin = (Button) findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String emailNya = editEmail.getText().toString();
                    String passNya = editPassword.getText().toString();

                    if (!TextUtils.isEmpty(emailNya) && !TextUtils.isEmpty(passNya)) {

                        Retrofit retro = new Retrofit.Builder().baseUrl(Aplikasi.URL_HOST)
                                .addConverterFactory(GsonConverterFactory.create()).build();
                        ApiService service = retro.create(ApiService.class);
                        Call<UserLoginModel> call = service.setUserLogin(emailNya, passNya);
                        call.enqueue(new Callback<UserLoginModel>() {
                            @Override
                            public void onResponse(Call<UserLoginModel> call, Response<UserLoginModel> response) {
                                if(response.body().getStatus().equals("true")){
                                    Utils.saveSharedSetting(LoginActivity.this, MainActivity.PREF_USER_ID_MOBIL, response.body().get_id());
                                    finish();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, "Email or Password incorrect !!!", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<UserLoginModel> call, Throwable t) {
                                Toast.makeText(LoginActivity.this, "Email or Password incorrect !!!", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(LoginActivity.this, "Email or Password empty !!!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
