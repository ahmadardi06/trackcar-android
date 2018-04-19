package com.ansyah.ardi.trackcar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ansyah.ardi.trackcar.Adapter.LogsAdapter;
import com.ansyah.ardi.trackcar.Api.ApiService;
import com.ansyah.ardi.trackcar.Config.Aplikasi;
import com.ansyah.ardi.trackcar.Model.LogsModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HistorylogsActivity extends AppCompatActivity {

    List<LogsModel> logsModels = new ArrayList<>();
    ArrayList<Integer> gambarnya;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historylogs);
        setTitle("Logs Activity");

        mRecyclerView   = (RecyclerView) findViewById(R.id.recycler_viewLogs);
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
        Call<ArrayList<LogsModel>> call = service.getLogsData(Aplikasi.ID_MOBIL);
        call.enqueue(new Callback<ArrayList<LogsModel>>() {
            @Override
            public void onResponse(Call<ArrayList<LogsModel>> call, Response<ArrayList<LogsModel>> response) {
                if(response.isSuccessful()){
                    logsModels = response.body();
                    gambarnya = new ArrayList<Integer>();
                    for (int i=0; i<logsModels.size(); i++){
                        if(logsModels.get(i).getLogs().getJenis().equals("lamp")){
                            gambarnya.add(R.drawable.ic_lamp_on);
                        }
                        else if(logsModels.get(i).getLogs().getJenis().equals("engine")){
                            gambarnya.add(R.drawable.ic_flash_on_hijau);
                        }
                        else if(logsModels.get(i).getLogs().getJenis().equals("door")){
                            gambarnya.add(R.drawable.ic_lock_open_on);
                        }
                        else if(logsModels.get(i).getLogs().getJenis().equals("alarm")){
                            gambarnya.add(R.drawable.ic_access_alarm_hijau);
                        }
                        else if(logsModels.get(i).getLogs().getJenis().equals("gps")){
                            gambarnya.add(R.drawable.ic_my_location_hijau);
                        }
                        else{
                            gambarnya.add(R.drawable.ic_linked_camera_hijau);
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
