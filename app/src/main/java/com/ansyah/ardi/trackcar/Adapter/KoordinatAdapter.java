package com.ansyah.ardi.trackcar.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ansyah.ardi.trackcar.Config.DateParser;
import com.ansyah.ardi.trackcar.MapsActivity;
import com.ansyah.ardi.trackcar.Model.KoordinatObjek;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.ansyah.ardi.trackcar.R.id;
import static com.ansyah.ardi.trackcar.R.layout;

/**
 * Created by ardi on 19/04/18.
 */

public class KoordinatAdapter extends RecyclerView.Adapter<KoordinatAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<KoordinatObjek> driversData;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout.custom_koordinat, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final KoordinatObjek driversModel = driversData.get(position);
        String lat = "Latitude: " + Double.toString(driversModel.getLatitude());
        String lon = "Longitude: " + Double.toString(driversModel.getLongitude());

        holder.tanggal.setText(DateParser.parseDateToDayDateMonthYear(driversModel.getTanggal()));
        holder.latitude.setText(lat);
        holder.longitude.setText(lon);
        holder.btnKoordinatMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("latitude", driversModel.getLatitude());
                intent.putExtra("longitude", driversModel.getLongitude());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return driversData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tanggal, _id, latitude, longitude;
        public Button btnKoordinatMaps;

        public MyViewHolder(View itemView) {
            super(itemView);
            tanggal     = (TextView) itemView.findViewById(id.txtKoordinatTanggal);
            latitude    = (TextView) itemView.findViewById(id.txtKoordinatLatitude);
            longitude   = (TextView) itemView.findViewById(id.txtKoordinatLongitude);
            btnKoordinatMaps = (Button) itemView.findViewById(id.btnKoordinatMaps);
        }

    }

    public KoordinatAdapter(Context context, ArrayList<KoordinatObjek> driversModel) {
        this.driversData = driversModel;
        this.context = context;
    }
}
