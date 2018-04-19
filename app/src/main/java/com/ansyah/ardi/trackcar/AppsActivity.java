package com.ansyah.ardi.trackcar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ansyah.ardi.trackcar.Api.ApiService;
import com.ansyah.ardi.trackcar.Config.Aplikasi;
import com.ansyah.ardi.trackcar.Model.DriverModel;
import com.ansyah.ardi.trackcar.Model.RelayModel;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppsActivity extends AppCompatActivity {

    final Context context = this;
    Bitmap imageBitmap;

    ImageView imgAppsRespon, imgAppsDriver;
    ToggleButton btnGps, btnAlarm, btnLock, btnLights, btnEngine;
    Button btnCamera, btnMaps, btnDriver;
    TextView txtDrivers;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Aplikasi.URL_HOST);
        }catch (URISyntaxException e){
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        setTitle("Remote Your Car");

        mSocket.on("statuslampu", onStatusLampu);
        mSocket.on("statusengine", onStatusengine);
        mSocket.on("statusdoor", onStatusDoor);
        mSocket.on("statusalarm", onStatusAlarm);
        mSocket.on("statusgps", onStatusGps);
        mSocket.on("takefoto", onTakeFoto);
        mSocket.connect();

        Retrofit retro = new Retrofit.Builder().baseUrl(Aplikasi.URL_HOST)
                .addConverterFactory(GsonConverterFactory.create()).build();
        ApiService service = retro.create(ApiService.class);
        Call<RelayModel> call = service.getRelay(Aplikasi.ID_MOBIL);
        call.enqueue(new Callback<RelayModel>() {
            @Override
            public void onResponse(Call<RelayModel> call, Response<RelayModel> response) {
                if(response.isSuccessful()){
                    btnLock = (ToggleButton) findViewById(R.id.btnAppsLock);
                    if(response.body().getDoor()){
                        btnLock.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lock_outline_hijau), null, null);
                        btnLock.setTextColor(getResources().getColor(R.color.colorHijau));
                    }else{
                        btnLock.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lock_open_black_24dp), null, null);
                        btnLock.setTextColor(getResources().getColor(R.color.colorTulisan));
                    }

                    btnLights = (ToggleButton) findViewById(R.id.btnAppsLights);
                    if(response.body().getLamp()){
                        btnLights.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lamp_on), null, null);
                        btnLights.setTextColor(getResources().getColor(R.color.colorHijau));
                    }else{
                        btnLights.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lamp_black_24dp), null, null);
                        btnLights.setTextColor(getResources().getColor(R.color.colorTulisan));
                    }

                    btnEngine = (ToggleButton) findViewById(R.id.btnAppsEngine);
                    if(response.body().getEngine()){
                        btnEngine.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_flash_on_hijau), null, null);
                        btnEngine.setTextColor(getResources().getColor(R.color.colorHijau));
                    }else{
                        btnEngine.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_flash_on_black_24dp), null, null);
                        btnEngine.setTextColor(getResources().getColor(R.color.colorTulisan));
                    }

                    btnAlarm = (ToggleButton) findViewById(R.id.btnAppsAlarm);
                    if(response.body().getAlarm()){
                        btnAlarm.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_access_alarm_hijau), null, null);
                        btnAlarm.setTextColor(getResources().getColor(R.color.colorHijau));
                    }else{
                        btnAlarm.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_access_alarm_black_24dp), null, null);
                        btnAlarm.setTextColor(getResources().getColor(R.color.colorTulisan));
                    }
                }
            }

            @Override
            public void onFailure(Call<RelayModel> call, Throwable t) {

            }
        });

        btnLights = (ToggleButton) findViewById(R.id.btnAppsLights);
        btnLights.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("msg", true);
                        obj1.put("idmobil", Aplikasi.ID_MOBIL);
                    }catch (JSONException e) {
                        return;
                    }
                    btnLights.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lamp_on), null, null);
                    btnLights.setTextColor(getResources().getColor(R.color.colorHijau));
                    mSocket.emit("statuslampu", obj1);
                }
                else{
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("msg", false);
                        obj1.put("idmobil", Aplikasi.ID_MOBIL);
                    }catch (JSONException e) {
                        return;
                    }
                    btnLights.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lamp_black_24dp), null, null);
                    btnLights.setTextColor(getResources().getColor(R.color.colorTulisan));
                    mSocket.emit("statuslampu", obj1);
                }
            }
        });

        btnEngine = (ToggleButton) findViewById(R.id.btnAppsEngine);
        btnEngine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("msg", true);
                        obj1.put("idmobil", Aplikasi.ID_MOBIL);
                    }catch (JSONException e) {
                        return;
                    }
                    btnEngine.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_flash_on_hijau), null, null);
                    btnEngine.setTextColor(getResources().getColor(R.color.colorHijau));
                    mSocket.emit("statusengine", obj1);
                }
                else{
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("msg", false);
                        obj1.put("idmobil", Aplikasi.ID_MOBIL);
                    }catch (JSONException e) {
                        return;
                    }
                    btnEngine.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_flash_on_black_24dp), null, null);
                    btnEngine.setTextColor(getResources().getColor(R.color.colorTulisan));
                    mSocket.emit("statusengine", obj1);
                }
            }
        });

        btnMaps = (Button) findViewById(R.id.btnAppsMap);
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ngint = new Intent(AppsActivity.this, MapsActivity.class);
                startActivity(ngint);
            }
        });

        btnLock = (ToggleButton) findViewById(R.id.btnAppsLock);
        btnLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("msg", true);
                        obj1.put("idmobil", Aplikasi.ID_MOBIL);
                    }catch (JSONException e) {
                        return;
                    }
                    btnLock.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lock_outline_hijau), null, null);
                    btnLock.setTextColor(getResources().getColor(R.color.colorHijau));
                    mSocket.emit("statusdoor", obj1);
                }
                else{
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("msg", false);
                        obj1.put("idmobil", Aplikasi.ID_MOBIL);
                    }catch (JSONException e) {
                        return;
                    }
                    btnLock.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lock_open_black_24dp), null, null);
                    btnLock.setTextColor(getResources().getColor(R.color.colorTulisan));
                    mSocket.emit("statusdoor", obj1);
                }
            }
        });

        btnAlarm = (ToggleButton) findViewById(R.id.btnAppsAlarm);
        btnAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("msg", true);
                        obj1.put("idmobil", Aplikasi.ID_MOBIL);
                    }catch (JSONException e) {
                        return;
                    }
                    btnAlarm.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_access_alarm_hijau), null, null);
                    btnAlarm.setTextColor(getResources().getColor(R.color.colorHijau));
                    mSocket.emit("statusalarm", obj1);
                }
                else{
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("msg", false);
                        obj1.put("idmobil", Aplikasi.ID_MOBIL);
                    }catch (JSONException e) {
                        return;
                    }
                    btnAlarm.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_access_alarm_black_24dp), null, null);
                    btnAlarm.setTextColor(getResources().getColor(R.color.colorTulisan));
                    mSocket.emit("statusalarm", obj1);
                }
            }
        });

        btnGps = (ToggleButton) findViewById(R.id.btnAppsGpss);
        btnGps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("msg", true);
                        obj1.put("idmobil", Aplikasi.ID_MOBIL);
                    }catch (JSONException e) {
                        return;
                    }
                    btnGps.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_my_location_hijau), null, null);
                    btnGps.setTextColor(getResources().getColor(R.color.colorHijau));
                    mSocket.emit("statusgps", obj1);
                }
                else{
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("msg", false);
                        obj1.put("idmobil", Aplikasi.ID_MOBIL);
                    }catch (JSONException e) {
                        return;
                    }
                    btnGps.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_my_location_black_24dp), null, null);
                    btnGps.setTextColor(getResources().getColor(R.color.colorTulisan));
                    mSocket.emit("statusgps", obj1);
                }
            }
        });

        btnCamera = (Button) findViewById(R.id.btnAppsCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sekarang = new SimpleDateFormat("ddMMyyyy-HH-mm").format(Calendar.getInstance().getTime());
                JSONObject obj1 = new JSONObject();
                try{
                    obj1.put("msg", "takefoto"+sekarang);
                    obj1.put("idmobil", Aplikasi.ID_MOBIL);
                }catch (JSONException e){
                    return;
                }
                btnCamera.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_linked_camera_hijau), null,null);
                btnCamera.setTextColor(getResources().getColor(R.color.colorHijau));
                mSocket.emit("takefoto", obj1);
            }
        });

        btnDriver = (Button) findViewById(R.id.btnAppsDriver);
        btnDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Retrofit retro = new Retrofit.Builder().baseUrl(Aplikasi.URL_HOST)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                ApiService service = retro.create(ApiService.class);
                Call<List<DriverModel>> call = service.getDriver(Aplikasi.ID_MOBIL);
                call.enqueue(new Callback<List<DriverModel>>() {
                    @Override
                    public void onResponse(Call<List<DriverModel>> call, Response<List<DriverModel>> response) {
                        if(response.isSuccessful()) {

                            List<DriverModel> dataDriver = response.body();

                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.custom_driver);

                            imgAppsDriver = (ImageView) dialog.findViewById(R.id.imageViewDrivers);
                            txtDrivers = (TextView) dialog.findViewById(R.id.textViewDrivers);

                            String urlFoto = dataDriver.get(dataDriver.size()-1).getGambarFull();
                            new ImageLoaderClass().execute(urlFoto);

                            txtDrivers.setText(dataDriver.get(dataDriver.size()-1).getTanggal());

                            Button btnClose = (Button) dialog.findViewById(R.id.buttonCloseDialog);
                            btnClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

                            btnDriver.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_picture_in_picture_hijau), null, null);
                            btnDriver.setTextColor(getResources().getColor(R.color.colorHijau));

                            dialog.show();
                        }
                        else{
                            Log.d("onResponse", "Response Gagal");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<DriverModel>> call, Throwable t) {

                    }
                });
            }
        });
    }

    private class ImageLoaderClass extends AsyncTask<String, String, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                imageBitmap = BitmapFactory.decodeStream((InputStream) new URL(strings[0]).getContent());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return imageBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imgAppsDriver.setImageBitmap(bitmap);
        }
    };

    private Emitter.Listener onStatusLampu  = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String msg;
                    try {
                        msg = data.getString("msg");
                    } catch (JSONException e) {
                        return;
                    }
                    Log.e("status lampu", msg);
                    imgAppsRespon = (ImageView) findViewById(R.id.imgAppsRespon);
                    if(msg == "true"){
                        imgAppsRespon.setImageResource(R.drawable.kondisireadyoff);
                    }
                    else{
                        imgAppsRespon.setImageResource(R.drawable.kondisireadyon);
                    }
                }
            });
        }
    };

    private Emitter.Listener onStatusengine  = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String msg;
                    try {
                        msg = data.getString("msg");
                    } catch (JSONException e) {
                        return;
                    }
                    Log.e("status engine", msg);
                    imgAppsRespon = (ImageView) findViewById(R.id.imgAppsRespon);
                    if(msg == "true"){
                        imgAppsRespon.setImageResource(R.drawable.kondisireadyoff);
                    }
                    else{
                        imgAppsRespon.setImageResource(R.drawable.kondisireadyon);
                    }
                }
            });
        }
    };

    private Emitter.Listener onStatusDoor  = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String msg;
                    try {
                        msg = data.getString("msg");
                    } catch (JSONException e) {
                        return;
                    }
                    Log.e("status door", msg);
                    imgAppsRespon = (ImageView) findViewById(R.id.imgAppsRespon);
                    if(msg == "true"){
                        imgAppsRespon.setImageResource(R.drawable.kondisireadyoff);
                    }
                    else{
                        imgAppsRespon.setImageResource(R.drawable.kondisireadyon);
                    }
                }
            });
        }
    };

    private Emitter.Listener onStatusAlarm  = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String msg;
                    try {
                        msg = data.getString("msg");
                    } catch (JSONException e) {
                        return;
                    }
                    Log.e("status alarm", msg);
                    imgAppsRespon = (ImageView) findViewById(R.id.imgAppsRespon);
                    if(msg == "true"){
                        imgAppsRespon.setImageResource(R.drawable.kondisireadyoff);
                    }
                    else{
                        imgAppsRespon.setImageResource(R.drawable.kondisireadyon);
                    }
                }
            });
        }
    };

    private Emitter.Listener onStatusGps  = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String msg;
                    try {
                        msg = data.getString("msg");
                    } catch (JSONException e) {
                        return;
                    }
                    Log.e("status gps", msg);
                    imgAppsRespon = (ImageView) findViewById(R.id.imgAppsRespon);
                    if(msg == "true"){
                        imgAppsRespon.setImageResource(R.drawable.kondisireadyoff);
                    }
                    else{
                        imgAppsRespon.setImageResource(R.drawable.kondisireadyon);
                    }
                }
            });
        }
    };

    private Emitter.Listener onTakeFoto  = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String msg;
                    try {
                        msg = data.getString("msg");
                    } catch (JSONException e) {
                        return;
                    }
                    Log.e("take foto", msg);
                    imgAppsRespon = (ImageView) findViewById(R.id.imgAppsRespon);
                    if(msg == "true"){
                        imgAppsRespon.setImageResource(R.drawable.kondisireadyoff);
                    }
                    else{
                        imgAppsRespon.setImageResource(R.drawable.kondisireadyon);
                    }
                }
            });
        }
    };

}
