package com.ansyah.ardi.trackcar;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.ansyah.ardi.trackcar.Adapter.LogsAdapter;
import com.ansyah.ardi.trackcar.Api.ApiService;
import com.ansyah.ardi.trackcar.Config.Aplikasi;
import com.ansyah.ardi.trackcar.Config.Utils;
import com.ansyah.ardi.trackcar.Model.LogsModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HistoryActivity extends AppCompatActivity {

    List<LogsModel> logsModels = new ArrayList<>();
    ArrayList<Integer> gambarnya;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    String isIdMobil;
    EditText editSearchLog;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historylogs);
        setTitle("Logs Activity");

        isIdMobil = String.valueOf(Utils.readSharedSetting(HistoryActivity.this, MainActivity.PREF_USER_ID_MOBIL, ""));

        editSearchLog = (EditText) findViewById(R.id.editSearchLog);
        editSearchLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar nowDate = Calendar.getInstance();
                int year = nowDate.get(Calendar.YEAR);
                int mont = nowDate.get(Calendar.MONTH);
                int tgll = nowDate.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(HistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Calendar newTanggal = Calendar.getInstance();
                        newTanggal.set(i, i1, i2);
                        String bulannya = "";
                        if(newTanggal.get(Calendar.MONTH) <= 9){
                            bulannya = "0"+String.valueOf(newTanggal.get(Calendar.MONTH)+1);
                        } else {
                            bulannya = String.valueOf(newTanggal.get(Calendar.MONTH)+1);
                        }
                        editSearchLog.setText(newTanggal.get(Calendar.YEAR)+"-"+bulannya+"-"+newTanggal.get(Calendar.DAY_OF_MONTH));
                    }
                }, year, mont, tgll);
                datePickerDialog.show();
            }
        });
        editSearchLog.addTextChangedListener(onTextWatcherLog);

        mRecyclerView   = (RecyclerView) findViewById(R.id.recycler_viewLogs);
        mLayoutManager  = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        prepareDataLogs();
    }

    protected TextWatcher onTextWatcherLog = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            try {
                filterKata(editable.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    private void filterKata(String s) throws ParseException {
        List<LogsModel> arrayLogModel = new ArrayList<LogsModel>();
        ArrayList<Integer> arrayGambar = new ArrayList<Integer>();
        int i = 0;
        for(LogsModel item : logsModels) {
            Date d1 = dateFormat.parse(item.getLogs().getTanggal());
            Date d2 = dateFormat.parse(s);
            if(d1.equals(d2)) {
                arrayLogModel.add(item);
                if(logsModels.get(i).getLogs().getJenis().equals("lamp")){
                    arrayGambar.add(R.drawable.ic_lamp_hijau);
                }
                else if(logsModels.get(i).getLogs().getJenis().equals("engine")){
                    arrayGambar.add(R.drawable.ic_power_hijau);
                }
                else if(logsModels.get(i).getLogs().getJenis().equals("door")){
                    arrayGambar.add(R.drawable.ic_lock_buka_hijau);
                }
                else if(logsModels.get(i).getLogs().getJenis().equals("alarm")){
                    arrayGambar.add(R.drawable.ic_alarm_hijau);
                }
                else if(logsModels.get(i).getLogs().getJenis().equals("gps")){
                    arrayGambar.add(R.drawable.ic_gps_hijau);
                }
                else{
                    arrayGambar.add(R.drawable.ic_camera_hijau);
                }
            }
            i++;
        }

        mRecyclerView.setAdapter(new LogsAdapter(arrayLogModel, arrayGambar));
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    protected void prepareDataLogs(){
        Retrofit retro = new Retrofit.Builder().baseUrl(Aplikasi.URL_HOST)
                .addConverterFactory(GsonConverterFactory.create()).build();
        ApiService service = retro.create(ApiService.class);
        Call<ArrayList<LogsModel>> call = service.getLogsData(isIdMobil);
        call.enqueue(new Callback<ArrayList<LogsModel>>() {
            @Override
            public void onResponse(Call<ArrayList<LogsModel>> call, Response<ArrayList<LogsModel>> response) {
                if(response.isSuccessful()){
                    logsModels = response.body();
                    gambarnya = new ArrayList<Integer>();
                    for (int i=0; i<logsModels.size(); i++){
                        if(logsModels.get(i).getLogs().getJenis().equals("lamp")){
                            gambarnya.add(R.drawable.ic_lamp_hijau);
                        }
                        else if(logsModels.get(i).getLogs().getJenis().equals("engine")){
                            gambarnya.add(R.drawable.ic_power_hijau);
                        }
                        else if(logsModels.get(i).getLogs().getJenis().equals("door")){
                            gambarnya.add(R.drawable.ic_lock_buka_hijau);
                        }
                        else if(logsModels.get(i).getLogs().getJenis().equals("alarm")){
                            gambarnya.add(R.drawable.ic_alarm_hijau);
                        }
                        else if(logsModels.get(i).getLogs().getJenis().equals("gps")){
                            gambarnya.add(R.drawable.ic_gps_hijau);
                        }
                        else{
                            gambarnya.add(R.drawable.ic_camera_hijau);
                        }
                    }
                    mRecyclerView.setAdapter(new LogsAdapter(logsModels, gambarnya));
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<LogsModel>> call, Throwable t) {

            }
        });
    }
}
