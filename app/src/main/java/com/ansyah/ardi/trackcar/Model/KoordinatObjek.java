package com.ansyah.ardi.trackcar.Model;

/**
 * Created by ardi on 05/07/18.
 */

public class KoordinatObjek {
    public String tanggal, _id;
    public double latitude, longitude;

    public KoordinatObjek(String tanggal, String _id, double latitude, double longitude) {
        this.tanggal = tanggal;
        this._id = _id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
