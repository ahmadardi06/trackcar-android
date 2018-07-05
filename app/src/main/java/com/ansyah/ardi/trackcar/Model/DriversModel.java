package com.ansyah.ardi.trackcar.Model;

import java.util.ArrayList;

/**
 * Created by ardi on 19/04/18.
 */

public class DriversModel {
    private String _id;
    private ArrayList<DriversObjek> driver;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public ArrayList<DriversObjek> getDriver() {
        return driver;
    }

    public void setDriver(ArrayList<DriversObjek> driver) {
        this.driver = driver;
    }
}
