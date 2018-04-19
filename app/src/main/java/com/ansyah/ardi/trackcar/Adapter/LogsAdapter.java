package com.ansyah.ardi.trackcar.Adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ansyah.ardi.trackcar.Model.LogsModel;
import com.ansyah.ardi.trackcar.Model.LogsObjek;
import com.ansyah.ardi.trackcar.R;

import java.util.ArrayList;
import java.util.List;

import static com.ansyah.ardi.trackcar.R.*;
import static com.ansyah.ardi.trackcar.R.drawable.ic_lamp_on;

/**
 * Created by ardi on 19/04/18.
 */

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.MyViewHolder> {

    private List<LogsModel> logsData;
    private ArrayList<Integer> gambare;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout.custom_logs, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LogsModel logModel = logsData.get(position);
        holder.jenis.setText(logModel.getLogs().getJenis());
        holder.keterangan.setText(logModel.getLogs().getKeterangan());
        holder.tanggal.setText(logModel.getLogs().getTanggal());
        holder.gambar.setImageResource((Integer) gambare.get(position));
    }

    @Override
    public int getItemCount() {
        return logsData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView jenis, keterangan, tanggal;
        public ImageView gambar;

        public MyViewHolder(View itemView) {
            super(itemView);
            jenis = (TextView) itemView.findViewById(id.txtJenisLogs);
            keterangan = (TextView) itemView.findViewById(id.txtKeteranganLogs);
            tanggal = (TextView) itemView.findViewById(id.txtTanggal);
            gambar = (ImageView) itemView.findViewById(id.gmbLogs);
        }
    }

    public LogsAdapter(List<LogsModel> logsModel, ArrayList<Integer> gambare) {
        this.logsData = logsModel;
        this.gambare = gambare;
    }
}
