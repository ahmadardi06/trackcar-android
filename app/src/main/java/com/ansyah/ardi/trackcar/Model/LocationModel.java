package com.ansyah.ardi.trackcar.Model;

/**
 * Created by ardi on 18/04/18.
 */

public class LocationModel {
    private double latitude, longitude;
    private String _id, tanggal;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String get_id() {
        return _id;
    }

    public String getTanggal() {
        return tanggal;
    }
}
