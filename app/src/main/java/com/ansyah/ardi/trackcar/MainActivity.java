package com.ansyah.ardi.trackcar;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ansyah.ardi.trackcar.Api.ApiService;
import com.ansyah.ardi.trackcar.Config.Aplikasi;
import com.ansyah.ardi.trackcar.Config.Utils;
import com.ansyah.ardi.trackcar.Model.DriverModel;
import com.ansyah.ardi.trackcar.Model.RelayModel;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ToggleButton btnLocking, btnLights;
    ImageView imgRespon;
    ImageButton btnAppsKiri, btnAppsKanan;

    public static final String PREF_USER_FIRST_TIME = "user_first_time";
    boolean isUserFirstTime;
    String[] colors = {"#96CC7A", "#EA705D", "#66BBCC"};

    private Socket mSocket;
    {
        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.query = "idMobil=" + Aplikasi.ID_MOBIL;
            mSocket = IO.socket(Aplikasi.URL_HOST, opts);
        } catch (URISyntaxException e) {}
    }

    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isUserFirstTime = Boolean.valueOf(Utils.readSharedSetting(MainActivity.this, PREF_USER_FIRST_TIME, "true"));

        Intent introIntent = new Intent(MainActivity.this, PagerActivity.class);
        introIntent.putExtra(PREF_USER_FIRST_TIME, isUserFirstTime);

        if (isUserFirstTime)
            startActivity(introIntent);

        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        mSocket.on("statuslampu", onStatusLampu);
        mSocket.on("statusdoor", onStatusDoor);
        mSocket.connect();

        Retrofit retro = new Retrofit.Builder().baseUrl(Aplikasi.URL_HOST)
                .addConverterFactory(GsonConverterFactory.create()).build();
        ApiService service = retro.create(ApiService.class);
        Call<RelayModel> call = service.getRelay(Aplikasi.ID_MOBIL);
        call.enqueue(new Callback<RelayModel>() {
            @Override
            public void onResponse(Call<RelayModel> call, Response<RelayModel> response) {
                if(response.isSuccessful()){
                    btnLocking = (ToggleButton) findViewById(R.id.btnLocking);
                    if(response.body().getDoor()){
                        btnLocking.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lock_outline_hijau), null, null);
                        btnLocking.setTextColor(getResources().getColor(R.color.colorHijau));
                    }else{
                        btnLocking.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lock_open_black_24dp), null, null);
                        btnLocking.setTextColor(getResources().getColor(R.color.colorTulisan));
                    }

                    btnLights = (ToggleButton) findViewById(R.id.btnLights);
                    if(response.body().getLamp()){
                        btnLights.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lamp_on), null, null);
                        btnLights.setTextColor(getResources().getColor(R.color.colorHijau));
                    }else{
                        btnLights.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lamp_black_24dp), null, null);
                        btnLights.setTextColor(getResources().getColor(R.color.colorTulisan));
                    }
                }
            }

            @Override
            public void onFailure(Call<RelayModel> call, Throwable t) {

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
                Intent frm  = new Intent(MainActivity.this, HistorylogsActivity.class);
                startActivity(frm);
            }
        });

        btnLocking = (ToggleButton) findViewById(R.id.btnLocking);
        btnLocking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("msg", true);
                        obj1.put("idmobil", Aplikasi.ID_MOBIL);
                    }catch (JSONException e) {
                        return;
                    }
                    btnLocking.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lock_outline_hijau), null, null);
                    btnLocking.setTextColor(getResources().getColor(R.color.colorHijau));
                    mSocket.emit("statusdoor", obj1);
                }
                else {
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("msg", false);
                        obj1.put("idmobil", Aplikasi.ID_MOBIL);
                    }catch (JSONException e) {
                        return;
                    }
                    btnLocking.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lock_open_black_24dp), null, null);
                    btnLocking.setTextColor(getResources().getColor(R.color.colorTulisan));
                    mSocket.emit("statusdoor", obj1);
                }
            }
        });

        btnLights = (ToggleButton) findViewById(R.id.btnLights);
        btnLights.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
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
                else {
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
//                    btnLights = (ToggleButton) findViewById(R.id.btnLights);
                    if(msg == "true"){
//                        btnLights.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lamp_on), null, null);
//                        btnLights.setTextColor(getResources().getColor(R.color.colorHijau));
                        imgRespon.setImageResource(R.drawable.kondisilampuon);
                    }
                    else{
//                        btnLights.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lamp_black_24dp), null, null);
//                        btnLights.setTextColor(getResources().getColor(R.color.colorTulisan));
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
//                    btnLocking = (ToggleButton) findViewById(R.id.btnLocking);
                    if(msg == "true"){
//                        btnLocking.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lock_outline_hijau), null, null);
//                        btnLocking.setTextColor(getResources().getColor(R.color.colorHijau));
                        imgRespon.setImageResource(R.drawable.kondisireadyoff);
                    }
                    else{
//                        btnLocking.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_lock_open_black_24dp), null, null);
//                        btnLocking.setTextColor(getResources().getColor(R.color.colorTulisan));
                        imgRespon.setImageResource(R.drawable.kondisireadyon);
                    }
                }
            });
        }
    };
}
