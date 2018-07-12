package com.ansyah.ardi.trackcar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ansyah.ardi.trackcar.Api.ApiService;
import com.ansyah.ardi.trackcar.Config.Aplikasi;
import com.ansyah.ardi.trackcar.Config.Utils;
import com.ansyah.ardi.trackcar.Model.MobilObjek;
import com.ansyah.ardi.trackcar.Model.RelayModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InfoActivity extends AppCompatActivity {

    TextView policeNumber, owner, typeOfVehicle, vehicleBrand, emailAddress;
    TextView gps, engine, lamp, door;
    String isIdMobil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setTitle("Information");

        isIdMobil = String.valueOf(Utils.readSharedSetting(this, MainActivity.PREF_USER_ID_MOBIL, ""));

        policeNumber    = (TextView) findViewById(R.id.txtInfoPoliceNumber);
        owner           = (TextView) findViewById(R.id.txtInfoOwner);
        typeOfVehicle   = (TextView) findViewById(R.id.txtInfoTypeFfVehicle);
        vehicleBrand    = (TextView) findViewById(R.id.txtInfoVehicleBrand);
        emailAddress    = (TextView) findViewById(R.id.txtInfoEmailAddress);

        gps     = (TextView) findViewById(R.id.txtInfoGps);
        engine  = (TextView) findViewById(R.id.txtInfoEngineMachine);
        lamp    = (TextView) findViewById(R.id.txtInfoLight);
        door    = (TextView) findViewById(R.id.txtInfoHorn);

        Retrofit retro = new Retrofit.Builder().baseUrl(Aplikasi.URL_HOST)
                .addConverterFactory(GsonConverterFactory.create()).build();
        ApiService service = retro.create(ApiService.class);
        Call<MobilObjek> call = service.getMobil(isIdMobil);
        call.enqueue(new Callback<MobilObjek>() {
            @Override
            public void onResponse(Call<MobilObjek> call, Response<MobilObjek> response) {
                if(response.isSuccessful()){
                    policeNumber.setText(response.body().getNopol());
                    owner.setText(response.body().getPemilik());
                    typeOfVehicle.setText(response.body().getJenis());
                    vehicleBrand.setText(response.body().getMerk());
                    emailAddress.setText(response.body().getEmail());

                    gps.setText(String.valueOf(response.body().isGps()));
                    engine.setText(String.valueOf(response.body().isEngine()));
                    lamp.setText(String.valueOf(response.body().isLamp()));
                    door.setText(String.valueOf(response.body().isDoor()));
                }
            }

            @Override
            public void onFailure(Call<MobilObjek> call, Throwable t) {

            }
        });
    }
}
