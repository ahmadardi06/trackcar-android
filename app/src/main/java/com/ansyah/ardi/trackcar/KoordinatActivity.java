package com.ansyah.ardi.trackcar;

import android.app.DatePickerDialog;
import android.content.Context;
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

import com.ansyah.ardi.trackcar.Adapter.DriversAdapter;
import com.ansyah.ardi.trackcar.Adapter.KoordinatAdapter;
import com.ansyah.ardi.trackcar.Api.ApiService;
import com.ansyah.ardi.trackcar.Config.Aplikasi;
import com.ansyah.ardi.trackcar.Config.Utils;
import com.ansyah.ardi.trackcar.Model.DriversModel;
import com.ansyah.ardi.trackcar.Model.DriversObjek;
import com.ansyah.ardi.trackcar.Model.KoordinatModel;
import com.ansyah.ardi.trackcar.Model.KoordinatObjek;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    EditText editSearchKoordinat;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_koordinat);
        setTitle("History of Locations");

        isIdMobil = String.valueOf(Utils.readSharedSetting(this, MainActivity.PREF_USER_ID_MOBIL, ""));
        editSearchKoordinat = (EditText) findViewById(R.id.editSearchKoordinat);
        editSearchKoordinat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendarNow = Calendar.getInstance();
                int year = calendarNow.get(Calendar.YEAR);
                int mont = calendarNow.get(Calendar.MONTH);
                int tgll = calendarNow.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(KoordinatActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                        editSearchKoordinat.setText(newTanggal.get(Calendar.YEAR)+"-"+bulannya+"-"+newTanggal.get(Calendar.DAY_OF_MONTH));
                    }
                }, year, mont, tgll);
                datePickerDialog.show();
            }
        });

        editSearchKoordinat.addTextChangedListener(onTextWatcherSearchKoordinat);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerKoordinat);
        mLayoutManager  = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        prepareDataLogs();
    }

    protected TextWatcher onTextWatcherSearchKoordinat = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            try {
                filterKoordinat(editable.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    private void filterKoordinat(String s) throws ParseException {
        ArrayList<KoordinatObjek> arrayDriver = new ArrayList<KoordinatObjek>();
        for (KoordinatObjek item : logsModels) {
            Date d1 = dateFormat.parse(item.getTanggal());
            Date d2 = dateFormat.parse(s);
            if(d1.equals(d2)){
                arrayDriver.add(item);
            }
        }

        mRecyclerView.setAdapter(new KoordinatAdapter(context, arrayDriver));
        mRecyclerView.getAdapter().notifyDataSetChanged();
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
