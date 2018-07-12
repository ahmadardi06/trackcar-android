package com.ansyah.ardi.trackcar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ansyah.ardi.trackcar.Api.ApiService;
import com.ansyah.ardi.trackcar.Config.Aplikasi;
import com.ansyah.ardi.trackcar.Config.Utils;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ansyah.ardi.trackcar.MainActivity.PREF_USER_ID_MOBIL;

public class AppsActivity extends AppCompatActivity {

    Bitmap imageBitmap;
    ImageView imgAppsRespon, imgAppsDriver;
    ToggleButton btnAppsGps, btnAppsLock, btnAppsLights, btnAppsEngine;
    Button btnAppsMaps, btnAppsCamera, btnAppsDriver, btnStartEngine;
    String isIdMobil;

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        setTitle("Remote the Car");

        isIdMobil = String.valueOf(Utils.readSharedSetting(this, PREF_USER_ID_MOBIL, ""));
        Log.d("idMobil", isIdMobil);

        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.query = "idMobil=" + isIdMobil;
            mSocket = IO.socket(Aplikasi.URL_HOST, opts);
        }catch (URISyntaxException e){}

        mSocket.on("statuslampu", onStatusLampu);
        mSocket.on("statusdoor", onStatusDoor);
        mSocket.on("statusengine", onStatusengine);
        mSocket.on("statusalarm", onStatusAlarm);
        mSocket.on("statusgps", onStatusGps);
        mSocket.on("takefoto", onTakeFoto);
        mSocket.connect();

        btnAppsGps          = (ToggleButton) findViewById(R.id.btnAppsGpss);
        btnAppsLock         = (ToggleButton) findViewById(R.id.btnAppsLock);
        btnAppsLights       = (ToggleButton) findViewById(R.id.btnAppsLights);
        btnAppsEngine       = (ToggleButton) findViewById(R.id.btnAppsEngine);
        btnStartEngine      = (Button) findViewById(R.id.btnStartEngine);
        btnAppsMaps         = (Button) findViewById(R.id.btnAppsMap);
        btnAppsCamera       = (Button) findViewById(R.id.btnAppsCamera);
        btnAppsDriver       = (Button) findViewById(R.id.btnAppsDriver);

        cekRelayStatus();

        btnStartEngine.setOnTouchListener(oStartEngine);

        btnAppsMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(AppsActivity.this, MapsActivity.class));
                startActivity(new Intent(AppsActivity.this, KoordinatActivity.class));
            }
        });

        btnAppsCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showKonfirmasiButton("Are you sure to take picture?", btnAppsCamera);
            }
        });


        btnAppsDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AppsActivity.this, DriverActivity.class));
            }
        });

    }

    private void cekRelayStatus() {
        Retrofit retro = new Retrofit.Builder().baseUrl(Aplikasi.URL_HOST)
                .addConverterFactory(GsonConverterFactory.create()).build();
        ApiService service = retro.create(ApiService.class);
        Call<RelayModel> call = service.getRelay(isIdMobil);
        call.enqueue(new Callback<RelayModel>() {
            @Override
            public void onResponse(Call<RelayModel> call, Response<RelayModel> response) {
                if(response.isSuccessful()){
                    btnAppsLights = (ToggleButton) findViewById(R.id.btnAppsLights);
                    if(!response.body().getLamp()){
                        setOnOffToggle(btnAppsLights, R.drawable.ic_lamp_hijau, R.color.colorHijau, true, "Light");
                    }else{
                        setOnOffToggle(btnAppsLights, R.drawable.ic_lamp_hitam, R.color.colorTulisan, false, "Light");
                    }

                    btnAppsLock = (ToggleButton) findViewById(R.id.btnAppsLock);
                    if(!response.body().getDoor()){
                        setOnOffToggle(btnAppsLock, R.drawable.ic_horn_hijau, R.color.colorHijau, true, "Horn");
                    }else{
                        setOnOffToggle(btnAppsLock, R.drawable.ic_horn_hitam, R.color.colorTulisan, false, "Horn");
                    }

                    btnAppsEngine = (ToggleButton) findViewById(R.id.btnAppsEngine);
                    if(!response.body().getEngine()){
                        setOnOffToggle(btnAppsEngine, R.drawable.ic_power_hijau, R.color.colorHijau, true, "Power");
                    }else{
                        setOnOffToggle(btnAppsEngine, R.drawable.ic_power_hitam, R.color.colorTulisan, false, "Power");
                    }

                    btnAppsGps = (ToggleButton) findViewById(R.id.btnAppsGpss);
                    if(response.body().getGps()){
                        setOnOffToggle(btnAppsGps, R.drawable.ic_gps_hijau, R.color.colorHijau, true, "GPS");
                    }else{
                        setOnOffToggle(btnAppsGps, R.drawable.ic_gps_hitam, R.color.colorTulisan, false, "GPS");
                    }
                }
            }

            @Override
            public void onFailure(Call<RelayModel> call, Throwable t) {

            }
        });
    }

    private void setOnOffToggle(final ToggleButton tg, int draw, int warna, Boolean b, String label) {
        tg.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(draw), null, null);
        tg.setTextColor(getResources().getColor(warna));
        tg.setChecked(b);
        if(b)
            tg.setTextOn(label);
        else
            tg.setTextOff(label);

        tg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showKonfirmasiToggle("Are you sure ?", tg, b);
            }
        });
    }

    private void showKonfirmasiButton(String message, final Button btn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AppsActivity.this);
        builder.setTitle("Confirmation");
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (btn.getId()) {
                    case R.id.btnAppsCamera:
                        String sekarang = new SimpleDateFormat("ddMMyyyy-HH-mm").format(Calendar.getInstance().getTime());
                        JSONObject objek = new JSONObject();
                        try{
                            objek.put("msg", "takefoto"+sekarang);
                            objek.put("idmobil", isIdMobil);
                        }catch (JSONException e){
                            return;
                        }
                        btnAppsCamera.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_camera_hijau), null,null);
                        btnAppsCamera.setTextColor(getResources().getColor(R.color.colorHijau));
                        mSocket.emit("takefoto", objek);
                        Toast.makeText(AppsActivity.this, "Take Picture Successfully.", Toast.LENGTH_LONG).show();
                        break;

                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                dialogInterface.dismiss();
            }
        });
        AlertDialog d = builder.create();
        d.show();
    }

    private void showKonfirmasiToggle(String msg, final ToggleButton tg, final Boolean b) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(AppsActivity.this);
        builder.setTitle("Confirmation");
        builder.setMessage(msg);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (tg.getId()) {
                    case R.id.btnAppsLock:
                        if(b){
                            createJsonObjectToggle(btnAppsLock, false, R.drawable.ic_horn_hijau, R.color.colorHijau, "statusdoor");
                        }
                        else{
                            createJsonObjectToggle(btnAppsLock, true, R.drawable.ic_horn_hitam, R.color.colorTulisan, "statusdoor");
                        }
                        break;

                    case R.id.btnAppsLights:
                        if(b){
                            createJsonObjectToggle(btnAppsLights, false, R.drawable.ic_lamp_hijau, R.color.colorHijau, "statuslampu");
                        }
                        else{
                            createJsonObjectToggle(btnAppsLights, true, R.drawable.ic_lamp_hitam, R.color.colorTulisan, "statuslampu");
                        }
                        break;

                    case R.id.btnAppsEngine:
                        if(b){
                            createJsonObjectToggle(btnAppsEngine, false, R.drawable.ic_power_hijau, R.color.colorHijau, "statusengine");
                        }
                        else{
                            createJsonObjectToggle(btnAppsEngine, true, R.drawable.ic_power_hitam, R.color.colorTulisan, "statusengine");
                        }
                        break;

                    case R.id.btnAppsGpss:
                        if(b){
                            createJsonObjectToggle(btnAppsGps, true, R.drawable.ic_gps_hijau, R.color.colorHijau, "statusgps");
                        }
                        else{
                            createJsonObjectToggle(btnAppsGps, false, R.drawable.ic_gps_hitam, R.color.colorTulisan, "statusgps");
                        }
                        break;
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                dialogInterface.dismiss();
            }
        });
        AlertDialog d = builder.create();
        d.show();
    }

    private void createJsonObjectToggle(ToggleButton tg, Boolean b, int drawable, int color, String event) {
        JSONObject objek = new JSONObject();
        try {
            objek.put("msg", b);
            objek.put("idmobil", isIdMobil);
        }catch (JSONException e) {
            return;
        }
        tg.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(drawable), null, null);
        tg.setTextColor(getResources().getColor(color));
        mSocket.emit(event, objek);
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

    private View.OnTouchListener oStartEngine = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int aksi = motionEvent.getAction();
            if(aksi == MotionEvent.ACTION_DOWN){
                if (!btnAppsEngine.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Engine still OFF, turn ON the Power!.", Toast.LENGTH_LONG).show();
                }
                else {
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("msg", false);
                        obj1.put("idmobil", isIdMobil);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mSocket.emit("statusalarm", obj1);
                }
            }
            else if(aksi == MotionEvent.ACTION_UP){
                Log.w("PRESS UP", String.valueOf(aksi));
                JSONObject obj1 = new JSONObject();
                try {
                    obj1.put("msg", true);
                    obj1.put("idmobil", isIdMobil);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("statusalarm", obj1);
            }
            return false;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
    }

    private void backupCoding() {
        //        btnDriver.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Retrofit retro = new Retrofit.Builder().baseUrl(Aplikasi.URL_HOST)
//                        .addConverterFactory(GsonConverterFactory.create()).build();
//                ApiService service = retro.create(ApiService.class);
//                Call<List<DriverModel>> call = service.getDriver(isIdMobil);
//                call.enqueue(new Callback<List<DriverModel>>() {
//                    @Override
//                    public void onResponse(Call<List<DriverModel>> call, Response<List<DriverModel>> response) {
//                        if(response.isSuccessful()) {
//
//                            List<DriverModel> dataDriver = response.body();
//
//                            final Dialog dialog = new Dialog(context);
//                            dialog.setContentView(R.layout.custom_driver);
//
//                            imgAppsDriver = (ImageView) dialog.findViewById(R.id.imageViewDrivers);
//                            txtDrivers = (TextView) dialog.findViewById(R.id.textViewDrivers);
//
//                            String urlFoto = dataDriver.get(dataDriver.size()-1).getGambarFull();
//                            new ImageLoaderClass().execute(urlFoto);
//
//                            txtDrivers.setText(dataDriver.get(dataDriver.size()-1).getTanggal());
//
//                            Button btnClose = (Button) dialog.findViewById(R.id.buttonCloseDialog);
//                            btnClose.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    dialog.dismiss();
//                                }
//                            });
//
//                            btnDriver.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_driver_hijau), null, null);
//                            btnDriver.setTextColor(getResources().getColor(R.color.colorHijau));
//
//                            dialog.show();
//                        }
//                        else{
//                            Log.d("onResponse", "Response Gagal");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<DriverModel>> call, Throwable t) {
//
//                    }
//                });
//            }
//        });
    }

}
