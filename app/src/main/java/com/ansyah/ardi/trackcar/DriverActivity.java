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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.ansyah.ardi.trackcar.Adapter.DriversAdapter;
import com.ansyah.ardi.trackcar.Adapter.LogsAdapter;
import com.ansyah.ardi.trackcar.Api.ApiService;
import com.ansyah.ardi.trackcar.Config.Aplikasi;
import com.ansyah.ardi.trackcar.Config.Utils;
import com.ansyah.ardi.trackcar.Model.DriversModel;
import com.ansyah.ardi.trackcar.Model.DriversObjek;
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

public class DriverActivity extends AppCompatActivity {

    ArrayList<DriversObjek> logsModels;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    String isIdMobil;
    EditText editSearchDriver;
    Calendar nowDate;
    DatePickerDialog dtDate;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        setTitle("History of Drivers");

        isIdMobil = String.valueOf(Utils.readSharedSetting(this, MainActivity.PREF_USER_ID_MOBIL, ""));
        editSearchDriver = (EditText) findViewById(R.id.editSearchDriver);
        editSearchDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nowDate = Calendar.getInstance();
                int year = nowDate.get(Calendar.YEAR);
                int mont = nowDate.get(Calendar.MONTH);
                int tgll = nowDate.get(Calendar.DAY_OF_MONTH);
                dtDate = new DatePickerDialog(DriverActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                        editSearchDriver.setText(newTanggal.get(Calendar.YEAR)+"-"+bulannya+"-"+newTanggal.get(Calendar.DAY_OF_MONTH));
                        editSearchDriver.addTextChangedListener(onTextWatcherSearchDriver);
                    }
                }, year, mont, tgll);
                dtDate.show();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recylcerDrivers);
        mLayoutManager  = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        prepareDataLogs();
    }

    protected TextWatcher onTextWatcherSearchDriver = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            try {
                filterTanggal(editable.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    private void filterTanggal(String s) throws ParseException {
        ArrayList<DriversObjek> arrayDriver = new ArrayList<DriversObjek>();
        for (DriversObjek item : logsModels) {
            Date d1 = dateFormat.parse(item.getTanggal());
            Date d2 = dateFormat.parse(s);
//            Log.d("DD1", d1.toString());
//            Log.d("DD2", d2.toString());
            if(d1.equals(d2)){
                arrayDriver.add(item);
            }
        }

        mRecyclerView.setAdapter(new DriversAdapter(arrayDriver));
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    protected void prepareDataLogs(){
        Retrofit retro = new Retrofit.Builder().baseUrl(Aplikasi.URL_HOST)
                .addConverterFactory(GsonConverterFactory.create()).build();
        ApiService service = retro.create(ApiService.class);
        Call<DriversModel> call = service.getDrivers(isIdMobil);
        call.enqueue(new Callback<DriversModel>() {
            @Override
            public void onResponse(Call<DriversModel> call, Response<DriversModel> response) {
                if(response.isSuccessful()){
                    logsModels = response.body().getDriver();
                    Log.d("RESP", logsModels.get(0).getTanggal());
                    mRecyclerView.setAdapter(new DriversAdapter(logsModels));
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<DriversModel> call, Throwable t) {

            }

        });

    }
}