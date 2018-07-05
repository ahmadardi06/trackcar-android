package com.ansyah.ardi.trackcar.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ansyah.ardi.trackcar.Config.DateParser;
import com.ansyah.ardi.trackcar.Model.DriversModel;
import com.ansyah.ardi.trackcar.Model.DriversObjek;
import com.ansyah.ardi.trackcar.Model.LogsModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.ansyah.ardi.trackcar.R.id;
import static com.ansyah.ardi.trackcar.R.layout;

/**
 * Created by ardi on 19/04/18.
 */

public class DriversAdapter extends RecyclerView.Adapter<DriversAdapter.MyViewHolder> {

    private ArrayList<DriversObjek> driversData;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout.custom_drivers, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DriversObjek driversModel = driversData.get(position);
//        holder._id.setText(driversModel.get_id());
        holder.tanggal.setText(DateParser.parseDateToDayDateMonthYear(driversModel.getTanggal()));
//        holder.gambar.setImageBitmap();
        new ImageLoaderClass(holder.gambar).execute(driversModel.getFullGambar());
    }

    @Override
    public int getItemCount() {
        return driversData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tanggal, _id;
        public ImageView gambar;

        public MyViewHolder(View itemView) {
            super(itemView);
//            _id = (TextView) itemView.findViewById(id.txtIdDriver);
            tanggal = (TextView) itemView.findViewById(id.txtTanggalDriver);
            gambar = (ImageView) itemView.findViewById(id.txtGambarDriver);
        }
    }

    public DriversAdapter(ArrayList<DriversObjek> driversModel) {
        this.driversData = driversModel;
    }

    private class ImageLoaderClass extends AsyncTask<String, String, Bitmap>{
        ImageView imgViewDriver;

        public ImageLoaderClass(ImageView imgViewDriver) {
            this.imgViewDriver = imgViewDriver;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap imageBitmap = null;
            try {
                imageBitmap = BitmapFactory.decodeStream((InputStream) new URL(strings[0]).getContent());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return imageBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imgViewDriver.setImageBitmap(bitmap);
        }
    };
}
