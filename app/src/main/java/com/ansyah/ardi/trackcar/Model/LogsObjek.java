package com.ansyah.ardi.trackcar.Model;

/**
 * Created by ardi on 19/04/18.
 */

public class LogsObjek {
    private String jenis, keterangan, tanggal;

    public LogsObjek(String jenis, String keterangan, String tanggal) {
        this.jenis = jenis;
        this.keterangan = keterangan;
        this.tanggal = tanggal;
    }

    public String getJenis() {
        return jenis;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getTanggal() {
        return tanggal;
    }
}
