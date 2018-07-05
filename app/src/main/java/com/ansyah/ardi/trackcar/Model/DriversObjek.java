package com.ansyah.ardi.trackcar.Model;

/**
 * Created by ardi on 19/04/18.
 */

public class DriversObjek {
    private String gambar, _id, tanggal;

    public DriversObjek(String gambar, String _id, String tanggal) {
        this.gambar = gambar;
        this._id = _id;
        this.tanggal = tanggal;
    }

    public String getGambar() {
        return gambar;
    }

    public String getFullGambar() { return "https://trackcar.herokuapp.com/images/driver/"+gambar; }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
