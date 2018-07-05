package com.ansyah.ardi.trackcar;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.net.URISyntaxException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ToggleButton btnLocking, btnLights, btnEngineMain;;
    ImageView imgRespon;
    ImageButton btnAppsKiri, btnAppsKanan;
    Button btnLogoutMain;

    public static final String PREF_USER_ID_MOBIL = "user_id_mobil";
    public String isIdMobil;
    private Socket mSocket;

    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isIdMobil = String.valueOf(Utils.readSharedSetting(this, PREF_USER_ID_MOBIL, ""));

        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.query = "idMobil=" + isIdMobil;
            mSocket = IO.socket(Aplikasi.URL_HOST, opts);
        } catch (URISyntaxException e) {}

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.putExtra(PREF_USER_ID_MOBIL, isIdMobil);

        if(isIdMobil.isEmpty())
            startActivity(loginIntent);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        mSocket.on("statuslampu", onStatusLampu);
        mSocket.on("statusdoor", onStatusDoor);
        mSocket.on("statusengine", onStatusEngine);
        mSocket.connect();

        btnLocking = (ToggleButton) findViewById(R.id.btnLocking);
        btnLights = (ToggleButton) findViewById(R.id.btnLights);
        btnEngineMain = (ToggleButton) findViewById(R.id.btnEngineMain);

        cekStatusRelay();

        btnLogoutMain = (Button) findViewById(R.id.btnLogoutMain);
        btnLogoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showKonfirmasiButton("Are you sure to logout?", btnLogoutMain);
            }
        });

        btnAppsKiri = (ImageButton) findViewById(R.id.btnAppKiri);
        btnAppsKiri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent frm  = new Intent(MainActivity.this, AppsActivity.class);
                startActivity(frm);
            }
        });

        btnAppsKanan = (ImageButton) findViewById(R.id.btnAppKanan);
        btnAppsKanan .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent frm  = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(frm);
            }
        });
    }

    private void showKonfirmasiButton(String message, final Button btn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirmation");
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (btn.getId()) {
                    case R.id.btnLogoutMain:
                        Utils.saveSharedSetting(MainActivity.this, PREF_USER_ID_MOBIL, "");
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirmation");
        builder.setMessage(msg);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (tg.getId()) {
                    case R.id.btnLocking:
                        if(b){
                            createJsonObjectToggle(btnLocking, true, R.drawable.ic_lock_tutup_hijau, R.color.colorHijau, "statusdoor");
                        }
                        else {
                            createJsonObjectToggle(btnLocking, false, R.drawable.ic_lock_buka_hitam, R.color.colorTulisan, "statusdoor");
                        }
                        break;

                    case R.id.btnLights:
                        if(b) {
                            createJsonObjectToggle(btnLights, true, R.drawable.ic_lamp_hijau, R.color.colorHijau, "statuslampu");
                        }
                        else {
                            createJsonObjectToggle(btnLights, false, R.drawable.ic_lamp_hitam, R.color.colorTulisan, "statuslampu");
                        }
                        break;

                    case R.id.btnEngineMain:
                        if(b) {
                            createJsonObjectToggle(btnEngineMain, true, R.drawable.ic_power_hijau, R.color.colorHijau, "statusengine");
                        }
                        else {
                            createJsonObjectToggle(btnEngineMain, false, R.drawable.ic_power_hitam, R.color.colorTulisan, "statusengine");
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

    private void cekStatusRelay() {
        Retrofit retro = new Retrofit.Builder().baseUrl(Aplikasi.URL_HOST)
                .addConverterFactory(GsonConverterFactory.create()).build();
        ApiService service = retro.create(ApiService.class);
        Call<RelayModel> call = service.getRelay(isIdMobil);
        call.enqueue(new Callback<RelayModel>() {
            @Override
            public void onResponse(Call<RelayModel> call, Response<RelayModel> response) {
                if(response.isSuccessful()){
                    btnLocking = (ToggleButton) findViewById(R.id.btnLocking);
                    if(response.body().getDoor()){
                        setOnOffToggle(btnLocking, R.drawable.ic_lock_tutup_hijau, R.color.colorHijau, true, "Lock");
                    }else{
                        setOnOffToggle(btnLocking, R.drawable.ic_lock_buka_hitam, R.color.colorTulisan, false, "Lock");
                    }

                    btnLights = (ToggleButton) findViewById(R.id.btnLights);
                    if(response.body().getLamp()){
                        setOnOffToggle(btnLights, R.drawable.ic_lamp_hijau, R.color.colorHijau, true, "Light");
                    }else{
                        setOnOffToggle(btnLights, R.drawable.ic_lamp_hitam, R.color.colorTulisan, false, "Light");
                    }

                    btnEngineMain = (ToggleButton) findViewById(R.id.btnEngineMain);
                    if(response.body().getEngine()){
                        setOnOffToggle(btnEngineMain, R.drawable.ic_power_hijau, R.color.colorHijau, true, "Power");
                    }else{
                        setOnOffToggle(btnEngineMain, R.drawable.ic_power_hitam, R.color.colorTulisan, false, "Power");
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
        tg.setTextOn(label);

        tg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showKonfirmasiToggle("Are you sure ?", tg, b);
            }
        });
    }

    private Emitter.Listener onStatusLampu = new Emitter.Listener() {
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
                    imgRespon = (ImageView) findViewById(R.id.imgAppRespon);
                    if(msg == "true"){
                        imgRespon.setImageResource(R.drawable.kondisilampuon);
                    }
                    else{
                        imgRespon.setImageResource(R.drawable.kondisireadyon);
                    }
                }
            });
        }
    };

    private Emitter.Listener onStatusDoor = new Emitter.Listener() {
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
                    imgRespon = (ImageView) findViewById(R.id.imgAppRespon);
                    if(msg == "true"){
                        imgRespon.setImageResource(R.drawable.kondisireadyoff);
                    }
                    else{
                        imgRespon.setImageResource(R.drawable.kondisireadyon);
                    }
                }
            });
        }
    };

    private Emitter.Listener onStatusEngine = new Emitter.Listener() {
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
                    imgRespon = (ImageView) findViewById(R.id.imgAppRespon);
                    if(msg == "true"){
                        imgRespon.setImageResource(R.drawable.kondisireadyoff);
                    }
                    else{
                        imgRespon.setImageResource(R.drawable.kondisireadyon);
                    }
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
    }

}
