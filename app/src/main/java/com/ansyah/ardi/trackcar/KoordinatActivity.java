package com.ansyah.ardi.trackcar;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ansyah.ardi.trackcar.Adapter.DriversAdapter;
import com.ansyah.ardi.trackcar.Adapter.KoordinatAdapter;
import com.ansyah.ardi.trackcar.Api.ApiService;
import com.ansyah.ardi.trackcar.Config.Aplikasi;
import com.ansyah.ardi.trackcar.Config.Utils;
import com.ansyah.ardi.trackcar.Model.DriversModel;
import com.ansyah.ardi.trackcar.Model.DriversObjek;
import com.ansyah.ardi.trackcar.Model.KoordinatModel;
import com.ansyah.ardi.trackcar.Model.KoordinatObjek;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KoordinatActivity extends AppCompatActivity {

    private Context context = this;
    ArrayList<KoordinatObjek> logsModels;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    String isIdMobil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_koordinat);
        setTitle("History of Locations");

        isIdMobil = String.valueOf(Utils.readSharedSetting(this, MainActivity.PREF_USER_ID_MOBIL, ""));

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerKoordinat);
        mLayoutManager  = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        prepareDataLogs();
    }

    private void prepareDataLogs() {
        Retrofit retro = new Retrofit.Builder().baseUrl(Aplikasi.URL_HOST)
                .addConverterFactory(GsonConverterFactory.create()).build();
        ApiService service = retro.create(ApiService.class);
        Call<KoordinatModel> call = service.getAllKoordinat(isIdMobil);
        call.enqueue(new Callback<KoordinatModel>() {
            @Override
            public void onResponse(Call<KoordinatModel> call, Response<KoordinatModel> response) {
                if(response.isSuccessful()){
                    logsModels = response.body().getKoordinat();
//                    Log.d("RESP", logsModels.get(0).getFullGambar());
                    mRecyclerView.setAdapter(new KoordinatAdapter(context, logsModels));
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<KoordinatModel> call, Throwable t) {

            }

        });
    }
}
