package com.ansyah.ardi.trackcar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.ansyah.ardi.trackcar.Adapter.DriversAdapter;
import com.ansyah.ardi.trackcar.Adapter.KoordinatAdapter;
import com.ansyah.ardi.trackcar.Api.ApiService;
import com.ansyah.ardi.trackcar.Config.Aplikasi;
import com.ansyah.ardi.trackcar.Config.Utils;
import com.ansyah.ardi.trackcar.Model.DriversObjek;
import com.ansyah.ardi.trackcar.Model.KoordinatModel;
import com.ansyah.ardi.trackcar.Model.KoordinatObjek;
import com.ansyah.ardi.trackcar.Model.LocationModel;
import com.ansyah.ardi.trackcar.Model.RelayModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    String isIdMobil;
    private double latitude, longitude;
    EditText editSearchKoordinatMap;
    DatePickerDialog dtDate;
    ArrayList<KoordinatObjek> koordinatObjek;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    ArrayList<LatLng> arrayLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setTitle("Location");

        isIdMobil = String.valueOf(Utils.readSharedSetting(MapsActivity.this, MainActivity.PREF_USER_ID_MOBIL, ""));
        editSearchKoordinatMap = (EditText) findViewById(R.id.editSearchKoordinatMap);
        editSearchKoordinatMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar nowDate = Calendar.getInstance();
                int year = nowDate.get(Calendar.YEAR);
                int mont = nowDate.get(Calendar.MONTH);
                int tgll = nowDate.get(Calendar.DAY_OF_MONTH);
                dtDate = new DatePickerDialog(MapsActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                        editSearchKoordinatMap.setText(newTanggal.get(Calendar.YEAR)+"-"+bulannya+"-"+newTanggal.get(Calendar.DAY_OF_MONTH));
                    }
                }, year, mont, tgll);
                dtDate.show();
            }
        });
        editSearchKoordinatMap.addTextChangedListener(onTextSearchKoordinatMap);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);

        preDataFirst();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_marker_mobil_1);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);

        LatLng posisiNow = new LatLng(latitude, longitude);
        MarkerOptions marker =  new MarkerOptions().position(posisiNow).title("Your Car");
        marker.icon(markerIcon);

        mMap.addMarker(marker);
        enableMyLocationIfPermitted();

        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        arrayLatLng = new ArrayList<LatLng>();
        arrayLatLng.add(posisiNow);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(arrayLatLng.get(0), 16));
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private TextWatcher onTextSearchKoordinatMap = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            try {
                filterTanggalKoordinatMap(editable.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    private void filterTanggalKoordinatMap(String s) throws ParseException {
        mMap.clear();

        arrayLatLng = new ArrayList<LatLng>();
        for (KoordinatObjek item : koordinatObjek) {
            Date d1 = dateFormat.parse(item.getTanggal());
            Date d2 = dateFormat.parse(s);
            if(d1.equals(d2)){
                LatLng latLng = new LatLng(item.getLatitude(), item.getLongitude());
                arrayLatLng.add(latLng);
            }
        }

        Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_marker_mobil_1);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);

        for(int i = 0 ; i < arrayLatLng.size() ; i++) {
            createMarker(arrayLatLng.get(i), "Your Car", markerIcon);
        }
        mMap.addPolyline((new PolylineOptions()).clickable(true).width(25).color(Color.BLUE).addAll(arrayLatLng));
    };

    protected Marker createMarker(LatLng latLng, String title, BitmapDescriptor bitmapDescriptor) {
        return mMap.addMarker(new MarkerOptions().position(latLng).title(title)
                .icon(bitmapDescriptor));
    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, showing default location", Toast.LENGTH_SHORT).show();
        LatLng redmond = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(redmond));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationIfPermitted();
                } else {
                    showDefaultLocation();
                }
                return;
            }

        }
    }

    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
        new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mMap.setMinZoomPreference(15);
                return false;
            }
        };

    private void preDataFirst() {
        Retrofit retro = new Retrofit.Builder().baseUrl(Aplikasi.URL_HOST)
                .addConverterFactory(GsonConverterFactory.create()).build();
        ApiService service = retro.create(ApiService.class);
        Call<KoordinatModel> call = service.getAllKoordinat(isIdMobil);
        call.enqueue(new Callback<KoordinatModel>() {
            @Override
            public void onResponse(Call<KoordinatModel> call, Response<KoordinatModel> response) {
                if(response.isSuccessful()){
                    koordinatObjek = response.body().getKoordinat();
                }
            }

            @Override
            public void onFailure(Call<KoordinatModel> call, Throwable t) {

            }

        });
    }
}
