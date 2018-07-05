package com.ansyah.ardi.trackcar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ansyah.ardi.trackcar.Adapter.DriversAdapter;
import com.ansyah.ardi.trackcar.Adapter.LogsAdapter;
import com.ansyah.ardi.trackcar.Api.ApiService;
import com.ansyah.ardi.trackcar.Config.Aplikasi;
import com.ansyah.ardi.trackcar.Config.Utils;
import com.ansyah.ardi.trackcar.Model.DriversModel;
import com.ansyah.ardi.trackcar.Model.DriversObjek;
import com.ansyah.ardi.trackcar.Model.LogsModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DriverActivity extends AppCompatActivity {

    ArrayList<DriversObjek> logsModels;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    String isIdMobil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        setTitle("History of Drivers");

        isIdMobil = String.valueOf(Utils.readSharedSetting(this, MainActivity.PREF_USER_ID_MOBIL, ""));

        mRecyclerView = (RecyclerView) findViewById(R.id.recylcerDrivers);
        mLayoutManager  = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        prepareDataLogs();
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
//                    Log.d("RESP", logsModels.get(0).getFullGambar());
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