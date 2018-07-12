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
import org.w3c.dom.Text;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ToggleButton btnGpsMain;
    ImageView imgRespon;
    ImageButton btnAppsKiri, btnAppsKanan;
    Button btnLogoutMain, btnInfoMain, btnCaptureMain;
    TextView txtConnected;

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

        mSocket.on("statusgps", onStatusGps);
        mSocket.on("broadcastMobil", onStatusConnection);
        mSocket.connect();

        btnGpsMain      = (ToggleButton) findViewById(R.id.btnGpsMain);
        btnCaptureMain  = (Button) findViewById(R.id.btnCaptureMain);
        btnInfoMain     = (Button) findViewById(R.id.btnInfoMain);

        txtConnected = (TextView) findViewById(R.id.txtConnected);

        cekStatusRelay();

        btnLogoutMain = (Button) findViewById(R.id.btnLogoutMain);
        btnLogoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showKonfirmasiButton("Are you sure to logout?", btnLogoutMain);
            }
        });

        btnCaptureMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showKonfirmasiButton("Are you sure to capture?", btnCaptureMain);
            }
        });

        btnInfoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
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

                    case R.id.btnCaptureMain:
                        String sekarang = new SimpleDateFormat("ddMMyyyy-HH-mm").format(Calendar.getInstance().getTime());
                        JSONObject objek = new JSONObject();
                        try{
                            objek.put("msg", "takefoto"+sekarang);
                            objek.put("idmobil", isIdMobil);
                        }catch (JSONException e){
                            return;
                        }
                        btnCaptureMain.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_camera_hijau), null,null);
                        btnCaptureMain.setTextColor(getResources().getColor(R.color.colorHijau));
                        mSocket.emit("takefoto", objek);
                        Toast.makeText(MainActivity.this, "Take Picture Successfully.", Toast.LENGTH_LONG).show();
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
                    case R.id.btnGpsMain:
                        if(b){
                            createJsonObjectToggle(btnGpsMain, true, R.drawable.ic_gps_hijau, R.color.colorHijau, "statusgps");
                        }
                        else {
                            createJsonObjectToggle(btnGpsMain, false, R.drawable.ic_gps_hitam, R.color.colorTulisan, "statusgps");
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
                    btnGpsMain = (ToggleButton) findViewById(R.id.btnGpsMain);
                    if(response.body().getGps()){
                        setOnOffToggle(btnGpsMain, R.drawable.ic_gps_hijau, R.color.colorHijau, true, "GPS");
                    }else{
                        setOnOffToggle(btnGpsMain, R.drawable.ic_gps_hitam, R.color.colorTulisan, false, "GPS");
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

    private Emitter.Listener onStatusGps = new Emitter.Listener() {
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
                    Log.d("status gps", msg);
                }
            });
        }
    };

    private Emitter.Listener onStatusConnection = new Emitter.Listener() {
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

                    if(!msg.isEmpty()) {
                        txtConnected.setText(msg);
                    }
                    else{
                        txtConnected.setText("connecting ...");
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
