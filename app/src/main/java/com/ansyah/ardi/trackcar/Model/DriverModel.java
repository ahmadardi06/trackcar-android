package com.ansyah.ardi.trackcar.Model;

/**
 * Created by ardi on 18/04/18.
 */

public class DriverModel {
    private String _id, gambar, tanggal;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getGambarFull(){
        return "https://trackcar.herokuapp.com/images/driver/"+gambar;
    }
}
